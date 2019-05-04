/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.query.BODTO;


public class StockUnitLabelTO extends BODTO<StockUnitLabel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String itemdataRef;
	public String lotRef;
	public ItemMeasureTO itemMeasure;
	public String date;

	public StockUnitLabelTO(StockUnitLabel l){
		this(l.getId(), l.getVersion(), l.getName(), l.getItemdataRef(), l.getScale(), l.getItemUnit(), l.getLotRef(), l.getAmount(), l.getDateRef());
	}
	
	public StockUnitLabelTO(Long id, int version, String labelId, 
			String itemdataRef, int scale, String itemUnit,
			String lotRef, BigDecimal amount, String date ){
		
		super(id, version, labelId);
		this.itemdataRef = itemdataRef;
		this.lotRef = lotRef;
		
		this.date = date;
		this.itemMeasure = new ItemMeasureTO(amount, itemUnit, scale);
		
	}

	public String getItemdataRef() {
		return itemdataRef;
	}

	public void setItemdataRef(String itemdataRef) {
		this.itemdataRef = itemdataRef;
	}

	public String getLotRef() {
		return lotRef;
	}

	public void setLotRef(String lotRef) {
		this.lotRef = lotRef;
	}

	public ItemMeasureTO getItemMeasure() {
		return itemMeasure;
	}

	public void setItemMeasure(ItemMeasureTO itemMeasure) {
		this.itemMeasure = itemMeasure;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
