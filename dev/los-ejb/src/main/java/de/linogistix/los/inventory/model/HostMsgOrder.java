package de.linogistix.los.inventory.model;

import de.linogistix.los.model.HostMsg;

public class HostMsgOrder extends HostMsg{

	private LOSCustomerOrder customerOrder;
	private String orderNumber;
	
	public HostMsgOrder( LOSCustomerOrder customerOrder ) {
		this.customerOrder = customerOrder;
		this.orderNumber = (customerOrder == null ? "?" : customerOrder.getNumber());
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public LOSCustomerOrder getCustomerOrder() {
		return customerOrder;
	}
	public void setCustomerOrder(LOSCustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	
}
