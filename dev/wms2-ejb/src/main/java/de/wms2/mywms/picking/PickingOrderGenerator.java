/* 
Copyright 2019-2020 Matthias Krane
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.delivery.DeliveryOrderStateChangeEvent;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Handling of picking order generation
 * 
 * @author krane
 *
 */
@Stateless
public class PickingOrderGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private Event<PickingOrderStateChangeEvent> pickingOrderStateChangeEvent;
	@Inject
	private Event<PickingOrderLineStateChangeEvent> pickingOrderLineStateChangeEvent;
	@Inject
	private Event<DeliveryOrderStateChangeEvent> deliveryOrderStateChangeEvent;
	@Inject
	private Event<PickingOrderPrepareEvent> pickingOrderPrepareEvent;
	@Inject
	private PickingOrderEntityService pickingOrderService;

	/**
	 * Generate orders for the given picks. All picks should have the same strategy.
	 * This strategy is used for order generation.
	 */
	public List<PickingOrder> generatePickingOrders(Collection<PickingOrderLine> picks) throws BusinessException {
		String logStr = "generatePickingOrders ";
		logger.log(Level.FINE, logStr);

		// Check data of the given lines and extract information for picking order
		// generation
		Client client = null;
		OrderStrategy strategy = null;
		Map<String, List<DeliveryOrder>> deliveryOrdersMap = new HashMap<>();
		Map<String, List<PickingOrderLine>> picksMap = new HashMap<>();

		for (PickingOrderLine pick : picks) {

			// Use only valid lines
			if (pick.getState() >= OrderState.PICKED) {
				logger.log(Level.FINE, logStr + "Pick is already picked. pick=" + pick);
				continue;
			}
			if (pick.getPickingOrder() != null) {
				logger.log(Level.FINE, logStr + "Pick has already picking order. pick=" + pick + ", pickingOrder="
						+ pick.getPickingOrder());
				continue;
			}
			if (pick.getPickFromStockUnit() == null) {
				logger.log(Level.FINE, logStr + "Pick has no pick from stock. pick=" + pick);
				continue;
			}

			// Verify that only one client is requested
			if (client == null) {
				client = pick.getClient();
			}
			if (!Objects.equals(client, pick.getClient())) {
				logger.log(Level.SEVERE, logStr + "Cannot generate picking order for different clients. Abort. client1="
						+ client + ", client2=" + pick.getClient());
				throw new BusinessException(Wms2BundleResolver.class, "PickingGenerator.inequalClients");
			}

			// Verify that only one strategy is used
			if (strategy == null) {
				strategy = pick.getOrderStrategy();
			}
			if (!Objects.equals(strategy, pick.getOrderStrategy())) {
				logger.log(Level.SEVERE,
						logStr + "Cannot generate picking orders for different strategies. Abort. strategy1=" + strategy
								+ ", strategy2=" + pick.getOrderStrategy());
				throw new BusinessException(Wms2BundleResolver.class, "PickingGenerator.inequalStrategies");
			}

			// Split picking lines in separate lists for each type
			String orderKey = "";
			if (strategy.isCreateTypeOrders()) {
				orderKey += pick.getPickingType();
			}
			orderKey += "-";

			List<PickingOrderLine> picksPerOrder = picksMap.get(orderKey);
			if (picksPerOrder == null) {
				picksPerOrder = new ArrayList<>();
				picksMap.put(orderKey, picksPerOrder);
			}
			picksPerOrder.add(pick);

			// Collect delivery orders
			DeliveryOrderLine deliveryOrderLine = pick.getDeliveryOrderLine();
			if (deliveryOrderLine != null) {
				DeliveryOrder deliveryOrder = deliveryOrderLine.getDeliveryOrder();
				List<DeliveryOrder> deliveryOrders = deliveryOrdersMap.get(orderKey);
				if (deliveryOrders == null) {
					deliveryOrders = new ArrayList<>();
					deliveryOrdersMap.put(orderKey, deliveryOrders);
				}
				if (!deliveryOrders.contains(deliveryOrder)) {
					deliveryOrders.add(deliveryOrder);
				}
			}
		}

		List<PickingOrder> newPickingOrders = new ArrayList<>();

		// Handle the pick list for each type separately
		for (String orderKey : picksMap.keySet()) {

			List<PickingOrderLine> affectedPicks = picksMap.get(orderKey);
			if (affectedPicks == null || affectedPicks.size() == 0) {
				continue;
			}

			List<DeliveryOrder> affectedDeliveryOrders = deliveryOrdersMap.get(orderKey);
			if (affectedDeliveryOrders == null) {
				affectedDeliveryOrders = new ArrayList<>();
			}

			StorageLocation destinationLocation = calculateDestinationLocation(affectedDeliveryOrders, strategy);
			int prio = calculatePrio(affectedDeliveryOrders);

			// Send all lists to preparation services
			// User exit for specialized services for picking order generation like
			// pick-to-pack...
			// The externalNumber of the picking order is effectively used to identify the
			// picking-orders, generated by the specialized services.
			// The called service may generate more than one picking order.
			String id = UUID.randomUUID().toString();
			firePickingOrderPrepareEvent(id, affectedPicks, destinationLocation, prio);
			List<PickingOrder> preparedOrders = pickingOrderService.readList(id, null, OrderState.PROCESSABLE);

			// Other picks are calculated by the default strategy
			List<PickingOrderLine> preparedLines = new ArrayList<>();
			for (PickingOrder preparedOrder : preparedOrders) {
				newPickingOrders.add(preparedOrder);
				for (PickingOrderLine preparedLine : preparedOrder.getLines()) {
					preparedLines.add(preparedLine);
				}
			}
			affectedPicks.removeAll(preparedLines);
			if (affectedPicks.size() == 0) {
				continue;
			}

			// Create a new picking order for the not yet prepared picks
			PickingOrder pickingOrder = pickingOrderService.create(client, strategy);
			pickingOrder.setState(OrderState.UNDEFINED);
			pickingOrder.setCreateFollowUpPicks(strategy.isCreateFollowUpPicks());
			pickingOrder.setDestination(destinationLocation);
			pickingOrder.setPrio(prio);
			if (affectedDeliveryOrders.size() == 1) {
				DeliveryOrder deliveryOrder = affectedDeliveryOrders.get(0);
				pickingOrder.setDeliveryOrder(deliveryOrder);
				pickingOrder.setPickingHint(deliveryOrder.getPickingHint());
				pickingOrder.setPackingHint(deliveryOrder.getPackingHint());
				pickingOrder.setDeliveryDate(deliveryOrder.getDeliveryDate());
				pickingOrder.setAddress(deliveryOrder.getAddress());
				pickingOrder.setCarrierName(deliveryOrder.getCarrierName());
				pickingOrder.setCarrierService(deliveryOrder.getCarrierService());
				pickingOrder.setExternalNumber(deliveryOrder.getExternalNumber());
			}

			for (PickingOrderLine pick : affectedPicks) {
				pick.setPickingOrder(pickingOrder);

				// Switch state of pick to PROCESSABLE
				int pickState = pick.getState();
				if (pickState != OrderState.PROCESSABLE) {
					pick.setState(OrderState.PROCESSABLE);
					firePickingLineStateChangeEvent(pick, pickState);
				}
			}
			pickingOrder.getLines().addAll(affectedPicks);

			firePickingOrderStateChangeEvent(pickingOrder, -1);

			newPickingOrders.add(pickingOrder);

			// Switch state of the deliveryOrder to PROCESSABLE
			for (DeliveryOrder deliveryOrder : affectedDeliveryOrders) {
				recalculateDeliveryOrderState(deliveryOrder);
			}
		}

		return newPickingOrders;
	}

	/**
	 * Try to add the given picks to the given order. Picks which cannot be added
	 * are returned.
	 * 
	 * @return The picks which have NOT been added
	 */
	public List<PickingOrderLine> addPicksToOrder(PickingOrder pickingOrder, Collection<PickingOrderLine> picks)
			throws BusinessException {
		String logStr = "addPicksToOrder ";
		logger.log(Level.FINE, logStr + "pickingOrder=" + pickingOrder);

		List<PickingOrderLine> notMatchingPicks = new ArrayList<>();

		OrderStrategy strategy = pickingOrder.getOrderStrategy();
		int mainPickingType = calculatePickingType(pickingOrder, picks);
		int orderState = pickingOrder.getState();

		for (PickingOrderLine pick : picks) {
			if (!isOrderMatching(pickingOrder, pick, mainPickingType)) {
				logger.log(Level.INFO, logStr + "pick is not matching. pick.pickingType=" + pick.getPickingType()
						+ ", mainPickingType=" + mainPickingType + ", pickingOrder=" + pickingOrder);
				notMatchingPicks.add(pick);
				continue;
			}
			if (!pick.getOrderStrategy().equals(strategy)) {
				logger.log(Level.INFO,
						logStr + "Cannot handle different strategies of picking order and line. Abort. order strategy="
								+ strategy + ", pick strategy=" + pick.getOrderStrategy());
				notMatchingPicks.add(pick);
				continue;
			}
			pick.setPickingOrder(pickingOrder);
			pickingOrder.getLines().add(pick);
			if (orderState >= OrderState.RELEASED && orderState < OrderState.PICKED) {
				int pickState = pick.getState();
				pick.setState(OrderState.PROCESSABLE);
				firePickingLineStateChangeEvent(pick, pickState);
			}
		}

		return notMatchingPicks;
	}

	/**
	 * Check whether the strategy allows to add the pick the picking order
	 */
	private boolean isOrderMatching(PickingOrder pickingOrder, PickingOrderLine pick, int mainPickingType) {
		OrderStrategy strategy = pickingOrder.getOrderStrategy();

		// check client
		if (!pickingOrder.getClient().equals(pick.getClient())) {
			return false;
		}

		// check type
		if (strategy.isCreateTypeOrders()) {
			if (pick.getPickingType() != mainPickingType) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Find a picking type for the order. If the order has lines with types, use one
	 * of them. Otherwise use one of the types of the given picks.
	 */
	private int calculatePickingType(PickingOrder pickingOrder, Collection<PickingOrderLine> picks) {
		if (pickingOrder.getLines().size() > 0) {
			return pickingOrder.getLines().get(0).getPickingType();
		}
		if (picks.size() > 0) {
			return picks.iterator().next().getPickingType();
		}
		return PickingType.DEFAULT;
	}

	/**
	 * Calculate and set the state of the delivery order
	 */
	private void recalculateDeliveryOrderState(DeliveryOrder deliveryOrder) throws BusinessException {
		int deliveryOrderState = deliveryOrder.getState();
		if (deliveryOrderState < OrderState.PROCESSABLE) {
			deliveryOrder.setState(OrderState.PROCESSABLE);
			fireDeliveryOrderStateChangeEvent(deliveryOrder, deliveryOrderState);
		}
		if (deliveryOrderState == OrderState.PENDING) {
			deliveryOrder.setState(OrderState.STARTED);
			fireDeliveryOrderStateChangeEvent(deliveryOrder, deliveryOrderState);
		}
	}

	/**
	 * Find the smallest value of all priorities of the delivery orders
	 */
	private int calculatePrio(List<DeliveryOrder> deliveryOrders) {
		int prio = Integer.MAX_VALUE;

		for (DeliveryOrder deliveryOrder : deliveryOrders) {
			int orderPrio = deliveryOrder.getPrio();
			if (orderPrio < prio) {
				prio = orderPrio;
			}
		}

		if (prio < Integer.MAX_VALUE) {
			return prio;
		}

		return OrderPrio.NORMAL;
	}

	/**
	 * If all deliveryOrders have only one destination, use that. Otherwise use the
	 * destination of the strategy
	 */
	private StorageLocation calculateDestinationLocation(List<DeliveryOrder> deliveryOrders, OrderStrategy strategy) {
		StorageLocation destinationLocation = null;

		for (DeliveryOrder deliveryOrder : deliveryOrders) {
			StorageLocation ordersDestinationLocation = deliveryOrder.getDestination();
			if (destinationLocation == null) {
				destinationLocation = ordersDestinationLocation;
				continue;
			}
			if (!Objects.equals(destinationLocation, ordersDestinationLocation)) {
				return strategy.getDefaultDestination();
			}
		}

		if (destinationLocation != null) {
			return destinationLocation;
		}

		return strategy.getDefaultDestination();
	}

	private void firePickingOrderStateChangeEvent(PickingOrder pickingOrder, int oldState) throws BusinessException {
		try {
			logger.fine("Fire PickingOrderStateChangeEvent. pickingOrder=" + pickingOrder + ", state="
					+ pickingOrder.getState() + ", oldState=" + oldState);
			pickingOrderStateChangeEvent
					.fire(new PickingOrderStateChangeEvent(pickingOrder, oldState, pickingOrder.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void firePickingLineStateChangeEvent(PickingOrderLine pickingOrderLine, int oldState)
			throws BusinessException {
		try {
			logger.fine("Fire PickingOrderLineStateChangeEvent. pickingOrderLine=" + pickingOrderLine + ", state="
					+ pickingOrderLine.getState() + ", oldState=" + oldState);
			pickingOrderLineStateChangeEvent.fire(
					new PickingOrderLineStateChangeEvent(pickingOrderLine, oldState, pickingOrderLine.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireDeliveryOrderStateChangeEvent(DeliveryOrder deliveryOrder, int oldState) throws BusinessException {
		try {
			logger.fine("Fire DeliveryOrderStateChangeEvent. deliveryOrder=" + deliveryOrder + ", state="
					+ deliveryOrder.getState() + ", oldState=" + oldState);
			deliveryOrderStateChangeEvent
					.fire(new DeliveryOrderStateChangeEvent(deliveryOrder, oldState, deliveryOrder.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void firePickingOrderPrepareEvent(String id, List<PickingOrderLine> picks, StorageLocation location,
			Integer prio) throws BusinessException {
		try {
			logger.fine("Fire PickingOrderPrepareEvent. id=" + id + ", picks=" + picks);
			pickingOrderPrepareEvent.fire(new PickingOrderPrepareEvent(id, picks, location, prio));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}
}
