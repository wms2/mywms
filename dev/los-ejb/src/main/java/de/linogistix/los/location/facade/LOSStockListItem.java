/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.io.Serializable;

public class LOSStockListItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clientName;
	
	private String itemNumber;
	
	private String batchNumber;
	
	private int stockAmount;
	
	private String unitLoadLabel;
	
	private String storageLocation;

	public LOSStockListItem(String clientName, 
							String itemNumber,
							String batchNumber, 
							int stockAmount, 
							String unitLoadLabel,
							String storageLocation) 
	{		
		this.clientName = clientName;
		this.itemNumber = itemNumber;
		this.batchNumber = batchNumber;
		this.stockAmount = stockAmount;
		this.unitLoadLabel = unitLoadLabel;
		this.storageLocation = storageLocation;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public int getStockAmount() {
		return stockAmount;
	}

	public void setStockAmount(int stockAmount) {
		this.stockAmount = stockAmount;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}
	
	
}
