package org.mywms.model;

import java.io.Serializable;
import java.math.BigDecimal;


public class ItemMeasure implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BigDecimal value;
	
	ItemUnit itemUnit;
	
	public ItemMeasure(BigDecimal value, ItemUnit itemUnit){
		this.value = value;
		this.itemUnit = itemUnit;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	public ItemUnit getItemUnit() {
		return itemUnit;
	}

	public void setItemUnit(ItemUnit itemUnit) {
		this.itemUnit = itemUnit;
	}
	
	@Override
	public String toString() {
		String ret;
		ret = this.value.toString();
		ret += " " ;
		ret += this.itemUnit.getUnitName();
		
		return ret;
	}
	
}
