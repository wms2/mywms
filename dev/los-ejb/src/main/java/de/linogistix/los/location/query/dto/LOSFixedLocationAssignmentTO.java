/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.strategy.FixAssignment;

public class LOSFixedLocationAssignmentTO extends BODTO<FixAssignment>{

	private static final long serialVersionUID = 1L;

	private String name;
	private String storageLocation;
	private String itemData;
	private String itemDataName;
	private BigDecimal amount;
	
	public LOSFixedLocationAssignmentTO(Long id, int version, String name){
		super(id, version, name);
	}
	
	public LOSFixedLocationAssignmentTO(Long id, int version, Long name, String storageLocation, String itemData, String itemDataName, int scale, BigDecimal amount){
		super(id, version, name);
		this.storageLocation = storageLocation;
		this.itemData = itemData;
		this.itemDataName = itemDataName;
		try {
			this.amount = amount.setScale(scale);
		}
		catch( Throwable t ) {
			this.amount = amount;
		}
		this.name = itemData+" \u21d4 "+storageLocation;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getItemDataName() {
		return itemDataName;
	}
	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
