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
package de.wms2.mywms.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.delivery.DeliveryOrderStateChangeEvent;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.shipping.ShippingOrderLine;

@Stateless
public class OrderStateCalculator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PacketEntityService pickingUnitLoadService;

	@Inject
	private Event<DeliveryOrderStateChangeEvent> deliveryOrderStateChangeEvent;

	/**
	 * Trigger recalculation of delivery order state after potentially influencing
	 * parameters have changed
	 */
	public void calculateDeliveryOrderState(PickingOrder pickingOrder) throws BusinessException {
		String logStr = "calculateDeliveryOrderState ";
		logger.fine(logStr + "pickingOrder=" + pickingOrder);

		DeliveryOrder deliveryOrder = pickingOrder.getDeliveryOrder();
		if (deliveryOrder == null) {
			logger.fine(logStr + "No delivery order set picking order. pickingOrder=" + pickingOrder);
			return;
		}

		calculateDeliveryOrderState(deliveryOrder);
	}

	/**
	 * Trigger recalculation of delivery order state after potentially influencing
	 * parameters have changed
	 */
	public void calculateDeliveryOrderState(ShippingOrderLine shippingOrderLine) throws BusinessException {
		String logStr = "calculateDeliveryOrderState ";
		logger.fine(logStr + "shippingOrderLine=" + shippingOrderLine);

		Packet packet = shippingOrderLine.getPacket();

		DeliveryOrder deliveryOrder = packet.getDeliveryOrder();
		if (deliveryOrder == null) {
			logger.info(logStr + "No delivery order set packet. Cannot calculate state of delivery order. packet="
					+ packet);
			return;
		}

		calculateDeliveryOrderState(deliveryOrder);
	}

	/**
	 * Trigger recalculation of delivery order state after potentially influencing
	 * parameters have changed
	 */
	public void calculateDeliveryOrderState(Packet packet) throws BusinessException {
		String logStr = "calculateDeliveryOrderState ";
		logger.fine(logStr + "packet=" + packet);

		DeliveryOrder deliveryOrder = packet.getDeliveryOrder();
		if (deliveryOrder == null) {
			logger.info(logStr + "No delivery order set packet. Cannot calculate state of delivery order. packet="
					+ packet);
			return;
		}

		calculateDeliveryOrderState(deliveryOrder);
	}

	/**
	 * Trigger recalculation of delivery order state after potentially influencing
	 * parameters have changed
	 */
	public void calculateDeliveryOrderState(DeliveryOrder deliveryOrder) throws BusinessException {
		String logStr = "calculateDeliveryOrderState ";
		logger.fine(logStr + "deliveryOrder=" + deliveryOrder);

		// The picking state of the delivery order is calculated by its lines
		// If there is a line with a picked amount => At least STARTED
		// If all lines are satisfied => At least PICKED
		// If all lines are satisfied or pending => At least PENDING
		// If the state is picked, calculate the next step. PACKING, SHIPPING or
		// FINISHED
		// The generation of packing- or shipping-orders will be done by event observers
		boolean hasPickedAmout = false;
		boolean hasOpenLine = false;
		boolean hasPendingLine = false;
		for (DeliveryOrderLine deliveryOrderLine : deliveryOrder.getLines()) {
			if (deliveryOrderLine.getState() < OrderState.PICKED && deliveryOrderLine.getState() != OrderState.PENDING) {
				hasOpenLine = true;
			}
			if (deliveryOrderLine.getState() == OrderState.PENDING) {
				hasPendingLine = true;
			}
			if (deliveryOrderLine.getPickedAmount().compareTo(BigDecimal.ZERO) > 0) {
				hasPickedAmout = true;
			}
		}

		int nextState = 0;
		if (hasOpenLine) {
			nextState = OrderState.STARTED;
		} else if (hasPendingLine) {
			nextState = OrderState.PENDING;
		} else if (hasPickedAmout) {
			nextState = OrderState.PICKED;
		} else {
			logger.info(logStr + "Nothing picked. Ignore. deliveryOrder=" + deliveryOrder);
			return;
		}

//		if (deliveryOrder.getState() > nextState) {
//			logger.info(
//					logStr + "Delivery order state > calculated picking state. Ignore. deliveryOrder=" + deliveryOrder
//							+ ", state=" + deliveryOrder.getState() + ", calculated picking state=" + nextState);
//			return;
//		}

		if (nextState == OrderState.STARTED) {
			// valid, no further action

		} else if (nextState == OrderState.PENDING) {
			// valid, no further action

		} else if (nextState == OrderState.PICKED) {
			boolean isPickingFinished = true;
			boolean isPackingFinished = true;
			boolean isShippingFinished = true;

			List<Packet> packets = pickingUnitLoadService.readByDeliveryOrder(deliveryOrder);
			for (Packet packet : packets) {
				if (packet.getState() < OrderState.SHIPPED) {
					isShippingFinished = false;
				}
				if (packet.getState() < OrderState.PACKED) {
					isPackingFinished = false;
				}
				if (packet.getState() < OrderState.PICKED) {
					isPickingFinished = false;
				}
			}

			OrderStrategy strategy = deliveryOrder.getOrderStrategy();
			if (!isPickingFinished) {
				nextState = OrderState.STARTED;
			} else if (strategy.isSendToPacking() && !isPackingFinished) {
				nextState = OrderState.PACKING;
			} else if (strategy.isSendToShipping() && !isShippingFinished) {
				nextState = OrderState.SHIPPING;
			} else {
				nextState = OrderState.FINISHED;
			}
		} else {
			logger.severe(logStr + "Impossible state. Abort. deliveryOrder=" + deliveryOrder + ", state="
					+ deliveryOrder.getState() + ", calculated picking state=" + nextState);
			return;
		}

		if (deliveryOrder.getState() < nextState) {
			logger.info(
					logStr + "New state for delivery order. deliveryOrder=" + deliveryOrder + ", state=" + nextState);
			int oldState = deliveryOrder.getState();
			deliveryOrder.setState(nextState);
			fireDeliveryOrderStateChangeEvent(deliveryOrder, oldState);
		}
	}

	private void fireDeliveryOrderStateChangeEvent(DeliveryOrder entity, int oldState) throws BusinessException {
		try {
			logger.fine("Fire DeliveryOrderStateChangeEvent. entity=" + entity + ", state=" + entity.getState()
					+ ", oldState=" + oldState);
			deliveryOrderStateChangeEvent.fire(new DeliveryOrderStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
