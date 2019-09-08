/* 
Copyright 2019 Matthias Krane
info@krane.engineer

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
package de.wms2.mywms.goodsreceipt;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.advice.AdviceLineAssignEvent;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitStateChangeEvent;
import de.wms2.mywms.inventory.TrashHandler;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * @author krane
 *
 */
@Stateless
public class GoodsReceiptBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private Event<AdviceLineAssignEvent> adviceLineAssignEvent;
	@Inject
	private Event<GoodsReceiptLineDeletedEvent> goodsReceiptLineDeletedEvent;
	@Inject
	private Event<GoodsReceiptStateChangeEvent> goodsReceiptStateChangeEvent;
	@Inject
	private Event<StockUnitStateChangeEvent> stockUnitStateChangeEvent;
	@Inject
	private Event<GoodsReceiptLineCollectEvent> goodsReceiptLineCollectEvent;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private GoodsReceiptEntityService goodsReceiptService;
	@Inject
	private TrashHandler trashHandler;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;

	public void cleanupDeleted() throws BusinessException {
		List<GoodsReceipt> orders = goodsReceiptService.readList(null, OrderState.DELETABLE, null, null, null);
		for (GoodsReceipt order : orders) {
			trashHandler.removeGoodsReceipt(order);
		}
	}

	public GoodsReceipt releaseOperation(GoodsReceipt order, User operator, Integer prio, String note)
			throws BusinessException {
		String logStr = "releaseOperation ";
		logger.log(Level.FINE, logStr + "order=" + order + ", operator=" + operator + ", prio=" + prio);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyFinished");
		}

		int orderPrio = order.getPrio();
		if (prio != null && prio.intValue() != orderPrio) {
			order.setPrio(prio.intValue());
		}

		order.setOperator(operator);

		if (order.getState() <= OrderState.RESERVED) {
			order.setState(OrderState.PROCESSABLE);
			if (order.getOperator() != null) {
				order.setState(OrderState.RESERVED);
			}
		}

		// do not kill notes in operating dialogs
		if (!StringUtils.isEmpty(note)) {
			order.setAdditionalContent(note);
		}

		if (order.getState() != orderState) {
			fireGoodsReceiptStateChangeEvent(order, orderState);
		}

		return order;
	}

	public GoodsReceipt startOperation(GoodsReceipt order, User operator) throws BusinessException {
		String logStr = "startOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyFinished");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		if (orderState > OrderState.RESERVED) {
			User ordersOperator = order.getOperator();
			if (ordersOperator != null && !ordersOperator.equals(operator)) {
				logger.log(Level.WARNING, logStr + "Order started by different user. order=" + order + ", operator="
						+ order.getOperator() + ", reserving operator=" + ordersOperator);
				throw new BusinessException(Wms2BundleResolver.class, "reservationMissmatch");
			}
		}
		order.setOperator(operator);

		if (orderState < OrderState.STARTED) {
			order.setState(OrderState.STARTED);
		}

		if (order.getState() != orderState) {
			fireGoodsReceiptStateChangeEvent(order, orderState);
		}

		return order;
	}

	public GoodsReceipt cancelOperation(GoodsReceipt order) throws BusinessException {
		String logStr = "cancelOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyFinished");
		}
		if (orderState < OrderState.PROCESSABLE) {
			logger.log(Level.INFO, logStr + "Order not yet processable. order=" + order);
			return order;
		}

		order.setOperator(null);
		order.setState(OrderState.PROCESSABLE);

		if (orderState != order.getState()) {
			fireGoodsReceiptStateChangeEvent(order, orderState);
		}

		return order;
	}

	public GoodsReceipt reserveOperation(GoodsReceipt order, User operator) throws BusinessException {
		String logStr = "reserveOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState > OrderState.RESERVED) {
			logger.log(Level.WARNING, logStr + "Order already started. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyStarted");
		}
		if (orderState < OrderState.PROCESSABLE) {
			logger.log(Level.WARNING, logStr + "Order not yet started. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderNotYetProcessable");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		order.setOperator(operator);

		if (orderState < OrderState.RESERVED) {
			order.setState(OrderState.RESERVED);
			fireGoodsReceiptStateChangeEvent(order, orderState);
		}

		return order;
	}

	public GoodsReceipt pauseOrder(GoodsReceipt order) throws BusinessException {
		String logStr = "pauseOrder ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState < OrderState.CREATED) {
			logger.log(Level.WARNING, logStr + "Order not finish-edited. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderNotCreated");
		}
		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyFinished");
		}

		order.setState(OrderState.PAUSE);
		if (order.getState() != orderState) {
			fireGoodsReceiptStateChangeEvent(order, orderState);
		}

		return order;
	}

	public GoodsReceipt createOrder(Client client, StorageLocation location) {
		String logStr = "createOrder ";
		logger.log(Level.FINE, logStr + "client=" + client);

		String number = sequenceBusiness.readNextValue(GoodsReceipt.class.getSimpleName(), GoodsReceipt.class,
				"orderNumber");

		GoodsReceipt goodsReceipt = manager.createInstance(GoodsReceipt.class);
		goodsReceipt.setClient(client);
		goodsReceipt.setStorageLocation(location);
		goodsReceipt.setOrderNumber(number);

		manager.persist(goodsReceipt);

		return goodsReceipt;
	}

	public void removeOrder(GoodsReceipt order) throws BusinessException {
		String logStr = "removeOrder";
		logger.log(Level.FINE, logStr + "order=" + order);

		for (GoodsReceiptLine line : order.getLines()) {
			manager.removeValidated(line);
		}

		int orderState = order.getState();
		order.setState(OrderState.DELETABLE);
		fireGoodsReceiptStateChangeEvent(order, orderState);

		manager.removeValidated(order);
	}

	public GoodsReceiptLine receiveStock(GoodsReceipt order, AdviceLine adviceLine, String unitLoadLabel,
			UnitLoadType unitLoadType, ItemData itemData, StorageStrategy storageStrategy, BigDecimal amount, Lot lot,
			String serialNumber, int lock, String lockNote, PackagingUnit packagingUnit, String note)
			throws BusinessException {
		String logStr = "receiveStock ";
		logger.log(Level.FINE, logStr + "order=" + order + ", adviceLine=" + adviceLine + ", itemData=" + itemData
				+ ", amount=" + amount + ", packagingUnit=" + packagingUnit);

		if (unitLoadType == null) {
			unitLoadType = unitLoadTypeService.getDefault();
		}

		if (itemData == null) {
			logger.log(Level.WARNING, logStr + "Missing parameter itemData");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}

		StorageLocation location = order.getStorageLocation();
		if (location == null) {
			logger.log(Level.WARNING, logStr + "Missing location");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocation");
		}

		if (itemData.isLotMandatory() && lot == null) {
			logger.log(Level.WARNING, logStr + "Missing Lot");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLot");
		}

		if (itemData.isLotMandatory() && lot.getBestBeforeEnd() != null) {
			Integer shelflife = itemData.getShelflife();
			if (shelflife != null) {
				Date min = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), shelflife);
				if (lot.getBestBeforeEnd().compareTo(min) < 0) {
					logger.log(Level.WARNING, logStr + "Not enough shelflife. itemData=" + itemData + ", shelflife="
							+ shelflife + ", min best-before=" + min + ", best-before=" + lot.getBestBeforeEnd());
					throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidShelfLife",
							new Object[] { shelflife });
				}
			}
		}

		Client client = order.getClient();
		User operator = userBusiness.getCurrentUser();
		int numPos = readNumLines(order);
		String lineNumber = sequenceBusiness.readNextCounterValue(order.getOrderNumber(), numPos + 1,
				GoodsReceiptLine.class, "lineNumber");
		String activityCode = lineNumber;

		UnitLoad unitLoad = null;

		if (StringUtils.isEmpty(unitLoadLabel)) {
			String label = sequenceBusiness.readNextValue(UnitLoad.class.getSimpleName(), UnitLoad.class, "labelId");
			unitLoad = inventoryBusiness.createUnitLoad(client, label, unitLoadType, location, StockState.INCOMING,
					activityCode, operator, note);
		} else {
			unitLoad = unitLoadService.readByLabel(unitLoadLabel);
			if (unitLoad == null) {
				unitLoad = inventoryBusiness.createUnitLoad(client, unitLoadLabel, unitLoadType, location,
						StockState.INCOMING, activityCode, operator, note);
			} else if (unitLoad.getState() != StockState.INCOMING) {
				logger.log(Level.WARNING, logStr + "UnitLoad already exists. unitLoad=" + unitLoad);
				throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.unitLoadAlreadyExists");
			}
		}

		unitLoad.addAdditionalContent(note);
		StockUnit stock = inventoryBusiness.createStock(unitLoad, itemData, amount, lot, serialNumber, packagingUnit,
				StockState.INCOMING, activityCode, operator, note, false);

		GoodsReceiptLine goodsReceiptLine = manager.createInstance(GoodsReceiptLine.class);
		goodsReceiptLine.setLineNumber(lineNumber);
		goodsReceiptLine.setGoodsReceipt(order);
		goodsReceiptLine.setAdviceLine(adviceLine);

		goodsReceiptLine.setItemData(itemData);
		goodsReceiptLine.setStorageStrategy(storageStrategy);
		goodsReceiptLine.setAmount(amount);
		goodsReceiptLine.setLotNumber(lot == null ? null : lot.getName());
		goodsReceiptLine.setBestBefore(lot == null ? null : lot.getBestBeforeEnd());
		goodsReceiptLine.setSerialNumber(serialNumber);

		goodsReceiptLine.setLock(lock);
		goodsReceiptLine.setLockNote(lockNote);
		goodsReceiptLine.setAdditionalContent(note);
		goodsReceiptLine.setOperator(operator);
		goodsReceiptLine.setUnitLoadLabel(unitLoad.getLabelId());
		goodsReceiptLine.setStockUnitId(stock.getId());

		manager.persist(goodsReceiptLine);

		if (adviceLine != null) {
			if (!adviceLine.getAdvice().getClient().equals(client)) {
				logger.log(Level.INFO, logStr + "Client does not match. order=" + order + ", goodsreceipt client="
						+ client + ", advice client=" + adviceLine.getAdvice().getClient());
				throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.clientMismatch");
			}

			if (!order.isUseAdvice()) {
				logger.log(Level.INFO,
						logStr + "Cannot assign advice to GoodsReceipt with useAdvice=false. order=" + order);
				throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.cannotHaveAdvices");
			}

			if (!order.getAdviceLines().contains(adviceLine)) {
				order.getAdviceLines().add(adviceLine);
				fireAdviceLineAssignEvent(adviceLine);
			}
		}

		if (order.getState() < OrderState.STARTED) {
			int orderState = order.getState();
			order.setState(OrderState.STARTED);
			fireGoodsReceiptStateChangeEvent(order, orderState);
		}

		fireGoodsReceiptLineCollectEvent(goodsReceiptLine);

		return goodsReceiptLine;
	}

	public StockUnit receiveStock(Client client, StorageLocation location, String unitLoadLabel,
			UnitLoadType unitLoadType, ItemData itemData, BigDecimal amount, Lot lot, String serialNumber,
			PackagingUnit packagingUnit, String note) throws BusinessException {
		String logStr = "receiveStock ";
		logger.log(Level.FINE,
				logStr + "itemData=" + itemData + ", amount=" + amount + ", packagingUnit=" + packagingUnit);

		if (unitLoadType == null) {
			unitLoadType = unitLoadTypeService.getDefault();
		}

		if (itemData == null) {
			logger.log(Level.WARNING, logStr + "Missing parameter itemData");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}

		if (location == null) {
			logger.log(Level.WARNING, logStr + "Missing location");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocation");
		}

		if (itemData.isLotMandatory() && lot == null) {
			logger.log(Level.WARNING, logStr + "Missing Lot");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLot");
		}

		if (itemData.isLotMandatory() && lot.getBestBeforeEnd() != null) {
			Integer shelflife = itemData.getShelflife();
			if (shelflife != null) {
				Date min = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), shelflife);
				if (lot.getBestBeforeEnd().compareTo(min) < 0) {
					logger.log(Level.WARNING, logStr + "Not enough shelflife. itemData=" + itemData + ", shelflife="
							+ shelflife + ", min best-before=" + min + ", best-before=" + lot.getBestBeforeEnd());
					throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidShelfLife",
							new Object[] { shelflife });
				}
			}
		}

		User operator = userBusiness.getCurrentUser();

		UnitLoad unitLoad = null;

		if (StringUtils.isEmpty(unitLoadLabel)) {
			String label = sequenceBusiness.readNextValue(UnitLoad.class.getSimpleName(), UnitLoad.class, "labelId");
			unitLoad = inventoryBusiness.createUnitLoad(client, label, unitLoadType, location, StockState.INCOMING,
					null, operator, note);
		} else {
			unitLoad = unitLoadService.readByLabel(unitLoadLabel);
			if (unitLoad == null) {
				unitLoad = inventoryBusiness.createUnitLoad(client, unitLoadLabel, unitLoadType, location,
						StockState.INCOMING, null, operator, note);
			} else {
				logger.log(Level.WARNING, logStr + "UnitLoad already exists. unitLoad=" + unitLoad);
				throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.unitLoadAlreadyExists");
			}
		}

		unitLoad.addAdditionalContent(note);
		StockUnit stock = inventoryBusiness.createStock(unitLoad, itemData, amount, lot, serialNumber, packagingUnit,
				StockState.ON_STOCK, null, operator, note, false);

		return stock;
	}

	public void assignAdviceLine(GoodsReceipt order, AdviceLine adviceLine) throws BusinessException {
		String logStr = "assignAdviceLine ";
		logger.log(Level.FINE, logStr + "order=" + order + ", adviceLine=" + adviceLine);

		if (!adviceLine.getAdvice().getClient().equals(order.getClient())) {
			logger.log(Level.INFO, logStr + "Client does not match. order=" + order + ", goodsreceipt client="
					+ order.getClient() + ", advice client=" + adviceLine.getAdvice().getClient());
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.clientMismatch");
		}

		if (order.getAdviceLines().contains(adviceLine)) {
			return;
		}

		if (!order.isUseAdvice()) {
			logger.log(Level.INFO,
					logStr + "Cannot assign advice to GoodsReceipt with useAdvice=false. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.cannotHaveAdvices");
		}

		order.getAdviceLines().add(adviceLine);

		fireAdviceLineAssignEvent(adviceLine);
	}

	public void removeAssignedAdviceLine(GoodsReceipt order, AdviceLine adviceLine) throws BusinessException {
		String logStr = "removeAssignedAdviceLine ";
		logger.log(Level.FINE, logStr + "order=" + order + ", adviceLine=" + adviceLine);

		if (!order.getAdviceLines().contains(adviceLine)) {
			return;
		}

		for (GoodsReceiptLine orderLine : order.getLines()) {
			if (orderLine.getAdviceLine() != null && orderLine.getAdviceLine().equals(adviceLine)) {
				logger.log(Level.INFO, logStr + "The advice line is used by a posted goods receipt line. order=" + order
						+ ", orderLine=" + orderLine + ", adviceline=" + adviceLine);
				throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.existsPostedLines");
			}
		}
		order.getAdviceLines().remove(adviceLine);
	}

	public void removeGoodsReceiptWithStocks(GoodsReceipt order) throws BusinessException {
		String logStr = "removeGoodsReceiptWithStocks ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyFinished");
		}

		for (GoodsReceiptLine line : order.getLines()) {
			removeGoodsReceiptLineWithStocks(line);
		}

		order.setState(OrderState.DELETABLE);
		fireGoodsReceiptStateChangeEvent(order, orderState);

		manager.remove(order);
	}

	public void removeGoodsReceiptLineWithStocks(GoodsReceiptLine goodsReceiptLine) throws BusinessException {
		String logStr = "removeGoodsReceiptLineWithStocks ";
		logger.log(Level.FINE, logStr + "line=" + goodsReceiptLine);

		GoodsReceipt order = goodsReceiptLine.getGoodsReceipt();
		if (order.getState() >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.orderAlreadyFinished");
		}

		StockUnit stock = null;
		if (goodsReceiptLine.getStockUnitId() != null) {
			stock = manager.find(StockUnit.class, goodsReceiptLine.getStockUnitId());
		}
		if (stock == null) {
			logger.log(Level.FINE, logStr + "Cannot read stock unit. id=" + goodsReceiptLine.getStockUnitId());
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.stockNotAvailable");
		}

		ItemData itemData = stock.getItemData();
		BigDecimal amount = stock.getAmount();
		UnitLoadType unitLoadType = stock.getUnitLoad().getUnitLoadType();
		User operator = userBusiness.getCurrentUser();
		String activityCode = order.getOrderNumber();
		BigDecimal stockUnitAmount = stock.getAmount();
		BigDecimal receiptAmount = goodsReceiptLine.getAmount();
		if (stockUnitAmount.compareTo(receiptAmount) != 0) {
			logger.log(Level.INFO, "Amount of stock unit is different to amount of goods receipt line. stockUnitAmount="
					+ stockUnitAmount + ", receiptAmount=" + receiptAmount);
			throw new BusinessException(Wms2BundleResolver.class, "GoodsReceipt.stockIsChanged");
		}

		inventoryBusiness.deleteStockUnit(stock, activityCode, operator, null);

		AdviceLine adviceLine = goodsReceiptLine.getAdviceLine();
		goodsReceiptLine.setAdviceLine(null);

		manager.remove(goodsReceiptLine);

		fireGoodsReceiptLineDeletedEvent(goodsReceiptLine, adviceLine, itemData, amount, unitLoadType);
	}

	public GoodsReceipt finishOrder(GoodsReceipt order) throws BusinessException {
		String logStr = "finishOrder ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. ignore. order=" + order);
			return order;
		}

		for (GoodsReceiptLine line : order.getLines()) {
			if (line.getState() < OrderState.FINISHED) {
				line.setState(OrderState.FINISHED);
			}

			StockUnit stock = manager.reload(StockUnit.class, line.getStockUnitId());
			if (stock != null) {
				if (stock.getState() == StockState.INCOMING) {
					stock.setState(StockState.ON_STOCK);
					fireStockUnitStateChangeEvent(stock, StockState.INCOMING);
				}
				UnitLoad unitLoad = stock.getUnitLoad();
				if (unitLoad != null && unitLoad.getState() == StockState.INCOMING) {
					unitLoad.setState(StockState.ON_STOCK);
				}
			}

		}

		order.setState(OrderState.FINISHED);

		fireGoodsReceiptStateChangeEvent(order, orderState);

		return order;
	}

	private int readNumLines(GoodsReceipt goodsReceipt) {
		Query query = manager.createQuery("SELECT count(entity) FROM " + GoodsReceiptLine.class.getName() + " entity "
				+ "WHERE entity.goodsReceipt = :goodsReceipt");

		query.setParameter("goodsReceipt", goodsReceipt);

		try {
			Long num = (Long) query.getSingleResult();
			if (num != null) {
				return num.intValue();
			}
		} catch (NoResultException nre) {
		}
		return 0;
	}

	private void fireAdviceLineAssignEvent(AdviceLine entity) throws BusinessException {
		try {
			adviceLineAssignEvent.fire(new AdviceLineAssignEvent(entity));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireGoodsReceiptLineDeletedEvent(GoodsReceiptLine goodsReceiptLine, AdviceLine adviceLine, ItemData itemData, BigDecimal amount, UnitLoadType unitLoadType)
			throws BusinessException {
		try {
			goodsReceiptLineDeletedEvent.fire(new GoodsReceiptLineDeletedEvent(goodsReceiptLine, adviceLine, itemData, amount, unitLoadType));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireGoodsReceiptStateChangeEvent(GoodsReceipt entity, int oldState) throws BusinessException {
		try {
			goodsReceiptStateChangeEvent.fire(new GoodsReceiptStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireStockUnitStateChangeEvent(StockUnit entity, int oldState) throws BusinessException {
		try {
			stockUnitStateChangeEvent.fire(new StockUnitStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireGoodsReceiptLineCollectEvent(GoodsReceiptLine entity) throws BusinessException {
		try {
			goodsReceiptLineCollectEvent.fire(new GoodsReceiptLineCollectEvent(entity));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
