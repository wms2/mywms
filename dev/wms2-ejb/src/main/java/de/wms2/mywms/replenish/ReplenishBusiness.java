/* 
Copyright 2019-2022 Matthias Krane
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
package de.wms2.mywms.replenish;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.User;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.TrashHandler;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.ItemDataArea;
import de.wms2.mywms.strategy.LocationReserver;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.transport.TransportOrderEntityService;
import de.wms2.mywms.transport.TransportOrderStateChangeEvent;
import de.wms2.mywms.transport.TransportOrderType;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;

/**
 * @author krane
 *
 */
@Stateless
public class ReplenishBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private Event<TransportOrderStateChangeEvent> transportOrderStateChangeEvent;
	@Inject
	private PersistenceManager manager;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private LocationReserver locationReserver;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private TransportOrderEntityService transportOrderService;
	@Inject
	private StockUnitEntityService stockUnitEntityService;
	@Inject
	private TrashHandler trashHandler;
	@Inject
	private SystemPropertyBusiness systemPropertyBusiness;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;

	public void cleanupDeleted() {
		String logStr = "cleanupDeleted ";
		logger.log(Level.FINE, logStr);

		List<TransportOrder> orders = transportOrderService.readList(null, null, null, TransportOrderType.REPLENISH,
				null, null, OrderState.DELETABLE, null);
		for (TransportOrder order : orders) {
			trashHandler.removeTransportOrder(order);
		}
		orders = transportOrderService.readList(null, null, null, TransportOrderType.REPLENISH_FIX, null, null,
				OrderState.DELETABLE, null);
		for (TransportOrder order : orders) {
			trashHandler.removeTransportOrder(order);
		}
		orders = transportOrderService.readList(null, null, null, TransportOrderType.REPLENISH_AREA, null, null,
				OrderState.DELETABLE, null);
		for (TransportOrder order : orders) {
			trashHandler.removeTransportOrder(order);
		}
	}

	public TransportOrder releaseOperation(TransportOrder order, User operator, Integer prio, String note)
			throws BusinessException {
		String logStr = "releaseOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyFinished");
		}
		if (orderState >= OrderState.PICKED) {
			logger.log(Level.WARNING, logStr + "Order already picked. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyPicked");
		}
		if (orderState > OrderState.RESERVED) {
			logger.log(Level.INFO, logStr + "Order already started. Ignore. order=" + order);
			return order;
		}

		if (prio != null) {
			order.setPrio(prio.intValue());
		}

		order.setOperator(operator);

		// do not kill notes in operating dialogs
		if (!StringUtils.isEmpty(note)) {
			order.setAdditionalContent(note);
		}

		order.setState(OrderState.PROCESSABLE);
		if (order.getOperator() != null) {
			order.setState(OrderState.RESERVED);
		}

		if (order.getState() != orderState) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public TransportOrder startOperation(TransportOrder order, User operator) throws BusinessException {
		String logStr = "startOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyFinished");
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

		StorageLocation location = order.getDestinationLocation();
		if (location != null) {
			if (location.isLocked()) {
				logger.log(Level.WARNING, logStr + "location locked. location=" + location);
				throw new BusinessException(Wms2BundleResolver.class, "Replenish.locationLocked");
			}
		}

		order.setState(OrderState.STARTED);
		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public TransportOrder cancelOperation(TransportOrder order) throws BusinessException {
		String logStr = "cancelOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState >= OrderState.PICKED) {
			logger.log(Level.WARNING, logStr + "Order already picked. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyPicked");
		}
		if (orderState < OrderState.PROCESSABLE) {
			logger.log(Level.INFO, logStr + "Order not yet processable. order=" + order);
			return order;
		}

		order.setOperator(null);
		order.setState(OrderState.PROCESSABLE);

		if (orderState != order.getState()) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public TransportOrder reserveOperation(TransportOrder order, User operator) throws BusinessException {
		String logStr = "reserveOperation ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (orderState > OrderState.RESERVED) {
			logger.log(Level.WARNING, logStr + "Order already started. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyStarted");
		}
		if (orderState < OrderState.PROCESSABLE) {
			logger.log(Level.WARNING, logStr + "Order not yet processable. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderNotYetProcessable");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		order.setOperator(operator);

		if (orderState < OrderState.RESERVED) {
			order.setState(OrderState.RESERVED);
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public TransportOrder pauseOrder(TransportOrder order) throws BusinessException {
		String logStr = "pauseOrder ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (order.getState() < OrderState.PAUSE) {
			logger.log(Level.WARNING, logStr + "Order not yet processable. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderNotYetProcessable");
		}
		if (order.getState() >= OrderState.STARTED) {
			logger.log(Level.WARNING, logStr + "Order already started. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyStarted");
		}

		order.setState(OrderState.PAUSE);
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
			logger.log(Level.INFO, logStr + "Order already finished. order=" + order);
			return order;
		}

		StockUnit sourceStock = manager.reload(StockUnit.class, order.getSourceStockUnitId());
		if (sourceStock != null) {
			if (order.getDestinationLocation() != null) {
				locationReserver.deallocateLocation(order.getDestinationLocation(), sourceStock.getUnitLoad(), true);
			}
		}

		order.setState(OrderState.CANCELED);
		if (order.getState() != orderState) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	public void removeOrder(TransportOrder order) throws BusinessException {
		String logStr = "removeOrder ";
		logger.log(Level.INFO, logStr + "order=" + order);

		if (order.getState() >= OrderState.STARTED && order.getState() < OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already started. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyStarted");
		}

		cancelOrder(order);

		manager.remove(order);
	}

	public TransportOrder resetOrder(TransportOrder order) throws BusinessException {
		String logStr = "resetOrder ";
		logger.log(Level.FINE, logStr + "order=" + order);

		int orderState = order.getState();

		if (order.getState() >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyFinished");
		}
		if (order.getState() < OrderState.PROCESSABLE) {
			logger.log(Level.WARNING, logStr + "Order not yet processable. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderNotYetProcessable");
		}

		order.setOperator(null);
		order.setState(OrderState.PROCESSABLE);
		if (order.getState() != orderState) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	/**
	 * @param order           The order to confirm
	 * @param sourceStockUnit Optional. If null the sourceStockUnit of the order is
	 *                        used.
	 * @param toLocation      Optional. If null the destination of the order is
	 *                        used.
	 * @param amount          Optional. If null the complete source stock is used.
	 */
	public TransportOrder confirmOrder(TransportOrder order, StockUnit sourceStockUnit, StorageLocation toLocation,
			BigDecimal amount, String note) throws BusinessException {
		String logStr = "confirmOrder ";

		User operator = userBusiness.getCurrentUser();
		logger.log(Level.FINE, logStr + "order=" + order + ", operator=" + operator);

		int orderState = order.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already finished. order=" + order);
			throw new BusinessException(Wms2BundleResolver.class, "Replenish.orderAlreadyFinished");
		}

		if (sourceStockUnit == null) {
			sourceStockUnit = manager.reload(StockUnit.class, order.getSourceStockUnitId());
			if (sourceStockUnit == null) {
				logger.log(Level.WARNING, "No source stock given. order=" + order);
				throw new BusinessException(Wms2BundleResolver.class, "Replenish.missingSourceStock");
			}
		}

		if (toLocation == null) {
			toLocation = order.getDestinationLocation();
			if (toLocation == null) {
				logger.log(Level.SEVERE, "No destination location given. order=" + order);
				throw new BusinessException(Wms2BundleResolver.class, "Replenish.missingDestinationLocation");
			}
		}

		if (amount == null) {
			amount = sourceStockUnit.getAmount();
		}
		order.setConfirmedAmount(amount);

		String activityCode = order.getOrderNumber();

		boolean useCompleteSource = (amount.compareTo(sourceStockUnit.getAmount()) == 0);
		if (useCompleteSource) {
			// do not move mixed source
			List<StockUnit> stocksOnUnitLoad = stockUnitEntityService.readByUnitLoad(sourceStockUnit.getUnitLoad());
			if (stocksOnUnitLoad.size() != 1) {
				useCompleteSource = false;
			}
		}

		if (useCompleteSource) {
			inventoryBusiness.transferUnitLoad(sourceStockUnit.getUnitLoad(), toLocation, order.getOrderNumber(),
					operator, null);

			// Aggregate stocks on a picking location
			// The old stock on the location is added to the new stock
			// Find stock to add the amount
			List<StockUnit> addToStocks = stockUnitEntityService.readList(sourceStockUnit.getClient(),
					sourceStockUnit.getItemData(), sourceStockUnit.getLotNumber(), null, toLocation,
					sourceStockUnit.getSerialNumber(), null, null);
			for (StockUnit addToStock : addToStocks) {
				if (addToStock.getUnitLoad().equals(sourceStockUnit.getUnitLoad())) {
					continue;
				}
				if (inventoryBusiness.isSummableItem(sourceStockUnit, sourceStockUnit.getAmount(), addToStock,
						addToStock.getAmount())) {
					Date strategyDate = sourceStockUnit.getStrategyDate();
					inventoryBusiness.transferStock(addToStock, sourceStockUnit, null, activityCode, operator, null);
					inventoryBusiness.transferReservation(addToStock, sourceStockUnit);
					sourceStockUnit.setStrategyDate(strategyDate);
				}
			}

		} else {
			UnitLoad addToUnitLoad = null;

			// find a unit load to add
			List<StockUnit> stocksOnLocation = stockUnitEntityService.readByLocation(toLocation);
			for (StockUnit stockOnLocation : stocksOnLocation) {
				if (inventoryBusiness.isSummableItem(sourceStockUnit, null, stockOnLocation, null)) {
					logger.log(Level.FINE,
							logStr + "Reuse target unit load. unitLoad=" + stockOnLocation.getUnitLoad());
					addToUnitLoad = stockOnLocation.getUnitLoad();
					break;
				}
			}
			if (addToUnitLoad == null) {
				logger.log(Level.FINE, logStr + "generate new unit load on target location");
				UnitLoadType unitLoadType = unitLoadTypeService.getVirtual();
				String label = sequenceBusiness.readNextValue(UnitLoad.class, "labelId");
				addToUnitLoad = inventoryBusiness.createUnitLoad(order.getClient(), label, unitLoadType, toLocation,
						sourceStockUnit.getState(), activityCode, operator, null);
			}

			inventoryBusiness.transferStock(sourceStockUnit, addToUnitLoad, amount, StockState.ON_STOCK, activityCode,
					operator, null);
		}

		locationReserver.deallocateLocation(toLocation, sourceStockUnit.getUnitLoad(), false);

		// do not kill notes in operating dialogs
		if (!StringUtils.isEmpty(note)) {
			order.setAdditionalContent(note);
		}

		order.setState(OrderState.FINISHED);
		if (order.getState() != orderState) {
			fireTransportOrderStateChangeEvent(order, orderState);
		}

		return order;
	}

	@SuppressWarnings("unchecked")
	public int refillFixAssignments() throws BusinessException {
		String logStr = "refillFixAssignments ";
		logger.log(Level.FINE, logStr);

		// Select fix assignments with stock
		String jpql = "SELECT fix, sum(stock.amount) FROM ";
		jpql += FixAssignment.class.getName() + " fix, ";
		jpql += StockUnit.class.getName() + " stock";
		jpql += " WHERE fix.minAmount>=0";
		jpql += " and stock.unitLoad.storageLocation=fix.storageLocation";
		jpql += " and stock.itemData=fix.itemData";
		jpql += " and not exists(select 1 from " + TransportOrder.class.getName() + " transportOrder ";
		jpql += "   where transportOrder.destinationLocation=fix.storageLocation";
		jpql += "   and transportOrder.itemData=fix.itemData";
		jpql += "   and transportOrder.state<" + OrderState.FINISHED + ")";
		jpql += " group by fix ";
		jpql += " having sum(stock.amount) < fix.minAmount";
		Query query = manager.createQuery(jpql);

		List<Object[]> results = query.getResultList();
		Set<FixAssignment> checkableFixAssignments = new HashSet<>();
		for (Object[] result : results) {
			FixAssignment fix = (FixAssignment) result[0];
			checkableFixAssignments.add(fix);
		}

		// Select fix assignments without stock
		jpql = "SELECT fix FROM ";
		jpql += FixAssignment.class.getName() + " fix ";
		jpql += " WHERE fix.minAmount>=0";
		jpql += " and not exists(select 1 from " + StockUnit.class.getName() + " stock ";
		jpql += "   where stock.unitLoad.storageLocation=fix.storageLocation";
		jpql += "   and stock.itemData=fix.itemData)";
		jpql += " and exists(select 1 from " + StockUnit.class.getName() + " stock ";
		jpql += "   where stock.itemData=fix.itemData ";
		jpql += "   and stock.state=" + StockState.ON_STOCK + ")";
		jpql += " and not exists(select 1 from " + TransportOrder.class.getName() + " transportOrder ";
		jpql += "   where transportOrder.destinationLocation=fix.storageLocation";
		jpql += "   and transportOrder.itemData=fix.itemData";
		jpql += "   and transportOrder.state<" + OrderState.FINISHED + ")";
		query = manager.createQuery(jpql);

		List<FixAssignment> emptyFixAssignments = query.getResultList();
		checkableFixAssignments.addAll(emptyFixAssignments);

		logger.log(Level.INFO, logStr + "Checkable locations. size=" + checkableFixAssignments.size());

		return refillFixAssignments(checkableFixAssignments);
	}

	public int refillFixAssignments(Collection<FixAssignment> fixAssignments) throws BusinessException {
		String logStr = "refillFixAssignments ";
		logger.log(Level.FINE, logStr);

		int numOrders = 0;
		for (FixAssignment fix : fixAssignments) {
			// First iteration for not picking location
			// Fist fill up reserve material
			Area area = fix.getStorageLocation().getArea();
			if (area == null) {
				continue;
			}
			boolean isPicking = StringUtils.contains(area.getUsages(), AreaUsages.PICKING);
			boolean isStorage = StringUtils.contains(area.getUsages(), AreaUsages.STORAGE);
			if (!isPicking && !isStorage) {
				continue;
			}

			List<TransportOrder> existingOrders = transportOrderService.readOpen(fix.getStorageLocation(),
					fix.getItemData());
			if (!existingOrders.isEmpty()) {
				logger.log(Level.INFO, logStr + "Order already exists. location=" + fix.getStorageLocation()
						+ ", itemData=" + fix.getItemData());
				continue;
			}

			// The selected assignments are only candidates
			// There may be some other reasons not to put replenish to these locations
			logger.log(Level.FINE,
					logStr + "Check location " + fix.getStorageLocation() + ", itemData=" + fix.getItemData());

			BigDecimal sumAmount = BigDecimal.ZERO;
			Set<String> lotsOnLocation = new HashSet<>();
			List<StockUnit> stocksOnLocation = stockUnitEntityService.readByItemDataLocation(fix.getItemData(),
					fix.getStorageLocation());
			for (StockUnit stock : stocksOnLocation) {
				sumAmount = sumAmount.add(stock.getAmount());
				if (!StringUtils.isBlank(stock.getLotNumber())) {
					lotsOnLocation.add(stock.getLotNumber());
				}
			}
			if (fix.getMinAmount() == null || sumAmount.compareTo(fix.getMinAmount()) > 0) {
				logger.log(Level.FINE, logStr + "Enough amount on location " + fix.getStorageLocation() + ", itemData="
						+ fix.getItemData());
				continue;
			}

			TransportOrder order = null;
			if (isPicking) {
				order = generateOrderForPicking(fix.getItemData(), fix.getStorageLocation(), null, sumAmount,
						lotsOnLocation);
			}
			if (order == null && isStorage) {
				order = generateOrderForStorage(fix.getItemData(), fix.getStorageLocation(), null, sumAmount,
						lotsOnLocation);
			}
			if (order != null) {
				numOrders++;
			}
		}

		return numOrders;
	}

	public TransportOrder generateOrder(ItemData itemData, BigDecimal requestedAmount, StorageLocation location)
			throws BusinessException {
		BigDecimal sumAmount = BigDecimal.ZERO;
		Set<String> lotSet = new HashSet<>();
		List<StockUnit> locationStockList = stockUnitEntityService.readByItemDataLocation(itemData, location);
		for (StockUnit stock : locationStockList) {
			sumAmount = sumAmount.add(stock.getAmount());
			if (!StringUtils.isBlank(stock.getLotNumber())) {
				lotSet.add(stock.getLotNumber());
			}
		}

		return generateOrderForPicking(itemData, location, requestedAmount, sumAmount, lotSet);
	}

	private TransportOrder generateOrderForPicking(ItemData itemData, StorageLocation location,
			BigDecimal requestedAmount, BigDecimal amountOnLocation, Collection<String> lotsOnLocation)
			throws BusinessException {
		String logStr = "generateOrderForPicking ";
		logger.log(Level.INFO, logStr + "itemData=" + itemData + ", location=" + location + ", requestedAmount="
				+ requestedAmount + ", amountOnLocation=" + amountOnLocation + ", lotsOnLocation=" + lotsOnLocation);

		StockUnit sourceStock = findReplenishStockForPicking(itemData, lotsOnLocation);
		if (sourceStock == null) {
			logger.log(Level.INFO, logStr + "No replenish stock available. location=" + location + ", itemData="
					+ itemData + ", lotsOnLocation=" + lotsOnLocation);
			return null;
		}

		String orderNumber = sequenceBusiness.readNextValue(TransportOrder.class, "orderNumber");
		TransportOrder order = manager.createInstance(TransportOrder.class);
		order.setClient(sourceStock.getClient());
		order.setOrderNumber(orderNumber);
		order.setItemData(itemData);
		order.setState(OrderState.PROCESSABLE);
		order.setDestinationLocation(location);
		order.setSourceStockUnitId(sourceStock.getId());
		order.setSourceLocation(sourceStock.getUnitLoad().getStorageLocation());
		order.setUnitLoad(sourceStock.getUnitLoad());
		order.setUnitLoadLabel(sourceStock.getUnitLoad().getLabelId());
		order.setAmount(requestedAmount);
		order.setOrderType(TransportOrderType.REPLENISH_FIX);
		manager.persist(order);

		locationReserver.reserveLocation(location, sourceStock.getUnitLoad(), TransportOrder.class, order.getId(),
				order.getOrderNumber());

		return order;
	}

	private StockUnit findReplenishStockForPicking(ItemData itemData, Collection<String> lotsOnLocation) {
		String logStr = "findReplenishStockForPicking ";
		boolean replenishFromPicking = systemPropertyBusiness.getBoolean(Wms2Properties.KEY_REPLENISH_FROM_PICKING,
				false);

		// a) search stock on fixed storage locations
		String jpql = " SELECT stock FROM ";
		jpql += StockUnit.class.getName() + " stock, ";
		jpql += StorageLocation.class.getName() + " location, ";
		jpql += Area.class.getName() + " area ";
		jpql += "WHERE location = stock.unitLoad.storageLocation AND area = location.area ";
		jpql += " AND location.lock=0 ";
		jpql += " AND stock.lock=0 and stock.unitLoad.lock=0";
		jpql += " AND stock.amount > 0 ";

		// Use only stock state ON_STOCK
		jpql += " AND stock.state=" + StockState.ON_STOCK;

		jpql += " AND not area.usages like '%" + AreaUsages.PICKING + "%' ";
		jpql += " AND area.usages like '%" + AreaUsages.STORAGE + "%' ";
		jpql += " AND exists( select 1 from " + FixAssignment.class.getName() + " fix ";
		jpql += "     where fix.storageLocation = location )";
		jpql += " AND stock.reservedAmount < stock.amount ";
		jpql += " AND stock.itemData =:itemData ";
		if (lotsOnLocation != null && lotsOnLocation.size() > 0) {
			jpql += " AND stock.lotNumber in (:lots) ";
		}
		jpql += " AND not exists( select 1 from " + TransportOrder.class.getName() + " transport ";
		jpql += "     where transport.unitLoad=stock.unitLoad and transport.state<" + OrderState.FINISHED + ")";
		jpql += " ORDER BY stock.strategyDate, stock.amount, stock.created, stock.id ";
		Query query = manager.createQuery(jpql);

		query.setParameter("itemData", itemData);
		if (lotsOnLocation != null && lotsOnLocation.size() > 0) {
			query.setParameter("lots", lotsOnLocation);
		}
		query.setMaxResults(1);

		try {
			StockUnit stock = (StockUnit) query.getSingleResult();
			logger.log(Level.INFO, logStr + "Found stock on fixed replenish location. itemData=" + itemData
					+ ", location=" + stock.getUnitLoad().getStorageLocation());
			return stock;
		} catch (Throwable t) {
		}

		// b) search stock on every location
		jpql = " SELECT stock FROM ";
		jpql += StockUnit.class.getName() + " stock, ";
		jpql += StorageLocation.class.getName() + " location, ";
		jpql += Area.class.getName() + " area ";
		jpql += "WHERE location = stock.unitLoad.storageLocation AND area = location.area ";
		jpql += " AND location.lock=0 ";
		jpql += " AND stock.lock=0 ";
		jpql += " AND stock.amount > 0 ";
		jpql += " AND not exists( select 1 from " + FixAssignment.class.getName() + " fix ";
		jpql += "     where fix.storageLocation = location )";
		if (replenishFromPicking) {
			jpql += " AND (";
			jpql += "   area.usages like '%" + AreaUsages.PICKING + "%' ";
			jpql += "   or area.usages like '%" + AreaUsages.STORAGE + "%' ";
			jpql += " )";
		} else {
			jpql += " AND area.usages like '%" + AreaUsages.STORAGE + "%' ";
			jpql += " AND not area.usages like '%" + AreaUsages.PICKING + "%' ";
		}
		jpql += " AND stock.reservedAmount < stock.amount ";
		jpql += " AND stock.itemData =:itemData ";
		if (lotsOnLocation != null && lotsOnLocation.size() > 0) {
			jpql += " AND stock.lotNumber in (:lots) ";
		}
		jpql += " AND not exists( select 1 from " + TransportOrder.class.getName() + " transport ";
		jpql += "     where transport.unitLoad=stock.unitLoad and transport.state<" + OrderState.FINISHED + ")";
		jpql += " ORDER BY stock.strategyDate, stock.amount, stock.created, stock.id ";
		query = manager.createQuery(jpql);

		query.setParameter("itemData", itemData);
		if (lotsOnLocation != null && lotsOnLocation.size() > 0) {
			query.setParameter("lots", lotsOnLocation);
		}
		query.setMaxResults(1);

		try {
			StockUnit stock = (StockUnit) query.getSingleResult();
			logger.log(Level.INFO, logStr + "Found stock on replenish location. itemData=" + itemData + ", location="
					+ stock.getUnitLoad().getStorageLocation());
			return stock;
		} catch (Throwable t) {
		}

		return null;

	}

	private TransportOrder generateOrderForStorage(ItemData itemData, StorageLocation location,
			BigDecimal requestedAmount, BigDecimal amountOnLocation, Collection<String> lotsOnLocation)
			throws BusinessException {
		String logStr = "generateOrderForStorage ";
		logger.log(Level.INFO, logStr + "itemData=" + itemData + ", location=" + location + ", requestedAmount="
				+ requestedAmount + ", amountOnLocation=" + amountOnLocation + ", lotsOnLocation=" + lotsOnLocation);

		StockUnit sourceStock = findReplenishStockForStorage(itemData, lotsOnLocation);
		if (sourceStock == null) {
			logger.log(Level.INFO, logStr + "No replenish stock available. itemData=" + itemData + ", location="
					+ location + ", lotsOnLocation=" + lotsOnLocation);
			return null;
		}

		String orderNumber = sequenceBusiness.readNextValue(TransportOrder.class, "orderNumber");
		TransportOrder order = manager.createInstance(TransportOrder.class);
		order.setClient(sourceStock.getClient());
		order.setOrderNumber(orderNumber);
		order.setItemData(itemData);
		order.setDestinationLocation(location);
		order.setSourceStockUnitId(sourceStock.getId());
		order.setSourceLocation(sourceStock.getUnitLoad().getStorageLocation());
		order.setUnitLoad(sourceStock.getUnitLoad());
		order.setUnitLoadLabel(sourceStock.getUnitLoad().getLabelId());
		;
		order.setAmount(requestedAmount);
		order.setState(OrderState.PROCESSABLE);
		order.setOrderType(TransportOrderType.REPLENISH_FIX);
		manager.persist(order);

		locationReserver.reserveLocation(location, sourceStock.getUnitLoad(), TransportOrder.class, order.getId(),
				order.getOrderNumber());

		return order;
	}

	private StockUnit findReplenishStockForStorage(ItemData itemData, Collection<String> lotsOnLocation) {
		String jpql = " SELECT stock FROM ";
		jpql += StockUnit.class.getName() + " stock, ";
		jpql += StorageLocation.class.getName() + " location, ";
		jpql += Area.class.getName() + " area ";
		jpql += "WHERE location = stock.unitLoad.storageLocation AND area = location.area ";
		jpql += " AND location.lock=0";
		jpql += " AND stock.lock=0 and stock.unitLoad.lock=0";
		jpql += " AND stock.amount > 0 ";
		// Do not use picking locations
		jpql += " AND not area.usages like '%" + AreaUsages.PICKING + "%' ";
		// Do only use storage locations
		jpql += " AND area.usages like '%" + AreaUsages.STORAGE + "%' ";
		// Do not use fixed locations
		jpql += " AND not exists( select 1 from " + FixAssignment.class.getName() + " fix ";
		jpql += "     where fix.storageLocation = location )";
		// Stock must be complete unreserved
		jpql += " AND stock.reservedAmount = 0 ";
		jpql += " AND stock.itemData =:itemData ";
		// Do not use mixed unit loads
		jpql += " AND not exists( select 1 from " + StockUnit.class.getName() + " stock2 ";
		jpql += "     where stock2.unitLoad = stock.unitLoad and stock2.itemData!=stock.itemData )";
		// Do not use unit loads with transports
		jpql += " AND not exists( select 1 from " + TransportOrder.class.getName() + " transport ";
		jpql += "     where transport.unitLoad=stock.unitLoad and transport.state<" + OrderState.FINISHED + ")";
		// Use only stock state ON_STOCK
		jpql += " AND stock.state=" + StockState.ON_STOCK;

		if (lotsOnLocation != null && lotsOnLocation.size() > 0) {
			jpql += " AND stock.lotNumber in (:lots) ";
		}
		jpql += " ORDER BY stock.strategyDate, stock.amount, stock.created, stock.id ";
		Query query = manager.createQuery(jpql);

		query.setParameter("itemData", itemData);
		if (lotsOnLocation != null && lotsOnLocation.size() > 0) {
			query.setParameter("lots", lotsOnLocation);
		}
		query.setMaxResults(1);

		try {
			StockUnit stock = (StockUnit) query.getSingleResult();
			return stock;
		} catch (Throwable t) {
		}
		return null;

	}

	private void fireTransportOrderStateChangeEvent(TransportOrder entity, int oldState) throws BusinessException {
		try {
			transportOrderStateChangeEvent
					.fire(new TransportOrderStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}
}
