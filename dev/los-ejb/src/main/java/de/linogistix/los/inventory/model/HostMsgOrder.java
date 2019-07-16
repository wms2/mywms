package de.linogistix.los.inventory.model;

import de.linogistix.los.model.HostMsg;
import de.wms2.mywms.delivery.DeliveryOrder;

public class HostMsgOrder extends HostMsg{

	private DeliveryOrder deliveryOrder;
	private String orderNumber;
	
	public HostMsgOrder( DeliveryOrder customerOrder ) {
		this.deliveryOrder = customerOrder;
		this.orderNumber = (customerOrder == null ? "?" : customerOrder.getOrderNumber());
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}
	public void setDeliveryOrder(DeliveryOrder customerOrder) {
		this.deliveryOrder = customerOrder;
	}

	
}
