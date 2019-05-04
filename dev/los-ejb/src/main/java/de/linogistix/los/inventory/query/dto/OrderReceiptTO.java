/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.query.BODTO;

public class OrderReceiptTO extends BODTO<OrderReceipt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String orderNumber;
	
	public String orderReference;
	
	public Date date;
	
	public String state;
	
	public OrderReceiptTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public OrderReceiptTO(Long id, int version, String orderNumber, String orderreference, Date date, LOSOrderRequestState state){
		super(id, version, orderNumber);
		this.orderNumber = orderNumber;
		this.orderReference = orderreference;
		this.date = date;
		this.state = state == null ? "" : state.toString();
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderReference() {
		return orderReference;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}
