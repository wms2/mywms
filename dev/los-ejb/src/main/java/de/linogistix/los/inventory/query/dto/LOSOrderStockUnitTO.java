/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.query.BODTO;

public class LOSOrderStockUnitTO extends BODTO<StockUnit> {

	private static final long serialVersionUID = 1L;

	public String lot = "";
	public Lot lotEntity;
	public String unitLoad;
	public String storageLocation;
	public BigDecimal availableAmount;

	public LOSOrderStockUnitTO(Long id, int version, String name ) { 
		super(id, version, name);
	}

	public LOSOrderStockUnitTO(Long id, 
							   int version,
							   Lot lot, 
							   String unitLoad, 
							   String storageLocation,  
							   BigDecimal amount, 
							   BigDecimal reservedAmount)
	{
		this(id, version, id.toString(), lot, unitLoad, storageLocation, amount, reservedAmount);
	}
	
	public LOSOrderStockUnitTO(Long id, 
							   int version, 
							   String name,
							   Lot lot, 
							   String unitLoad, 
							   String storageLocation,  
							   BigDecimal amount, 
							   BigDecimal reservedAmount)
	{
		super(id, version, name);
		
		if(lot != null){
			this.lot = lot.getName();
		}
		lotEntity = lot;
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
