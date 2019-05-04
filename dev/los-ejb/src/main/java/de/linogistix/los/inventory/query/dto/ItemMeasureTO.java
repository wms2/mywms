/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ItemMeasureTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BigDecimal value;
	
	String itemUnit;
	
	int scale = 0;
	
	public ItemMeasureTO(BigDecimal value, String itemUnit){
		this.value = value;
		this.itemUnit = itemUnit;
	}
	
	public ItemMeasureTO(BigDecimal value, String itemUnit, int scale){
		this.value = value;
		this.itemUnit = itemUnit;
		this.scale = scale;
	}

	@Column(scale=4, nullable=false, name="_value")
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	@Column(nullable=false)
	public String getItemUnit() {
		return itemUnit;
	}

	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}
	
	@Override
	public String toString() {
		String ret;
		ret = this.value.setScale(scale).toString();
		ret += " " ;
		ret += this.itemUnit;
		
		return ret;
	}

	@Column(name="_scale")
	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
}
