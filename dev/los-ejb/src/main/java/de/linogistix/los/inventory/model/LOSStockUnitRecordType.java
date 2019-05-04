/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

public enum LOSStockUnitRecordType {

	/** The stockunit has been created with a given amount*/
	STOCK_CREATED,
	/** Amount has been transferred from one stockunit to the other*/
	STOCK_SPLITTED,
	/** Amount of one stockunit has been changed*/
	STOCK_ALTERED,
	/** The Stockunit has been removed*/
	STOCK_REMOVED,
	/** The stockunit has been transferred from one unitload to the other*/
	STOCK_TRANSFERRED,
	/** The stockunit has been counted */
	STOCK_COUNTED
}
