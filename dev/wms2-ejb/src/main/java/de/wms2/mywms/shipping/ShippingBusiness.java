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

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;

import org.mywms.model.Client;
import org.mywms.model.User;

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
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PacketStateChangeEvent;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.OrderState;
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
			int lineState = line.getState();

			if (lineState < OrderState.FINISHED) {
				line.setState(OrderState.CANCELED);
				fireShippingOrderLineStateChangeEvent(line, lineState);
			}
			if (lineState >= OrderState.FINISHED && lineState != OrderState.CANCELED) {
				shippingOrder.setState(OrderState.FINISHED);
			}
		}

		if (shippingOrder.getState() != orderState) {
			fireShippingOrderStateChangeEvent(shippingOrder, orderState);
		}

		// TODO krane shipping. add event listener to for callback to customer order
		return shippingOrder;
	}

	public ShippingOrderLine confirmLine(ShippingOrderLine line) throws BusinessException {
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
			// TODO krane shipping. add event listener to for callback to customer order
		}

		boolean rename = propertyBusiness.getBoolean(Wms2Properties.KEY_SHIPPING_RENAME_UNITLOAD, client, null, false);
		if (rename) {
			String addOn = "-" + unitLoad.getId();
			if (!unitLoad.getLabelId().endsWith(addOn)) {
				unitLoad.setLabelId(unitLoad.getLabelId() + addOn);
			}
		}

		StorageLocation destination = shippingOrder.getDestination();
		if (destination != null) {
			inventoryBusiness.transferUnitLoad(unitLoad, destination, shippingOrder.getOrderNumber(), null, null);
		}

		if (line.getState() != oldLineState) {
			fireShippingOrderLineStateChangeEvent(line, oldLineState);
		}

		return line;
	}

	/**
	 * Cancellation of a single picking position
	 * <p>
	 * The picking order is not affected or recalculated!
	 */
	public ShippingOrderLine cancelLine(ShippingOrderLine line) throws BusinessException {
		return line;
	}

	public ShippingOrder createOrder(DeliveryOrder deliveryOrder) throws BusinessException {
		String logStr = "createOrder ";
		if (deliveryOrder == null) {
			logger.warning(logStr + "Missing parameter deliveryOrder");
			return null;
		}
		logger.fine(logStr + "Create shipment for customer order number=" + deliveryOrder.getOrderNumber());

		ShippingOrder shippingOrder = null;

		List<Packet> packetList = packetService.readByDeliveryOrder(deliveryOrder);
		logger.fine(logStr + "Found unit loads. num=" + packetList.size());

		for (Packet packet : packetList) {
			if (packet.getState() < OrderState.PICKED) {
				logger.info(logStr + "UnitLoad not picked. packet=" + packet + ", state=" + packet.getState());
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
				shippingOrder = manager.createInstance(ShippingOrder.class);
				shippingOrder.setClient(deliveryOrder.getClient());
				shippingOrder.setOrderNumber(sequenceBusiness.readNextValue(Wms2Properties.SEQ_SHIPPING_ORDER));
				shippingOrder.setExternalNumber(deliveryOrder.getExternalNumber());
				shippingOrder.setDeliveryOrder(deliveryOrder);
				shippingOrder.setState(OrderState.CREATED);
				manager.persist(shippingOrder);
				manager.flush();

				fireShippingOrderStateChangeEvent(shippingOrder, -1);
			}

			ShippingOrderLine line = null;
			line = manager.createInstance(ShippingOrderLine.class);
			line.setPacket(packet);
			line.setShippingOrder(shippingOrder);
			line.setState(OrderState.PROCESSABLE);
			manager.persistValidated(line);
			manager.flush();

			fireShippingOrderLineStateChangeEvent(line, -1);
		}

		return shippingOrder;
	}

	public ShippingOrder createOrder(Client client, String externalNumber, StorageLocation destination,
			Date shippingDate) throws BusinessException {
		String logStr = "createOrder ";
		logger.fine(logStr + "Create shipment for shipment number=" + externalNumber);

		ShippingOrder shipment = null;

		shipment = manager.createInstance(ShippingOrder.class);

		shipment.setClient(client);
		shipment.setOrderNumber(sequenceBusiness.readNextValue(Wms2Properties.SEQ_SHIPPING_ORDER));
		shipment.setShippingDate(shippingDate);
		shipment.setExternalNumber(externalNumber);
		shipment.setDestination(destination);

		manager.persist(shipment);
		manager.flush();

		fireShippingOrderStateChangeEvent(shipment, -1);

		return shipment;
	}

	public ShippingOrderLine addLine(ShippingOrder order, Packet packet) throws BusinessException {
		String logStr = "addPosition ";
		List<ShippingOrder> orders = shippingOrderService.readByPacket(packet);

		for (ShippingOrder affectedOrder : orders) {
			if (affectedOrder.getState() < OrderState.FINISHED) {
				logger.warning(logStr + "There is already a shipping order for packet. packet=" + packet
						+ ", shippingOrder=" + affectedOrder);
				throw new BusinessException(Wms2BundleResolver.class, "Shipping.existsShippingOrder");
			}
		}
		ShippingOrderLine pos = manager.createInstance(ShippingOrderLine.class);
		pos.setPacket(packet);
		pos.setShippingOrder(order);
		pos.setState(OrderState.PROCESSABLE);

		manager.persistValidated(pos);

		return pos;
	}

	public void removeOrder(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "removeOrder ";
		int orderState = shippingOrder.getState();

		if (orderState < OrderState.FINISHED) {
			logger.log(Level.WARNING,
					logStr + "Order not yet finished. shippingOrder=" + shippingOrder + ", state=" + orderState);
			throw new BusinessException(Wms2BundleResolver.class, "Shipping.orderNotFinished");
		}

		for (ShippingOrderLine line : shippingOrder.getLines()) {
			manager.removeValidated(line);
		}
		manager.removeValidated(shippingOrder);
		manager.flush();
	}

	private void fireShippingOrderLineStateChangeEvent(ShippingOrderLine shippingOrderLine, int oldState)
			throws BusinessException {
		try {
			logger.fine("Fire ShippingOrderLineStateChangeEvent. shippingOrderLine=" + shippingOrderLine + ", state="
					+ shippingOrderLine.getState() + ", oldState=" + oldState);
			shippingOrderLineStateChangeEvent.fire(new ShippingOrderLineStateChangeEvent(shippingOrderLine, oldState));
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
			shippingOrderStateChangeEvent.fire(new ShippingOrderStateChangeEvent(shippingOrder, oldState));
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
			stockUnitStateChangeEvent.fire(new StockUnitStateChangeEvent(stockUnit, oldState));
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
			packetStateChangeEvent.fire(new PacketStateChangeEvent(packet, oldState));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
