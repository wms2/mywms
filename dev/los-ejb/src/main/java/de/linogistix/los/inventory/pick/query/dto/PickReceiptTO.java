/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.query.dto;

import java.util.Date;

import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.query.BODTO;

public class PickReceiptTO extends BODTO<PickReceipt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public  String orderNumber;
	public  String pickNumber;
	public  String labelID;
	public  String state;
	public  Date date;
	
	public PickReceiptTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public PickReceiptTO(Long id, int version, String name,
			String orderNumber, String pickNumber, String labelID, String state, Date date){
		super(id, version, labelID);
		this.orderNumber = orderNumber;
		this.pickNumber = pickNumber;
		this.labelID = labelID;
		this.state = state;
		this.date = date;
		
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPickNumber() {
		return pickNumber;
	}

	public void setPickNumber(String pickNumber) {
		this.pickNumber = pickNumber;
	}

	public String getLabelID() {
		return labelID;
	}

	public void setLabelID(String labelID) {
		this.labelID = labelID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
