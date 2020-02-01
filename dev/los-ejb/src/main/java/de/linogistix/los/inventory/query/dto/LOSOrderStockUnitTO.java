/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.StockUnit;

public class LOSOrderStockUnitTO extends BODTO<StockUnit> {

	private static final long serialVersionUID = 1L;

	public String lot = "";
	public String unitLoad;
	public String storageLocation;
	public BigDecimal availableAmount;

	public LOSOrderStockUnitTO(Long id, int version, String name ) { 
		super(id, version, name);
	}

	public LOSOrderStockUnitTO(Long id, 
							   int version,
							   String lotNumber, 
							   String unitLoad, 
							   String storageLocation,  
							   BigDecimal amount, 
							   BigDecimal reservedAmount)
	{
		this(id, version, id.toString(), lotNumber, unitLoad, storageLocation, amount, reservedAmount);
	}
	
	public LOSOrderStockUnitTO(Long id, 
							   int version, 
							   String name,
							   String lotNumber, 
							   String unitLoad, 
							   String storageLocation,  
							   BigDecimal amount, 
							   BigDecimal reservedAmount)
	{
		super(id, version, name);
		
		this.lot = lotNumber;
		this.unitLoad = unitLoad;
		this.storageLocation = storageLocation;
		this.availableAmount = amount.subtract(reservedAmount);
		
		setClassName(StockUnit.class.getName());
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(String unitLoad) {
		this.unitLoad = unitLoad;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}
}
