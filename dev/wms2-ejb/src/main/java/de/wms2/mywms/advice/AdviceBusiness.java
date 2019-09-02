/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.advice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptEntityService;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.inventory.TrashHandler;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;

/**
 * @author krane
 *
 */
@Stateless
public class AdviceBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private GoodsReceiptEntityService goodsReceiptService;
	@Inject
	private Event<AdviceLineStateChangeEvent> adviceLineStateChangeEvent;
	@Inject
	private Event<AdviceStateChangeEvent> adviceStateChangeEvent;
	@Inject
	private AdviceEntityService adviceService;
	@Inject
	private TrashHandler trashHandler;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private GenericEntityService entitySerive;

	public void cleanupDeleted() throws BusinessException {
		String logStr = "cleanupDeleted ";
		logger.log(Level.FINE, logStr);

		List<Advice> orders = adviceService.readList(null, OrderState.DELETABLE, null, null, null);
		for (Advice order : orders) {
			trashHandler.removeAdvice(order);
		}
	}

	/**
	 * Finish the advice
	 * <p>
	 * The advice is finished.<br>
	 * StateChange events are fired.<br>
	 * No amount postings are done.
	 */
	public void finishOrder(Advice advice) throws BusinessException {
		String logStr = "finishOrder ";
		logger.log(Level.FINE, logStr + "advice=" + advice);

		for (AdviceLine line : advice.getLines()) {
			if (line.getState() < OrderState.FINISHED) {
				int lineState = line.getState();
				line.setState(OrderState.FINISHED);
				fireAdviceLineStateChangeEvent(line, lineState);
			}
		}
		if (advice.getState() < OrderState.FINISHED) {
			int orderState = advice.getState();
			advice.setState(OrderState.FINISHED);
			fireAdviceStateChangeEvent(advice, orderState);
		}
	}

	/**
	 * Remove the advice
	 * <p>
	 * The advice is removed.<br>
	 * StateChange events are fired.<br>
	 * No amount postings are done.<br>
	 * Removal is not possible if there is received amount
	 */
	public void removeOrder(Advice advice) throws BusinessException {
		String logStr = "removeOrder ";
		logger.log(Level.FINE, logStr + "advice=" + advice);

		for (AdviceLine adviceLine : advice.getLines()) {
			// Check assigned GoodsReceiptLines

			boolean hasGoodsReceiptLine = entitySerive.exists(GoodsReceiptLine.class, "adviceLine", adviceLine);
			if (hasGoodsReceiptLine) {
				logger.log(Level.INFO, logStr + "Cannot remove with assigned GoodsReceiptLine");
				throw new BusinessException(Wms2BundleResolver.class, "Advice.cannotRemoveWithGoodsReceiptLine");
			}

			// Check assigned GoodsReceipt
			List<GoodsReceipt> goodsReceiptList = goodsReceiptService.readList(adviceLine, null, null, null, null);
			for (GoodsReceipt goodsReceipt : goodsReceiptList) {

				if (goodsReceipt.getAdviceLines().contains(adviceLine)) {
					for (GoodsReceiptLine goodsReceiptLine : goodsReceipt.getLines()) {
						if (goodsReceiptLine.getAdviceLine() != null
								&& goodsReceiptLine.getAdviceLine().equals(adviceLine)) {
							logger.log(Level.INFO, logStr + "Cannot remove with assigned GoodsReceipt");
							throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.existsPostedLines");
						}
					}
					goodsReceipt.getAdviceLines().remove(adviceLine);
				}
			}

			int lineState = adviceLine.getState();
			adviceLine.setState(OrderState.DELETABLE);
			fireAdviceLineStateChangeEvent(adviceLine, lineState);

			manager.removeValidated(adviceLine);
		}

		int orderState = advice.getState();
		advice.setState(OrderState.DELETABLE);
		fireAdviceStateChangeEvent(advice, orderState);

		manager.removeValidated(advice);
	}

	/**
	 * Create a new advice
	 * <p>
	 * Add lines with createOrderLine()
	 */
	public Advice createOrder(Client client, String orderNumber) throws BusinessException {
		String logStr = "createOrder ";
		logger.log(Level.FINE, logStr + "client=" + client);

		if (client == null) {
			client = clientBusiness.getSystemClient();
		}

		if (StringUtils.isEmpty(orderNumber)) {
			orderNumber = sequenceBusiness.readNextValue(Advice.class.getSimpleName(), Advice.class, "orderNumber");
		}

		Advice advice = manager.createInstance(Advice.class);
		advice.setOrderNumber(orderNumber);
		advice.setState(OrderState.CREATED);
		advice.setClient(client);
		manager.persistValidated(advice);

		advice.setLines(new ArrayList<AdviceLine>());

		fireAdviceStateChangeEvent(advice, -1);

		return advice;
	}

	/**
	 * Add a new line to the advice.
	 * 
	 * @param minLineCounter The value to start with the line number generation. The
	 *                       number is counted upwards and each number is validated
	 *                       with a single query until a not already used number is
	 *                       found. So start with a plausible number.
	 * 
	 */
	public AdviceLine addOrderLine(Advice advice, ItemData itemData, String lotNumber, BigDecimal amount,
			int minLineCounter) throws BusinessException {
		String logStr = "addOrderLine ";
		logger.log(Level.FINE, logStr + "advice=" + advice + ", itemData=" + itemData + ", amount=" + amount);

		String lineNumber = sequenceBusiness.readNextCounterValue(advice.getOrderNumber(), minLineCounter,
				AdviceLine.class, "lineNumber");

		AdviceLine line = manager.createInstance(AdviceLine.class);
		line.setAdvice(advice);
		line.setItemData(itemData);
		line.setLotNumber(lotNumber);
		line.setAmount(amount);
		line.setLineNumber(lineNumber);
		line.setConfirmedAmount(BigDecimal.ZERO);
		line.setState(OrderState.CREATED);
		manager.persistValidated(line);

		advice.getLines().add(line);

		fireAdviceLineStateChangeEvent(line, -1);

		return line;
	}

	/**
	 * Finish editing of an advice.<br>
	 * After finishing editing the advice is usable for further processing.<br>
	 * The advice gets the state PROCESSABLE.
	 */
	public Advice releaseOperation(Advice order) throws BusinessException {
		String logStr = "releaseOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		for (AdviceLine line : order.getLines()) {
			int lineState = line.getState();
			if (lineState >= OrderState.PROCESSABLE) {
				continue;
			}
			line.setState(OrderState.PROCESSABLE);
			if (lineState != line.getState()) {
				fireAdviceLineStateChangeEvent(line, lineState);
			}
		}

		int orderState = order.getState();
		if (orderState < OrderState.PROCESSABLE) {
			order.setState(OrderState.PROCESSABLE);
			fireAdviceStateChangeEvent(order, orderState);
		}

		return order;
	}

	public void checkReceiveAmount(AdviceLine adviceLine, BigDecimal amount) throws BusinessException {
		String logStr = "checkReceiveAmount ";
		logger.log(Level.FINE, logStr + "adviceLine=" + adviceLine + ", amount=" + amount);

		Advice advice = adviceLine.getAdvice();
		BigDecimal newAmount = adviceLine.getConfirmedAmount().add(amount);

		if (newAmount.compareTo(adviceLine.getAmount()) > 0) {
			boolean limit = propertyBusiness.getBoolean(Wms2Properties.KEY_GOODSRECEIPT_LIMIT_AMOUNT_TO_NOTIFIED,
					advice.getClient(), null, false);
			if (limit) {
				logger.log(Level.INFO,
						logStr + "Amount of advice is limited. Cannot receive more amount. advice=" + adviceLine
								+ ", amount=" + amount + ", received-amount=" + adviceLine.getConfirmedAmount()
								+ ", notified-amount=" + adviceLine.getAmount());
				throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.adviceAmountLimited");
			}
		}

	}

	public AdviceLine addConfirmedAmount(AdviceLine adviceLine, BigDecimal amount) throws BusinessException {
		String logStr = "addConfirmedAmount ";
		logger.log(Level.FINE, logStr + "adviceLine=" + adviceLine + ", amount=" + amount);

		Advice advice = adviceLine.getAdvice();

		int lineState = adviceLine.getState();
		int orderState = advice.getState();

		adviceLine.setConfirmedAmount(adviceLine.getConfirmedAmount().add(amount));

		if (adviceLine.getState() < OrderState.STARTED) {
			adviceLine.setState(OrderState.STARTED);
		}
		if (adviceLine.getConfirmedAmount().compareTo(adviceLine.getAmount()) >= 0) {
			if (adviceLine.getState() < OrderState.FINISHED) {
				adviceLine.setState(OrderState.FINISHED);
			}
		}
		if (lineState != adviceLine.getState()) {
			fireAdviceLineStateChangeEvent(adviceLine, lineState);
		}

		if (advice.getState() < OrderState.STARTED) {
			advice.setState(OrderState.STARTED);
		}
		if (adviceLine.getState() >= OrderState.FINISHED && advice.getState() < OrderState.FINISHED) {
			boolean hasOpen = false;
			for (AdviceLine line : advice.getLines()) {
				if (line.equals(adviceLine)) {
					continue;
				}
				if (line.getState() < OrderState.FINISHED) {
					hasOpen = true;
					break;
				}
			}
			if (!hasOpen) {
				advice.setState(OrderState.FINISHED);
			}
		}
		if (orderState != advice.getState()) {
			fireAdviceStateChangeEvent(advice, orderState);
		}

		return adviceLine;
	}

	public AdviceLine removeConfirmedAmount(AdviceLine adviceLine, BigDecimal amount) throws BusinessException {
		String logStr = "removeConfirmedAmount ";
		logger.log(Level.FINE, logStr + "adviceLine=" + adviceLine + ", amount=" + amount);

		int lineState = adviceLine.getState();

		BigDecimal newAmount = adviceLine.getConfirmedAmount().subtract(amount);
		if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
			newAmount = BigDecimal.ZERO;
		}
		adviceLine.setConfirmedAmount(newAmount);

		if (adviceLine.getAmount().compareTo(adviceLine.getConfirmedAmount()) > 0) {
			if (adviceLine.getState() > OrderState.STARTED) {
				adviceLine.setState(OrderState.STARTED);
			}
		}
		if (lineState != adviceLine.getState()) {
			fireAdviceLineStateChangeEvent(adviceLine, lineState);
		}

		if (lineState >= OrderState.FINISHED && adviceLine.getState() < OrderState.FINISHED) {
			Advice advice = adviceLine.getAdvice();
			if (advice.getState() == OrderState.FINISHED) {
				int orderState = advice.getState();
				advice.setState(OrderState.STARTED);
				fireAdviceStateChangeEvent(advice, orderState);
			}
		}

		return adviceLine;
	}

	private void fireAdviceStateChangeEvent(Advice entity, int oldState) throws BusinessException {
		try {
			adviceStateChangeEvent.fire(new AdviceStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireAdviceLineStateChangeEvent(AdviceLine entity, int oldState) throws BusinessException {
		try {
			adviceLineStateChangeEvent.fire(new AdviceLineStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
