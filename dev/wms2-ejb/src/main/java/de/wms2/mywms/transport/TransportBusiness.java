/* 
Copyright 2019-2021 Matthias Krane
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
package de.wms2.mywms.transport;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;

import org.mywms.model.User;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLineEntityService;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.TrashHandler;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.LocationFinder;
import de.wms2.mywms.strategy.LocationReserver;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * @author krane
 *
 */
@Stateless
public class TransportBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private Event<TransportOrderStateChangeEvent> stateChangeEvent;
	@Inject
	private Event<TransportOrderPartialProcessEvent> partialProcessEvent;
	@Inject
	private PersistenceManager manager;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private LocationReserver locationReserver;
	@Inject
	private LocationFinder locationFinder;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private TransportOrderEntityService transportOrderEntityService;
	@Inject
	private TrashHandler trashHandler;
	@Inject
	private GoodsReceiptLineEntityService goodsReceiptLineService;

	public void cleanupDeleted() {
		String logStr = "cleanupDeleted ";
		logger.log(Level.FINE, logStr);

		List<TransportOrder> transportOrders = transportOrderEntityService.readList(null, null, null, null, OrderState.DELETABLE,
				null);
		for (TransportOrder transportOrder : transportOrders) {
			trashHandler.removeTransportOrder(transportOrder);
		}
	}

	public TransportOrder startOperation(TransportOrder order, User operator) throws BusinessException {
		String logStr = "startOperation ";
		logger.log(Level.FINE, logStr + "order=" + order + ", operator=" + operator);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.orderAlreadyFinished");
		}
		if (orderState > OrderState.RESERVED) {
			logger.log(Level.WARNING, logStr + "Order already started. Ignore. order=" + order);
			return order;
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		order.setOperator(operator);

		order.setState(OrderState.STARTED);

		if (order.getState() != orderState) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public TransportOrder cancelOrder(TransportOrder order) throws BusinessException {
		String logStr = "cancelOrder ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.orderAlreadyFinished");
		}

		order.setState(OrderState.CANCELED);

		if (order.getDestinationLocation() != null && order.getUnitLoad() != null) {
			locationReserver.deallocateLocation(order.getDestinationLocation(), order.getUnitLoad(), true);
		} else {
			logger.log(Level.WARNING, logStr + "cannot release reservation of location. destinationLocation="
					+ order.getDestinationLocation() + ", unitLoad=" + order.getUnitLoad());
		}

		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	/**
	 * Creates a transport order.
	 * <p>
	 * If an order already exists for the unitLoad, the existing order is used.<br>
	 * If no location is given it will be searched by strategy.<br>
	 * If no location can be found, null is returned. <br>
	 * 
	 * @param destinationLocation If null a location will be searched
	 * @param strategy            If null the default will be used
	 */
	public TransportOrder createOrder(UnitLoad unitLoad, StorageLocation destinationLocation, int orderType,
			StorageStrategy strategy) throws BusinessException {
		String logStr = "createOrder ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad + ", destinationLocation=" + destinationLocation
				+ ", strategy=" + strategy);

		List<TransportOrder> existingOrders = transportOrderEntityService.readOpen(unitLoad);
		if (!existingOrders.isEmpty()) {
			TransportOrder order = existingOrders.get(0);
			logger.log(Level.INFO,
					logStr + "Found existing transport order. order=" + order + ", unitLoad=" + unitLoad);
			return order;
		}

		if (destinationLocation == null) {
			// Try to resolve extra strategy of goods receipt
			if (strategy == null) {
				Area area = unitLoad.getStorageLocation().getArea();
				if (area.isUseFor(AreaUsages.GOODS_IN)) {
					GoodsReceiptLine goodsReceiptLine = goodsReceiptLineService.readFirstByUnitLoad(unitLoad);
					if (goodsReceiptLine != null) {
						strategy = goodsReceiptLine.getStorageStrategy();
					}
				}
			}

			destinationLocation = locationFinder.findStorageLocation(unitLoad, strategy);
		}
		if (destinationLocation == null) {
			logger.log(Level.INFO, logStr + "Found no location. unitLoad=" + unitLoad);
			return null;
		}

		TransportOrder order = manager.createInstance(TransportOrder.class);
		order.setUnitLoad(unitLoad);
		order.setOrderType(orderType);
		order.setState(OrderState.CREATED);
		order.setOrderNumber(sequenceBusiness.readNextValue(TransportOrder.class, "orderNumber"));
		order.setDestinationLocation(destinationLocation);
		order.setClient(unitLoad.getClient());
		manager.persist(order);

		locationReserver.reserveLocation(destinationLocation, unitLoad, TransportOrder.class, order.getId(),
				order.getOrderNumber());

		fireTransportOrderStateChangeEvent(order, -1);

		return order;
	}

	/**
	 * 
	 * Creates a manual transport order.
	 * <p>
	 * If an order already exists for the unitLoad, the existing order is used.
	 */
	public TransportOrder createEmptyOrder(UnitLoad unitLoad, int orderType) throws BusinessException {
		String logStr = "createEmptyOrder ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad);

		List<TransportOrder> existingOrders = transportOrderEntityService.readOpen(unitLoad);
		if (!existingOrders.isEmpty()) {
			TransportOrder order = existingOrders.get(0);
			logger.log(Level.INFO,
					logStr + "Found existing transport order. order=" + order + ", unitLoad=" + unitLoad);
			return order;
		}

		TransportOrder order = manager.createInstance(TransportOrder.class);
		order.setUnitLoad(unitLoad);
		order.setOrderType(orderType);
		order.setState(OrderState.CREATED);
		order.setOrderNumber(sequenceBusiness.readNextValue(TransportOrder.class, "orderNumber"));
		order.setDestinationLocation(null);
		order.setClient(unitLoad.getClient());
		manager.persist(order);

		fireTransportOrderStateChangeEvent(order, -1);

		return order;
	}

	public TransportOrder confirmOrder(TransportOrder order, StorageLocation location) throws BusinessException {
		String logStr = "confirmOrder ";
		logger.log(Level.FINE, logStr + "order=" + order + ", location=" + location);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.orderAlreadyFinished");
		}

		if (location == null) {
			location = order.getDestinationLocation();
		}
		if (location == null) {
			logger.log(Level.WARNING, logStr + "Cannot confirm transport order without destination. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.missingDestination");
		}

		UnitLoad unitLoad = order.getUnitLoad();
		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "Cannot confirm transport order without unit load. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.missingUnitLoad");
		}

		StorageLocation originalDestination = order.getDestinationLocation();
		boolean partialProcessed = false;
		User operator = userBusiness.getCurrentUser();

		order.setState(OrderState.FINISHED);
		order.setOperator(operator);
		order.setConfirmedDestination(location);

		if (originalDestination == null || !(location.equals(originalDestination))) {

			if (!unitLoad.getStorageLocation().equals(location)) {
				inventoryBusiness.checkTransferUnitLoad(unitLoad, location, true);
				inventoryBusiness.transferUnitLoad(unitLoad, location, order.getOrderNumber(), operator, null);
			}
			Area area = location.getArea();
			if (area != null && area.isUseFor(AreaUsages.TRANSFER)) {
				// Do not finish the transport order chain on a transfer location
				// Add next order to transport chain
				createSuccessor(order);
				partialProcessed = true;
			}
			if (!partialProcessed && originalDestination != null) {
				locationReserver.deallocateLocation(originalDestination, unitLoad, true);
			}

		} else {
			// original destination is used
			if (!unitLoad.getStorageLocation().equals(originalDestination)) {
				inventoryBusiness.transferUnitLoad(unitLoad, originalDestination, order.getOrderNumber(), operator,
						null);
			}
		}

		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		if (partialProcessed) {
			fireTransportOrderPartialProcessEvent(order);
		}

		return order;
	}

	private TransportOrder createSuccessor(TransportOrder predecessor) throws BusinessException {
		if (predecessor == null) {
			return null;
		}

		TransportOrder successor = manager.createInstance(TransportOrder.class);
		successor.setUnitLoad(predecessor.getUnitLoad());
		successor.setState(OrderState.PROCESSABLE);
		successor.setOrderNumber(sequenceBusiness.readNextValue(TransportOrder.class, "orderNumber"));
		successor.setDestinationLocation(predecessor.getDestinationLocation());
		successor.setClient(predecessor.getClient());
		successor.setExternalNumber(predecessor.getExternalNumber());
		successor.setOrderType(predecessor.getOrderType());

		manager.persist(successor);

		predecessor.setSuccessor(successor);

		fireTransportOrderStateChangeEvent(successor, -1);

		return successor;
	}

	public TransportOrder confirmOrder(TransportOrder order, UnitLoad destinationUnitLoad) throws BusinessException {
		String logStr = "confirmOrder ";
		logger.log(Level.FINE, logStr + "order=" + order + ", destinationUnitLoad=" + destinationUnitLoad);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Transport.orderAlreadyFinished");
		}

		UnitLoad unitLoad = order.getUnitLoad();
		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "Cannot confirm transport order without unit load. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.missingUnitLoad");
		}

		StorageLocation originalLocation = order.getDestinationLocation();
		locationReserver.deallocateLocation(originalLocation, unitLoad, true);

		User operator = userBusiness.getCurrentUser();

		order.setState(OrderState.FINISHED);
		order.setConfirmedDestination(destinationUnitLoad.getStorageLocation());
		order.setOperator(operator);

		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		if (stocksOnUnitLoad.size() != 1) {
			logger.log(Level.WARNING, logStr + "Cannot add mixed stock unit load to existing stock. order=" + order
					+ ", unitLoad=" + unitLoad);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.cannotHandleMoreThanOneStock");
		}
		StockUnit stock = stocksOnUnitLoad.get(0);
		inventoryBusiness.transferStock(stock, destinationUnitLoad, null, destinationUnitLoad.getState(),
				order.getOrderNumber(), operator, null);
		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public TransportOrder confirmOrder(TransportOrder order, StockUnit destinationStockUnit) throws BusinessException {
		String logStr = "confirmOrder ";
		logger.log(Level.FINE, logStr + "order=" + order + ", destinationStockUnit=" + destinationStockUnit);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.orderAlreadyFinished");
		}

		UnitLoad unitLoad = order.getUnitLoad();
		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "Cannot confirm transport order without unit load. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.missingUnitLoad");
		}

		StorageLocation originalLocation = order.getDestinationLocation();
		locationReserver.deallocateLocation(originalLocation, unitLoad, true);

		User operator = userBusiness.getCurrentUser();

		order.setState(OrderState.FINISHED);
		order.setDestinationLocation(destinationStockUnit.getUnitLoad().getStorageLocation());
		order.setOperator(operator);

		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		if (stocksOnUnitLoad.size() != 1) {
			logger.log(Level.WARNING, logStr + "Cannot add mixed stock unit load to existing stock. order=" + order
					+ ", unitLoad=" + unitLoad);
			throw new BusinessException(Wms2BundleResolver.class, "Relocate.cannotHandleMoreThanOneStock");
		}
		StockUnit stock = stocksOnUnitLoad.get(0);
		inventoryBusiness.transferStock(stock, destinationStockUnit, null, order.getOrderNumber(), operator, null);

		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	/**
	 * Delete a transport order. The reservation of the designated location is
	 * released.
	 */
	public void removeOrder(TransportOrder order) throws BusinessException {
		String logStr = "removeOrder ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		order.setState(OrderState.DELETABLE);

		if (order.getDestinationLocation() != null && orderState < OrderState.FINISHED) {
			locationReserver.deallocateLocation(order.getDestinationLocation(), order.getUnitLoad(), true);
		}

		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		try {
			manager.remove(order);

		} catch (Exception e) {
			logger.log(Level.WARNING,
					logStr + "Cannot remove order=" + order + ", EX=" + e.getClass().getName() + ", " + e.getMessage(),
					e);
			throw new BusinessException(Wms2BundleResolver.class, "Stocktaking.cannotDeleteOrder");
		}

	}

	/**
	 * Delete the reference to the unitLoad.<br>
	 * The unitLoadLabel will remain in the transport order. The reference to the
	 * UnitLoad entity can be removed to delete the UnitLoad.<br>
	 * No further operation of the order will be possible.
	 */
	public TransportOrder removeUnitLoadReference(TransportOrder order) throws BusinessException {
		String logStr = "removeUnitLoadReference ";
		logger.log(Level.FINE, logStr + "order=" + order + ", unitLoad=" + order.getUnitLoad());

		order.setUnitLoad(null);

		return order;
	}

	private void fireTransportOrderStateChangeEvent(TransportOrder entity, int oldState) throws BusinessException {
		logger.log(Level.FINE, "fireTransportOrderStateChangeEvent entity=" + entity + ", oldState=" + oldState
				+ ", newState=" + entity.getState());
		try {
			stateChangeEvent.fire(new TransportOrderStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireTransportOrderPartialProcessEvent(TransportOrder entity) throws BusinessException {
		logger.log(Level.FINE, "fireTransportOrderPartialProcessEvent entity=" + entity);
		try {
			partialProcessEvent.fire(new TransportOrderPartialProcessEvent(entity));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
