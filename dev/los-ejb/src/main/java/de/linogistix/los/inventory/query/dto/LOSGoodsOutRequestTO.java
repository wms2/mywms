/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.shipping.ShippingOrder;

public class LOSGoodsOutRequestTO extends BODTO<ShippingOrder> {

	private String number;
	private String clientNumber;
	private int state;
	private String customerOrderNumber;
	private Date shippingDate;

	private static final long serialVersionUID = 1L;

	public LOSGoodsOutRequestTO() {};

	public LOSGoodsOutRequestTO(ShippingOrder shippingOrder) {
		super(shippingOrder.getId(), shippingOrder.getVersion(), shippingOrder.getOrderNumber());
		this.number =  shippingOrder.getOrderNumber();
		this.clientNumber = shippingOrder.getClient().getNumber();
		this.customerOrderNumber = (shippingOrder.getDeliveryOrder()==null?null:shippingOrder.getDeliveryOrder().getOrderNumber());
		this.shippingDate = shippingOrder.getShippingDate();
	};

	public LOSGoodsOutRequestTO(
			long id,
			int version,
			String number,
			int state,
			String clientNumber, 
			String customerOrderNumber,
			Date shippingDate){
		super(id, version, number);
		this.number = number;
		this.state = state;
		this.clientNumber = clientNumber;
		this.customerOrderNumber = customerOrderNumber;
		this.shippingDate = shippingDate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getCustomerOrderNumber() {
		return customerOrderNumber;
	}

	public void setCustomerOrderNumber(String customerOrderNumber) {
		this.customerOrderNumber = customerOrderNumber;
	}

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
