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
package de.wms2.mywms.picking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.delivery.DeliveryOrderLineStateChangeEvent;
import de.wms2.mywms.delivery.DeliveryOrderStateChangeEvent;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Handling of picking position generation
 * 
 * @author krane
 *
 */
@Stateless
public class PickingOrderLineGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private Event<DeliveryOrderStateChangeEvent> deliveryOrderStateChangeEvent;
	@Inject
	private Event<DeliveryOrderLineStateChangeEvent> deliveryOrderLineStateChangeEvent;
	@Inject
	private Event<PickingOrderLineStateChangeEvent> pickingOrderLineStateChangeEvent;

	@Inject
	private PersistenceManager manager;
	@Inject
	private OrderStrategyEntityService orderStrategyService;
	@Inject
	private PickingStockFinder pickingStockService;
	@Inject
	private PickingOrderLineEntityService pickingLineService;
	@Inject
	private FixAssignmentEntityService fixAssignmentEntityService;

	public List<PickingOrderLine> generatePicks(DeliveryOrder deliveryOrder, boolean completeOrderOnly)
			throws BusinessException {
		String logStr = "generatePickingLines ";
		logger.log(Level.FINE, logStr + "deliveryOrder=" + deliveryOrder);

		List<PickingOrderLine> pickList = new ArrayList<>();

		int orderState = deliveryOrder.getState();
		OrderStrategy strategy = deliveryOrder.getOrderStrategy();

		for (DeliveryOrderLine deliveryOrderLine : deliveryOrder.getLines()) {
			int lineState = deliveryOrderLine.getState();
			if (lineState >= OrderState.PICKED) {
				continue;
			}

			BigDecimal remainingAmount = deliveryOrderLine.getAmount();

			// Consider already existing picks
			List<PickingOrderLine> existingPickList = pickingLineService.readByDeliveryOrderLine(deliveryOrderLine);
			for (PickingOrderLine pick : existingPickList) {
				if (pick.getState() < OrderState.PICKED) {
					remainingAmount = remainingAmount.subtract(pick.getAmount());
				} else if (pick.getState() <= OrderState.FINISHED) {
					remainingAmount = remainingAmount.subtract(pick.getPickedAmount());
				}
			}
			if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
				logger.log(Level.INFO, logStr + "All done for order line=" + deliveryOrderLine + ", ordered amount="
						+ deliveryOrderLine.getAmount() + ", remaining amount=" + remainingAmount);
				continue;
			}

			// Generate picks for the remaining amount
			List<PickingOrderLine> newPickList = generatePicks(deliveryOrderLine, strategy, remainingAmount);

			// Check whether all is calculated
			if (completeOrderOnly) {
				for (PickingOrderLine pick : newPickList) {
					remainingAmount = remainingAmount.subtract(pick.getAmount());
				}
				if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
					logger.log(Level.INFO,
							logStr + "Not enough amount to pick. itemData=" + deliveryOrderLine.getItemData()
									+ ", ordered amount=" + deliveryOrderLine.getAmount() + ", not available amount="
									+ remainingAmount);
					throw new BusinessException(Wms2BundleResolver.class, "PickingGenerator.notEnoughAmount");
				}
			}

			// Calculate new states
			deliveryOrderLine.setState(OrderState.PROCESSABLE);
			if (deliveryOrder.getState() < OrderState.PROCESSABLE) {
				deliveryOrder.setState(OrderState.PROCESSABLE);
			}
			if (deliveryOrderLine.getPickedAmount().compareTo(BigDecimal.ZERO) > 0) {
				deliveryOrderLine.setState(OrderState.STARTED);
			}
			if (deliveryOrder.getState() == OrderState.PENDING) {
				deliveryOrder.setState(OrderState.PROCESSABLE);
				for (DeliveryOrderLine checkStartedLine : deliveryOrder.getLines()) {
					if (checkStartedLine.getAmount().compareTo(BigDecimal.ZERO) > 0) {
						deliveryOrder.setState(OrderState.STARTED);
						break;
					}
				}
			}

			if (deliveryOrderLine.getState() != lineState) {
				fireDeliveryOrderLineStateChangeEvent(deliveryOrderLine, lineState);
			}

			pickList.addAll(newPickList);

		}

		if (deliveryOrder.getState() != orderState) {
			fireDeliveryOrderStateChangeEvent(deliveryOrder, orderState);
		}

		return pickList;
	}

	public List<PickingOrderLine> generatePicks(DeliveryOrderLine deliveryOrderLine, OrderStrategy strategy,
			BigDecimal amount) throws BusinessException {
		String logStr = "generatePickingLines ";

		List<PickingOrderLine> pickList = new ArrayList<>();

		if (amount == null) {
			logger.log(Level.SEVERE, logStr + "No amount. No pick");
			return pickList;
		}
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.SEVERE, logStr + "No amount (0). No pick");
			return pickList;
		}

		Client client = deliveryOrderLine.getDeliveryOrder().getClient();

		if (strategy == null) {
			strategy = orderStrategyService.getDefault(client);
		}

		BigDecimal remainingAmount = amount;
		while (true) {
			StockUnit sourceStock = pickingStockService.findFirstSourceStock(deliveryOrderLine.getItemData(),
					remainingAmount, client, deliveryOrderLine.getLot(), strategy);
			if (sourceStock == null) {
				break;
			}

			BigDecimal availableAmount = sourceStock.getAmount().subtract(sourceStock.getReservedAmount());
			BigDecimal pickAmount = remainingAmount;
			if (pickAmount.compareTo(availableAmount) > 0) {
				pickAmount = availableAmount;
			}
			if (strategy.isCompleteOnly()) {
				pickAmount = availableAmount;
			}

			PickingOrderLine pickingLine = generatePick(deliveryOrderLine, strategy, sourceStock, pickAmount, client,
					deliveryOrderLine.getAdditionalContent(), deliveryOrderLine.getPickingHint());

			pickList.add(pickingLine);

			remainingAmount = remainingAmount.subtract(pickAmount);
			if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}

		return pickList;
	}

	public PickingOrderLine generatePick(DeliveryOrderLine deliveryOrderLine, OrderStrategy strategy,
			StockUnit sourceStock, BigDecimal amount, Client client, String note, String hint)
			throws BusinessException {
		String logStr = "generatePickingLine ";
		logger.log(Level.FINE,
				logStr + "stock=" + sourceStock + ", itemData=" + sourceStock.getItemData() + ", amount=" + amount);

		StorageLocation sourceLocation = sourceStock.getUnitLoad().getStorageLocation();

		PickingOrderLine pick = manager.createInstance(PickingOrderLine.class);
		pick.setAmount(amount);
		pick.setDeliveryOrderLine(deliveryOrderLine);
		pick.setAdditionalContent(note);
		pick.setPickingHint(hint);
		pick.setPickingType(calculatePickingType(sourceStock, amount));
		pick.setItemData(sourceStock.getItemData());
		pick.setPickFromLocationName(sourceLocation.getName());
		pick.setPickFromStockUnit(sourceStock);
		pick.setPickFromUnitLoadLabel(sourceStock.getUnitLoad().getLabelId());
		pick.setState(OrderState.UNDEFINED);
		pick.setOrderStrategy(strategy);
		pick.setClient(client);

		manager.persist(pick);

		sourceStock.setReservedAmount(sourceStock.getReservedAmount().add(amount));

		firePickingOrderLineStateChangeEvent(pick, -1);

		return pick;
	}

	private int calculatePickingType(StockUnit sourceStock, BigDecimal amount) {
		if (amount.compareTo(sourceStock.getAmount()) != 0) {
			return PickingType.PICK;
		}

		UnitLoad unitLoad = sourceStock.getUnitLoad();
		if (unitLoad == null) {
			return PickingType.PICK;
		}
		if (unitLoad.isOpened()) {
			return PickingType.PICK;
		}

		StorageLocation location = unitLoad.getStorageLocation();
		if (location.getArea() != null && !location.getArea().isUseFor(AreaUsages.STORAGE)) {
			return PickingType.PICK;
		}

		ItemData itemData = sourceStock.getItemData();
		boolean hasFixedLocation = fixAssignmentEntityService.exists(itemData, location);
		if (hasFixedLocation) {
			return PickingType.PICK;
		}

		return PickingType.COMPLETE;
	}

	private void fireDeliveryOrderStateChangeEvent(DeliveryOrder deliveryOrder, int oldState) throws BusinessException {
		try {
			logger.fine("Fire DeliveryOrderStateChangeEvent. deliveryOrder=" + deliveryOrder + ", state="
					+ deliveryOrder.getState() + ", oldState=" + oldState);
			deliveryOrderStateChangeEvent.fire(new DeliveryOrderStateChangeEvent(deliveryOrder, oldState));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireDeliveryOrderLineStateChangeEvent(DeliveryOrderLine deliveryOrderLine, int oldState)
			throws BusinessException {
		try {
			logger.fine("Fire DeliveryOrderStateChangeEvent. deliveryOrderLine=" + deliveryOrderLine + ", state="
					+ deliveryOrderLine.getState() + ", oldState=" + oldState);
			deliveryOrderLineStateChangeEvent.fire(new DeliveryOrderLineStateChangeEvent(deliveryOrderLine, oldState));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void firePickingOrderLineStateChangeEvent(PickingOrderLine pickingOrderLine, int oldState)
			throws BusinessException {
		try {
			logger.fine("Fire DeliveryOrderStateChangeEvent. pickingOrderLine=" + pickingOrderLine + ", state="
					+ pickingOrderLine.getState() + ", oldState=" + oldState);
			pickingOrderLineStateChangeEvent.fire(new PickingOrderLineStateChangeEvent(pickingOrderLine, oldState));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
