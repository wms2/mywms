/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.info;

import java.io.Serializable;

import de.linogistix.los.inventory.model.LOSCustomerOrder;

/**
 * @author krane
 *
 */
public class InfoOrderTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String clientName = "";
	private String clientNumber = "";
	private String number = "";
	private String type = "";
	private String state = "";
	private int numPos = 0;
	private String destinationName = "";
	
	public InfoOrderTO() {
	}
	public InfoOrderTO( LOSCustomerOrder order ) {
		if( order == null ) {
			return;
		}
		this.clientName = order.getClient().getName();
		this.clientNumber = order.getClient().getNumber();
		this.number = order.getNumber();
		this.type = (order.getStrategy() == null ? "" : order.getStrategy().getName());
		this.state = ""+order.getState();
		this.numPos = order.getPositions().size();
		this.destinationName = (order.getDestination() == null ? "" : order.getDestination().getName());
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getNumPos() {
		return numPos;
	}

	public void setNumPos(int numPos) {
		this.numPos = numPos;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	
	
}
