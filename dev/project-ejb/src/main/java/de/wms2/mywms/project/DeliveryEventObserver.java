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
package de.wms2.mywms.project;

import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderStateChangeEvent;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PacketStateChangeEvent;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.shipping.ShippingBusiness;
import de.wms2.mywms.shipping.ShippingOrder;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;

/**
 * This observer synchronizes the picking, packing and shipping processes.
 * 
 * @author krane
 *
 */
public class DeliveryEventObserver {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PacketEntityService packetEntityService;
	@Inject
	private Event<PacketStateChangeEvent> packetStateChangeEvent;
	@Inject
	private Event<DeliveryOrderStateChangeEvent> deliveryOrderStateChangeEvent;
	@Inject
	private ShippingBusiness shippingBusiness;

	public void listen(@Observes DeliveryOrderStateChangeEvent event) throws BusinessException {
		if (event == null || event.getDeliveryOrder() == null) {
			return;
		}

		DeliveryOrder deliveryOrder = event.getDeliveryOrder();
		int oldState = event.getOldState();
		if (oldState < OrderState.SHIPPING && deliveryOrder.getState() == OrderState.SHIPPING) {
			logger.info("DeliveryOrder got state SHIPPING. deliveryOrder=" + deliveryOrder);
			ShippingOrder shippingOrder = shippingBusiness.createOrder(deliveryOrder);
			if (shippingOrder != null) {
				shippingBusiness.releaseOperation(shippingOrder);
			}
		}
	}

	public void listen(@Observes PacketStateChangeEvent event) throws BusinessException {
		if (event == null || event.getPacket() == null) {
			return;
		}
		Packet packet = event.getPacket();
		int oldState = event.getOldState();
		if (oldState < OrderState.PICKED && packet.getState() == OrderState.PICKED) {
			logger.info("Packet got state PICKED. packet=" + packet + ", state=" + packet.getState() + ", oldState="
					+ oldState);
			int currentPacketState = packet.getState();

			// A packet has been picked
			// Find next operation for packet
			OrderStrategy orderStrategy = findStrategyShipping(packet);
			if (orderStrategy != null) {
				if (orderStrategy.isCreatePackingOrder()) {
					packet.setState(OrderState.PACKING);
				} else if (orderStrategy.isCreateShippingOrder()) {
					packet.setState(OrderState.SHIPPING);
				} else {
					packet.setState(OrderState.FINISHED);
				}
				firePacketStateChangeEvent(packet, currentPacketState);
			}
		}

		if (oldState < OrderState.PACKED && packet.getState() == OrderState.PACKED) {
			logger.info("Packet got state PACKED. packet=" + packet + ", state=" + packet.getState() + ", oldState="
					+ oldState);
			int currentPacketState = packet.getState();

			// A packet has been packed
			// Find next operation for packet
			OrderStrategy orderStrategy = findStrategyShipping(packet);
			if (orderStrategy != null) {
				if (orderStrategy.isCreateShippingOrder()) {
					packet.setState(OrderState.SHIPPING);
				} else {
					packet.setState(OrderState.FINISHED);
				}
				firePacketStateChangeEvent(packet, currentPacketState);
			}

			// Check delivery order state
			DeliveryOrder deliveryOrder = packet.getDeliveryOrder();
			if (deliveryOrder != null && deliveryOrder.getState() < OrderState.FINISHED
					&& isCompletePacked(deliveryOrder)) {
				int nextDeliveryOrderState = OrderState.FINISHED;
				if (orderStrategy.isCreateShippingOrder()) {
					nextDeliveryOrderState = OrderState.SHIPPING;
				}
				if (deliveryOrder.getState() < nextDeliveryOrderState) {
					int currentDeliveryOrderState = deliveryOrder.getState();
					deliveryOrder.setState(nextDeliveryOrderState);
					fireDeliveryOrderStateChangeEvent(deliveryOrder, currentDeliveryOrderState);
				}
			}
		}

		if (oldState < OrderState.SHIPPED && packet.getState() == OrderState.SHIPPED) {
			logger.info("Packet got state SHIPPED. packet=" + packet + ", state=" + packet.getState() + ", oldState="
					+ oldState);
			int currentPacketState = packet.getState();
			packet.setState(OrderState.FINISHED);
			firePacketStateChangeEvent(packet, currentPacketState);

			// Check delivery order state
			DeliveryOrder deliveryOrder = packet.getDeliveryOrder();
			if (deliveryOrder != null && deliveryOrder.getState() < OrderState.FINISHED
					&& isCompleteShipped(deliveryOrder)) {
				int currentDeliveryOrderState = deliveryOrder.getState();
				deliveryOrder.setState(OrderState.FINISHED);
				fireDeliveryOrderStateChangeEvent(deliveryOrder, currentDeliveryOrderState);
			}
		}

	}

	private OrderStrategy findStrategyShipping(Packet packet) {
		OrderStrategy orderStrategy = null;
		DeliveryOrder deliveryOrder = packet.getDeliveryOrder();
		if (deliveryOrder != null) {
			orderStrategy = deliveryOrder.getOrderStrategy();
		}
		if (orderStrategy == null) {
			PickingOrder pickingOrder = packet.getPickingOrder();
			if (pickingOrder != null) {
				orderStrategy = deliveryOrder.getOrderStrategy();
			}
		}
		return orderStrategy;
	}

	private boolean isCompletePacked(DeliveryOrder deliveryOrder) {
		if (deliveryOrder.getState() < OrderState.PICKED) {
			return false;
		}
		if (packetEntityService.exists(deliveryOrder, null, OrderState.PACKED - 1)) {
			return false;
		}
		return true;
	}

	private boolean isCompleteShipped(DeliveryOrder deliveryOrder) {
		if (deliveryOrder.getState() < OrderState.PICKED) {
			return false;
		}
		if (packetEntityService.exists(deliveryOrder, null, OrderState.SHIPPED - 1)) {
			return false;
		}
		return true;
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

	private void fireDeliveryOrderStateChangeEvent(DeliveryOrder deliveryOrder, int oldState) throws BusinessException {
		try {
			logger.fine("Fire DeliveryOrderStateChangeEvent. deliveryOrder=" + deliveryOrder + ", state=" + deliveryOrder.getState()
					+ ", oldState=" + oldState);
			deliveryOrderStateChangeEvent.fire(new DeliveryOrderStateChangeEvent(deliveryOrder, oldState));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
