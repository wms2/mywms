/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.io.Serializable;

public class ClientItemNumberTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String clientNumber;
	
	private String itemNumber;
	
	public ClientItemNumberTO() {
		super();
	}

	public ClientItemNumberTO(String clientNumber, String itemNumber) {
		super();
		this.clientNumber = clientNumber;
		this.itemNumber = itemNumber;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
}
