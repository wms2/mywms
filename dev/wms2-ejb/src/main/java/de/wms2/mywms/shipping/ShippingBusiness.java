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
package de.wms2.mywms.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.wms2.mywms.address.Address;
import de.wms2.mywms.address.AddressEntityService;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitStateChangeEvent;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PacketStateChangeEvent;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStateCalculator;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;

/**
 * @author krane
 *
 */
public class ShippingBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private Event<ShippingOrderStateChangeEvent> shippingOrderStateChangeEvent;
	@Inject
	private Event<ShippingOrderLineStateChangeEvent> shippingOrderLineStateChangeEvent;
	@Inject
	private Event<StockUnitStateChangeEvent> stockUnitStateChangeEvent;
	@Inject
	private Event<PacketStateChangeEvent> packetStateChangeEvent;

	@Inject
	private PacketEntityService packetService;
	@Inject
	private ShippingOrderLineEntityService shippingOrderLineService;
	@Inject
	private ShippingOrderEntityService shippingOrderService;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private PacketEntityService packetEntityService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private AddressEntityService addressEntityService;
	@Inject
	private OrderStateCalculator orderStateCalculator;

	/**
	 * Release shipping order for operation
	 */
	public ShippingOrder releaseOperation(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "releaseOperation ";
		logger.log(Level.FINE, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING,
					logStr + "Order already done. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyFinished");
		}
		if (orderState < OrderState.CREATED) {
			logger.log(Level.WARNING,
					logStr + "Order not ready to start. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderNotYetProcessable");
		}
		if (orderState > OrderState.RESERVED && orderState != OrderState.PENDING) {
			logger.log(Level.INFO, logStr + "Order already started. do nothing. shippingOrder=" + shippingOrder
					+ ", state=" + orderState);
			return shippingOrder;
		}

		shippingOrder.setState(OrderState.PROCESSABLE);
		if (shippingOrder.getOperator() != null) {
			shippingOrder.setState(OrderState.RESERVED);
		}

		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}

		return shippingOrder;
	}

	public ShippingOrder reserveOperation(ShippingOrder shippingOrder, User operator) throws BusinessException {
		String logStr = "reserveOperation ";
		logger.log(Level.FINE, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState > OrderState.RESERVED) {
			logger.log(Level.WARNING,
					logStr + "Order already started. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyStarted");
		}
		if (orderState < OrderState.PROCESSABLE) {
			logger.log(Level.WARNING,
					logStr + "Order not yet processable. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderNotYetProcessable");
		}

		shippingOrder.setOperator(operator);

		if (operator != null && orderState < OrderState.RESERVED) {
			shippingOrder.setState(OrderState.RESERVED);
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}
		if (operator == null && orderState > OrderState.PROCESSABLE) {
			shippingOrder.setState(OrderState.PROCESSABLE);
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}

		return shippingOrder;
	}

	/**
	 * Start operation of order
	 */
	public ShippingOrder startOperation(ShippingOrder shippingOrder, User operator) throws BusinessException {
		String logStr = "startOperation ";
		logger.log(Level.FINE, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING, logStr + "Order already done. shippingOrder=" + shippingOrder);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyFinished");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		if (orderState > OrderState.RESERVED && orderState != OrderState.PENDING) {
			User ordersOperator = shippingOrder.getOperator();
			if (ordersOperator != null && !ordersOperator.equals(operator)) {
				logger.log(Level.WARNING, logStr + "Order started by different user. order=" + shippingOrder
						+ ", operator=" + shippingOrder.getOperator() + ", reserving operator=" + ordersOperator);
				throw new BusinessException(Wms2BundleResolver.class, "reservationMissmatch");
			}
			logger.log(Level.INFO, logStr + "Order already started. Ignore. order=" + shippingOrder);
			return shippingOrder;
		}

		shippingOrder.setOperator(operator);
		shippingOrder.setState(OrderState.STARTED);

		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}

		return shippingOrder;
	}

	/**
	 * Interrupt operation of order
	 * <p>
	 * - Order gets state.PENDING<br>
	 * - Lines get state.PROCESSABLE
	 */
	public ShippingOrder interruptOperation(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "interruptOperation ";
		logger.log(Level.FINE, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState > OrderState.FINISHED) {
			logger.log(Level.WARNING,
					logStr + "Order already picked. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyFinished");
		}
		if (orderState < OrderState.STARTED) {
			logger.log(Level.WARNING,
					logStr + "Order not yet started. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderNotYetStarted");
		}

		for (ShippingOrderLine line : shippingOrder.getLines()) {
			int pickState = line.getState();
			if (pickState >= OrderState.FINISHED) {
				continue;
			}
			if (pickState <= OrderState.PROCESSABLE) {
				continue;
			}

			line.setState(OrderState.PROCESSABLE);
			fireShippingOrderLineStateChangeEvent(line, pickState);
		}

		shippingOrder.setState(OrderState.PENDING);
		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}
		return shippingOrder;
	}

	/**
	 * Cancel operation of order
	 * <p>
	 * Order is sent back to the pool.
	 */
	public ShippingOrder cancelOperation(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "cancelOperation ";
		logger.log(Level.FINE, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState > OrderState.FINISHED) {
			logger.log(Level.WARNING,
					logStr + "Order already picked. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyFinished");
		}
		if (orderState < OrderState.PROCESSABLE) {
			logger.log(Level.WARNING,
					logStr + "Order not yet processable. shippingOrder=" + shippingOrder + ", state=" + orderState);
			return shippingOrder;
		}

		for (ShippingOrderLine line : shippingOrder.getLines()) {
			int lineState = line.getState();
			if (lineState >= OrderState.FINISHED) {
				continue;
			}
			if (lineState <= OrderState.PROCESSABLE) {
				continue;
			}

			line.setState(OrderState.PROCESSABLE);
			fireShippingOrderLineStateChangeEvent(line, lineState);
		}

		shippingOrder.setState(OrderState.PROCESSABLE);
		shippingOrder.setOperator(null);
		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}
		return shippingOrder;
	}

	public ShippingOrder pauseOrder(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "pauseOrder ";
		logger.log(Level.FINE, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState < OrderState.PAUSE) {
			logger.log(Level.WARNING,
					logStr + "Order not yet processable. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderNotYetProcessable");
		}
		if (orderState >= OrderState.STARTED) {
			logger.log(Level.WARNING,
					logStr + "Cannot pause started order. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyStarted");
		}

		shippingOrder.setState(OrderState.PAUSE);
		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}

		return shippingOrder;
	}

	/**
	 * Finishes a shipping order in the current state.<br>
	 * Not handled lines are canceled.
	 */
	public ShippingOrder finishOrder(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "finishOrder ";
		logger.log(Level.INFO, logStr + "shippingOrder=" + shippingOrder);

		int orderState = shippingOrder.getState();

		if (orderState >= OrderState.FINISHED) {
			logger.log(Level.WARNING,
					logStr + "Order already finished. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyFinished");
		}

		shippingOrder.setState(OrderState.CANCELED);
		for (ShippingOrderLine line : shippingOrder.getLines()) {
			Packet packet = line.getPacket();
			if (packet != null && packet.getPickingOrder() == null && packet.getDeliveryOrder() == null
					&& packet.getUnitLoad().getState() <= StockState.PICKED) {
				// This line has been manual created. Remove line and packet and reactivate unit
				// load
				removeLineInternal(line);
				continue;
			}
			int lineState = line.getState();

			if (lineState < OrderState.FINISHED) {
				line.setState(OrderState.CANCELED);
				fireShippingOrderLineStateChangeEvent(line, lineState);
			}
			if (lineState >= OrderState.FINISHED && lineState != OrderState.CANCELED) {
				shippingOrder.setState(OrderState.FINISHED);
			}
		}

		calculateWeight(shippingOrder);

		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}

		return shippingOrder;
	}

	public ShippingOrderLine confirmLine(ShippingOrderLine line, StorageLocation destination) throws BusinessException {
		ShippingOrder shippingOrder = line.getShippingOrder();
		Client client = shippingOrder.getClient();
		Packet packet = line.getPacket();
		UnitLoad unitLoad = packet.getUnitLoad();
		int oldLineState = line.getState();
		line.setState(OrderState.FINISHED);

		List<UnitLoad> childList = unitLoadService.readChilds(unitLoad);
		for (UnitLoad child : childList) {
			child.setState(StockState.SHIPPED);
			for (StockUnit stock : child.getStockUnitList()) {
				int stockState = stock.getState();
				if (stockState < StockState.SHIPPED) {
					stock.setState(StockState.SHIPPED);
					fireStockUnitStateChangeEvent(stock, stockState);
				}
			}
		}

		unitLoad.setState(StockState.SHIPPED);
		for (StockUnit stock : unitLoad.getStockUnitList()) {
			int stockState = stock.getState();
			if (stockState < StockState.SHIPPED) {
				stock.setState(StockState.SHIPPED);
				fireStockUnitStateChangeEvent(stock, stockState);
			}
		}

		int packetStateOld = packet.getState();
		if (packetStateOld < OrderState.SHIPPED) {
			packet.setState(OrderState.SHIPPED);
			firePacketStateChangeEvent(packet, packetStateOld);
		}

		boolean rename = propertyBusiness.getBoolean(Wms2Properties.KEY_SHIPPING_RENAME_UNITLOAD, client, null, false);
		if (rename) {
			String addOn = "-" + unitLoad.getId();
			if (!unitLoad.getLabelId().endsWith(addOn)) {
				unitLoad.setLabelId(unitLoad.getLabelId() + addOn);
			}
		}

		if (destination == null) {
			destination = shippingOrder.getDestination();
		}
		if (destination != null) {
			inventoryBusiness.transferUnitLoad(unitLoad, destination, shippingOrder.getOrderNumber(), null, null);
		}

		orderStateCalculator.calculateDeliveryOrderState(line);

		if (line.getState() != oldLineState) {
			fireShippingOrderLineStateChangeEvent(line, oldLineState);
		}

		return line;
	}

	public ShippingOrder createOrder(DeliveryOrder deliveryOrder) throws BusinessException {
		String logStr = "createOrder ";
		if (deliveryOrder == null) {
			logger.warning(logStr + "Missing parameter deliveryOrder");
			return null;
		}
		logger.fine(
				logStr + "Create shipping order for delivery order. deliveryOrder=" + deliveryOrder.getOrderNumber());

		ShippingOrder shippingOrder = null;

		List<Packet> packetList = packetService.readByDeliveryOrder(deliveryOrder);
		logger.fine(logStr + "Found unit loads. num=" + packetList.size());

		for (Packet packet : packetList) {
			if (packet.getState() < OrderState.PICKED) {
				logger.info(logStr + "Packet not finish picked. Do not use. packet=" + packet + ", state="
						+ packet.getState());
				continue;
			}
			if (packet.getState() >= OrderState.SHIPPED) {
				logger.info(logStr + "UnitLoad already shipped. packet=" + packet + ", state=" + packet.getState());
				continue;
			}

			List<ShippingOrderLine> existingLines = shippingOrderLineService.readList(null, packet, null,
					OrderState.UNDEFINED, OrderState.SHIPPED - 1);
			if (existingLines.size() > 0) {
				logger.info(logStr + "UnitLoad already in shipping order. packet=" + packet + ", shippingOrder="
						+ existingLines.get(0).getShippingOrder());
				continue;
			}

			if (shippingOrder == null) {
				shippingOrder = createOrder(deliveryOrder.getClient());
				shippingOrder.setDeliveryOrder(deliveryOrder);
				shippingOrder.setExternalNumber(deliveryOrder.getExternalNumber());
				shippingOrder.setAddress(deliveryOrder.getAddress());
				shippingOrder.setState(OrderState.PROCESSABLE);
			}

			addLineInternal(shippingOrder, packet);
		}

		calculateWeight(shippingOrder);

		return shippingOrder;
	}

	public ShippingOrder createOrder(Client client) throws BusinessException {
		StorageLocation destination = null;
		String destinationName = propertyBusiness.getString(Wms2Properties.KEY_SHIPPING_LOCATION, client, null, null);
		if (!StringUtils.isBlank(destinationName)) {
			destination = locationService.readByName(destinationName);
		}

		ShippingOrder shippingOrder = manager.createInstance(ShippingOrder.class);
		shippingOrder.setClient(client);
		shippingOrder.setOrderNumber(sequenceBusiness.readNextValue(ShippingOrder.class, "orderNumber"));
		shippingOrder.setShippingDate(new Date());
		shippingOrder.setDestination(destination);
		shippingOrder.setState(OrderState.PROCESSABLE);

		manager.persist(shippingOrder);
		manager.flush();

		fireShippingOrderStateChangeEvent(shippingOrder, -1);

		return shippingOrder;
	}

	public ShippingOrderLine addLine(ShippingOrder order, Packet packet) throws BusinessException {
		ShippingOrderLine line = addLineInternal(order, packet);
		calculateWeight(order);
		return line;
	}

	private ShippingOrderLine addLineInternal(ShippingOrder order, Packet packet) throws BusinessException {
		String logStr = "addLine ";
		List<ShippingOrder> orders = shippingOrderService.readByPacket(packet);

		for (ShippingOrder affectedOrder : orders) {
			if (affectedOrder.getState() < OrderState.FINISHED) {
				logger.warning(logStr + "There is already a shipping order for packet. packet=" + packet
						+ ", shippingOrder=" + affectedOrder);
				throw new BusinessException(Wms2BundleResolver.class, "Shipping.existsShippingOrder",
						packet.toString());
			}
		}
		ShippingOrderLine line = manager.createInstance(ShippingOrderLine.class);
		line.setPacket(packet);
		line.setShippingOrder(order);
		line.setState(OrderState.PROCESSABLE);

		manager.persistValidated(line);

		if (order.getLines() == null) {
			order.setLines(new ArrayList<>());
		}
		order.getLines().add(line);

		if (packet.getState() < OrderState.SHIPPING) {
			int oldState = packet.getState();
			packet.setState(OrderState.SHIPPING);
			firePacketStateChangeEvent(packet, oldState);
		}

		return line;
	}

	public ShippingOrderLine addLine(ShippingOrder order, UnitLoad unitLoad) throws BusinessException {
		Packet packet = packetEntityService.readFirstByUnitLoad(unitLoad);
		if (packet == null) {
			packet = packetEntityService.create(unitLoad);
			packet.setState(OrderState.SHIPPING);
		}
		if (unitLoad.getState() < StockState.PICKED) {
			unitLoad.setState(OrderState.PICKED);
		}
		return addLine(order, packet);
	}

	public void removeLine(ShippingOrderLine line) throws BusinessException {
		removeLineInternal(line);

		calculateWeight(line.getShippingOrder());
	}

	public void removeLineInternal(ShippingOrderLine line) throws BusinessException {
		// reset stock state for manual created shipping orders
		Packet packet = line.getPacket();
		ShippingOrder shippingOrder = line.getShippingOrder();

		int orderLineState = line.getState();
		line.setState(OrderState.DELETABLE);
		fireShippingOrderLineStateChangeEvent(line, orderLineState);

		manager.removeValidated(line);

		if (!shippingOrderLineService.existsByShippingOrder(shippingOrder)) {
			// remove empty order
			manager.removeValidated(shippingOrder);
		}

		if (packet.getPickingOrder() == null && packet.getDeliveryOrder() == null
				&& packet.getUnitLoad().getState() <= StockState.PICKED) {
			packet.getUnitLoad().setState(StockState.ON_STOCK);
			manager.removeValidated(packet);
		}
	}

	public void removeOrder(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "removeOrder ";
		int orderState = shippingOrder.getState();

		if (orderState < OrderState.FINISHED && orderState > OrderState.PROCESSABLE) {
			logger.log(Level.WARNING,
					logStr + "Order in progress. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderAlreadyStarted");
		}

		for (ShippingOrderLine line : shippingOrder.getLines()) {
			removeLineInternal(line);
		}
		manager.flush();
		shippingOrder.setLines(null);

		Address address = shippingOrder.getAddress();
		if (address != null) {
			shippingOrder.setAddress(null);
			addressEntityService.removeIfUnused(address);
		}

		manager.removeValidated(shippingOrder);
		manager.flush();
	}

	public void calculateWeight(ShippingOrder shippingOrder) {
		if (shippingOrder == null) {
			return;
		}
		shippingOrder.setWeight(null);

		String jpql = "select sum(line.packet.unitLoad.weight) from ";
		jpql += ShippingOrderLine.class.getName() + " line ";
		jpql += " where line.shippingOrder=:shippingOrder";
		TypedQuery<BigDecimal> query = manager.createQuery(jpql, BigDecimal.class);
		query.setParameter("shippingOrder", shippingOrder);

		try {
			BigDecimal weight = query.getSingleResult();
			shippingOrder.setWeight(weight);
		} catch (NoResultException e) {
		}
	}

	private void fireShippingOrderLineStateChangeEvent(ShippingOrderLine shippingOrderLine, int oldState)
			throws BusinessException {
		try {
			logger.fine("Fire ShippingOrderLineStateChangeEvent. shippingOrderLine=" + shippingOrderLine + ", state="
					+ shippingOrderLine.getState() + ", oldState=" + oldState);
			shippingOrderLineStateChangeEvent.fire(
					new ShippingOrderLineStateChangeEvent(shippingOrderLine, oldState, shippingOrderLine.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireShippingOrderStateChangeEvent(ShippingOrder shippingOrder, int oldState) throws BusinessException {
		try {
			logger.fine("Fire ShippingOrderStateChangeEvent. shippingOrder=" + shippingOrder + ", state="
					+ shippingOrder.getState() + ", oldState=" + oldState);
			shippingOrderStateChangeEvent
					.fire(new ShippingOrderStateChangeEvent(shippingOrder, oldState, shippingOrder.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireStockUnitStateChangeEvent(StockUnit stockUnit, int oldState) throws BusinessException {
		try {
			logger.fine("Fire StockUnitStateChangeEvent. stockUnit=" + stockUnit + ", state=" + stockUnit.getState()
					+ ", oldState=" + oldState);
			stockUnitStateChangeEvent.fire(new StockUnitStateChangeEvent(stockUnit, oldState, stockUnit.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void firePacketStateChangeEvent(Packet packet, int oldState) throws BusinessException {
		try {
			logger.fine("Fire PacketStateChangeEvent. packet=" + packet + ", state=" + packet.getState() + ", oldState="
					+ oldState);
			packetStateChangeEvent.fire(new PacketStateChangeEvent(packet, oldState, packet.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
