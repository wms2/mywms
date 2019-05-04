/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import java.math.BigDecimal;

import de.linogistix.los.util.StringTools;

/**
 * @author krane
 *
 */
public class LOSStockUnitReportTO {

	public String pos = "";
	
	public String itemNumber = "";
	public String itemName = "";
	public String itemUnit = "";
	public int itemScale;
	
	public String lotName = "";
	public String serialNumber = "";
	public BigDecimal amount = BigDecimal.ZERO;

	public String getPos() {
		return pos;
	}
	
	public String getItemNumber() {
		return itemNumber;
	}
	
	public String getFormattedItemNumber() {
		if( !StringTools.isEmpty(lotName) ) {
			return itemNumber + " (" + lotName + ")";
		}
		return itemNumber;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public String getFormattedAmount() {
		if( amount != null && amount.compareTo(BigDecimal.ZERO)!=0 ) {
			return amount.toString();
		}
		return "";
	}
	
	public String getItemUnit() {
		return itemUnit;
	}
	
	public String getFormattedItemUnit() {
		if( amount != null && amount.compareTo(BigDecimal.ZERO)!=0 ) {
			return itemUnit;
		}
		return "";
	}

	public String getLotName() {
		return lotName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	
	
}
