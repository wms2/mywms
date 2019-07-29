/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.delivery.DeliveryOrder;

/**
 * @author krane
 *
 */
public class LOSCustomerOrderTO extends BODTO<DeliveryOrder>{

	private static final long serialVersionUID = 1L;

	private String number;
	private String clientNumber;
	private String externalNumber;
	private Date delivery;
	private int state;
	private int numPos;
	private int prio = 50;
	private String destinationName;
    private String customerNumber;
    private String strategyName;
	
	public LOSCustomerOrderTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public LOSCustomerOrderTO(Long id, int version, String number, String clientNumber, String externalNumber, Date deliveryDate, int state, String destinationName,
		    String customerNumber, int prio, String strategyName){
		this(id, version, number, clientNumber, externalNumber, deliveryDate, state, destinationName,
		    customerNumber, prio, strategyName, -1);
	}
	
	public LOSCustomerOrderTO(Long id, int version, String number, String clientNumber, String externalNumber, Date deliveryDate, int state, String destinationName,
		    String customerNumber, int prio, String strategyName, int numPos){
		super(id, version, number);
		this.number = number;
		this.delivery = deliveryDate;
		this.clientNumber = clientNumber;
		this.externalNumber = externalNumber;
		this.destinationName = destinationName;
		this.state = state;
		
	    this.customerNumber = customerNumber;
	    this.strategyName = strategyName;
	    
	    this.prio = prio;
	    this.numPos = numPos;
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


	public Date getDelivery() {
		return delivery;
	}


	public void setDelivery(Date delivery) {
		this.delivery = delivery;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getNumPos() {
		return numPos;
	}

	public void setNumPos(int numPos) {
		this.numPos = numPos;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	
	
}
