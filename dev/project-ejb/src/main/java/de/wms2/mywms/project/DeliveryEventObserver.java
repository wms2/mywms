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
import de.wms2.mywms.picking.PickingOrderStateChangeEvent;
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
	@Inject
	private PacketEntityService packetService;

	public void listen(@Observes DeliveryOrderStateChangeEvent event) throws BusinessException {
		if (event == null || event.getDeliveryOrder() == null) {
			return;
		}

		DeliveryOrder deliveryOrder = event.getDeliveryOrder();
		int oldState = event.getOldState();
		int newState = event.getNewState();

		if (oldState < OrderState.PICKED && newState == OrderState.PICKED) {
			logger.info("DeliveryOrder got state PICKED. deliveryOrder=" + deliveryOrder + ", oldState=" + oldState
					+ ", newState=" + newState);

			OrderStrategy orderStrategy = deliveryOrder.getOrderStrategy();

			if (orderStrategy.isCreatePackingOrder()) {
				logger.info("Create packing order for delivery deliveryOrder=" + deliveryOrder + ", orderStrategy="
						+ orderStrategy);
				deliveryOrder.setState(OrderState.PACKING);
			} else if (orderStrategy.isCreateShippingOrder()) {
				// Check all picked packets. Only trigger shipping order generation if all
				// packets are finished
				boolean hasUnfinishedPacket = packetService.exists(deliveryOrder, null, OrderState.PICKED - 1);
				if (hasUnfinishedPacket) {
					logger.info("Has not finish Packet. Wait with shipping order generation. deliveryOrder="
							+ deliveryOrder);
				} else {
					logger.info("Create shipping order for delivery deliveryOrder=" + deliveryOrder + ", orderStrategy="
							+ orderStrategy);
					deliveryOrder.setState(OrderState.SHIPPING);
					ShippingOrder shippingOrder = shippingBusiness.createOrder(deliveryOrder);
					if (shippingOrder != null) {
						shippingBusiness.releaseOperation(shippingOrder);
					}
				}
			} else {
				logger.info("Finish delivery deliveryOrder=" + deliveryOrder + ", orderStrategy=" + orderStrategy);
				deliveryOrder.setState(OrderState.FINISHED);
			}
			if (deliveryOrder.getState() != OrderState.PICKED) {
				fireDeliveryOrderStateChangeEvent(deliveryOrder, OrderState.PICKED);
			}
		}

		if (oldState < OrderState.PACKED && newState == OrderState.PACKED) {
			logger.info("DeliveryOrder got state PACKED. deliveryOrder=" + deliveryOrder + ", oldState=" + oldState
					+ ", newState=" + newState);

			OrderStrategy orderStrategy = deliveryOrder.getOrderStrategy();

			if (orderStrategy.isCreateShippingOrder()) {
				logger.info("Create shipping order for delivery deliveryOrder=" + deliveryOrder + ", orderStrategy="
						+ orderStrategy);
				deliveryOrder.setState(OrderState.SHIPPING);
				ShippingOrder shippingOrder = shippingBusiness.createOrder(deliveryOrder);
				if (shippingOrder != null) {
					shippingBusiness.releaseOperation(shippingOrder);
				}
			} else {
				logger.info("Finish delivery deliveryOrder=" + deliveryOrder + ", orderStrategy=" + orderStrategy);
				deliveryOrder.setState(OrderState.FINISHED);
			}
			fireDeliveryOrderStateChangeEvent(deliveryOrder, OrderState.PACKED);
		}

		if (oldState < OrderState.SHIPPED && newState == OrderState.SHIPPED) {
			logger.info("DeliveryOrder got state SHIPPED. deliveryOrder=" + deliveryOrder + ", oldState=" + oldState
					+ ", newState=" + newState);

			logger.info("Finish delivery deliveryOrder=" + deliveryOrder);
			deliveryOrder.setState(OrderState.FINISHED);
			fireDeliveryOrderStateChangeEvent(deliveryOrder, OrderState.SHIPPED);
		}

	}

	public void listen(@Observes PickingOrderStateChangeEvent event) throws BusinessException {
		if (event == null || event.getPickingOrder() == null) {
			return;
		}

		PickingOrder pickingOrder = event.getPickingOrder();
		int oldState = event.getOldState();
		int newState = event.getNewState();
		if (oldState < OrderState.FINISHED && newState == OrderState.FINISHED) {
			logger.info("PickingOrder got state FINISHED. pickingOrder=" + pickingOrder + ", oldState=" + oldState
					+ ", newState=" + newState);
			DeliveryOrder deliveryOrder = pickingOrder.getDeliveryOrder();
			if (deliveryOrder == null) {
				// Standalone picking order.
				// Generate shipping oder if strategy flag is set.
				// Packing is not defined for picking orders without delivery order.
				OrderStrategy orderStrategy = pickingOrder.getOrderStrategy();

				if (orderStrategy.isCreateShippingOrder()) {
					logger.info("Create shipping order for pickingOrder=" + pickingOrder + ", orderStrategy="
							+ orderStrategy);
					ShippingOrder shippingOrder = null;
					for (Packet packet : pickingOrder.getPackets()) {
						if (packet.getState() >= OrderState.SHIPPING) {
							logger.info("Packet alread in shipping. packet=" + packet + ", pickingOrder=" + pickingOrder
									+ ", orderStrategy=" + orderStrategy);
							continue;
						}
						if (shippingOrder == null) {
							shippingOrder = shippingBusiness.createOrder(pickingOrder.getClient());
							shippingOrder.setAddress(pickingOrder.getAddress());
							shippingOrder.setExternalNumber(pickingOrder.getExternalNumber());
						}
						shippingBusiness.addLine(shippingOrder, packet);
					}
					if (shippingOrder != null) {
						shippingBusiness.releaseOperation(shippingOrder);
					}
				}
			} else {
				// If delivery order is in state PICKED the next step can be started.
				int deliveryOrderState = deliveryOrder.getState();
				if (deliveryOrderState == OrderState.PICKED) {
					OrderStrategy orderStrategy = deliveryOrder.getOrderStrategy();

					if (orderStrategy.isCreatePackingOrder()) {
						logger.info("Create packing order for delivery deliveryOrder=" + deliveryOrder
								+ ", orderStrategy=" + orderStrategy);
						deliveryOrder.setState(OrderState.PACKING);
					} else if (orderStrategy.isCreateShippingOrder()) {
						logger.info("Create shipping order for delivery deliveryOrder=" + deliveryOrder
								+ ", orderStrategy=" + orderStrategy);
						deliveryOrder.setState(OrderState.SHIPPING);
						ShippingOrder shippingOrder = shippingBusiness.createOrder(deliveryOrder);
						if (shippingOrder != null) {
							shippingBusiness.releaseOperation(shippingOrder);
						}
					} else {
						logger.info(
								"Finish delivery deliveryOrder=" + deliveryOrder + ", orderStrategy=" + orderStrategy);
						deliveryOrder.setState(OrderState.FINISHED);
					}
					fireDeliveryOrderStateChangeEvent(deliveryOrder, OrderState.PICKED);
				}
			}

		}

	}

	public void listen(@Observes PacketStateChangeEvent event) throws BusinessException {
		if (event == null || event.getPacket() == null) {
			return;
		}
		Packet packet = event.getPacket();
		int oldState = event.getOldState();
		int newState = event.getNewState();

		if (oldState < OrderState.SHIPPED && newState == OrderState.SHIPPED) {
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
			packetStateChangeEvent.fire(new PacketStateChangeEvent(packet, oldState, packet.getState()));
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

}
