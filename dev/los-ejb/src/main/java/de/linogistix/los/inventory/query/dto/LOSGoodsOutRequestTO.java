/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.query.BODTO;

public class LOSGoodsOutRequestTO extends BODTO<LOSGoodsOutRequest> {

	private String number;
	private String clientNumber;
	private LOSGoodsOutRequestState outState = LOSGoodsOutRequestState.RAW;
	private String outStateName;
	private String customerOrderNumber;
	private String customerOrderExternalNumber;
	private String customerOrderExternalId;
    private String customerOrderCustomerNumber;
	private Date shippingDate;
	private int numPos;

	private static final long serialVersionUID = 1L;

	public LOSGoodsOutRequestTO( LOSGoodsOutRequest x ){
		this(x.getId(), x.getVersion(), x.getNumber(), x.getNumber(), x.getOutState(), x.getClient().getNumber(), x.getCustomerOrder()==null?null:x.getCustomerOrder().getOrderNumber(), x.getShippingDate());
	}
	

	public LOSGoodsOutRequestTO(
			long id,
			int version,
			String name,
			String number,
			LOSGoodsOutRequestState outState,
			String clientNumber, 
			String customerOrderNumber,
			Date shippingDate){
		this(id, version, name, number, outState, clientNumber, customerOrderNumber, null, null, null, shippingDate, -1);
	}

	public LOSGoodsOutRequestTO(
			long id,
			int version,
			String name,
			String number,
			LOSGoodsOutRequestState outState,
			String clientNumber, 
			String customerOrderNumber,
			Date shippingDate,
			int numPos){
		this(id, version, name, number, outState, clientNumber, customerOrderNumber, null, null, null, shippingDate, numPos);
	}

	public LOSGoodsOutRequestTO(
			long id,
			int version,
			String name,
			String number,
			LOSGoodsOutRequestState outState,
			String clientNumber, 
			String customerOrderNumber,
			String customerOrderExternalNumber,
			String customerOrderExternalId,
		    String customerOrderCustomerNumber,
			Date shippingDate){
		this(id, version, name, number, outState, clientNumber, customerOrderNumber, customerOrderExternalNumber, customerOrderExternalId, customerOrderCustomerNumber, shippingDate, -1);
	}

	public LOSGoodsOutRequestTO(
			long id,
			int version,
			String name,
			String number,
			LOSGoodsOutRequestState outState,
			String clientNumber, 
			String customerOrderNumber,
			String customerOrderExternalNumber,
			String customerOrderExternalId,
		    String customerOrderCustomerNumber,
			Date shippingDate,
			int numPos){
		super(id, version, name);
		this.number = number;
		this.outState = outState; 
		this.outStateName = outState.name(); 
		this.clientNumber = clientNumber;
		this.customerOrderNumber = customerOrderNumber;
		this.customerOrderExternalNumber = customerOrderExternalNumber;
		this.customerOrderExternalId = customerOrderExternalId;
		this.customerOrderCustomerNumber = customerOrderCustomerNumber;
		this.shippingDate = shippingDate;
		this.numPos = numPos;
		setClassName(LOSGoodsOutRequest.class.getName());
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

	public LOSGoodsOutRequestState getOutState() {
		return outState;
	}

	public void setOutState(LOSGoodsOutRequestState outState) {
		this.outState = outState;
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

	public int getNumPos() {
		return numPos;
	}
	public void setNumPos(int numPos) {
		this.numPos = numPos;
	}


	public String getOutStateName() {
		return outStateName;
	}
	public void setOutStateName(String outStateName) {
		this.outStateName = outStateName;
	}


	public String getCustomerOrderExternalNumber() {
		return customerOrderExternalNumber;
	}


	public void setCustomerOrderExternalNumber(String customerOrderExternalNumber) {
		this.customerOrderExternalNumber = customerOrderExternalNumber;
	}


	public String getCustomerOrderExternalId() {
		return customerOrderExternalId;
	}


	public void setCustomerOrderExternalId(String customerOrderExternalId) {
		this.customerOrderExternalId = customerOrderExternalId;
	}


	public String getCustomerOrderCustomerNumber() {
		return customerOrderCustomerNumber;
	}


	public void setCustomerOrderCustomerNumber(String customerOrderCustomerNumber) {
		this.customerOrderCustomerNumber = customerOrderCustomerNumber;
	}



}
