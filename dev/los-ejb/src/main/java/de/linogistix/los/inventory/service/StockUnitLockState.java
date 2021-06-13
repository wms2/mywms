/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.inventory.res.InventoryBundleResolver;


/**
 * Defines locks for StockUnits from 100..199
 * 
 * @author trautm
 *
 */
public enum StockUnitLockState implements BusinessObjectLock{
	LOT_EXPIRED(102),
	QUALITY_FAULT(103);
	
	int lock;
	
	StockUnitLockState(int lock){
		this.lock = lock;
	}
	
	public int getLock() {
		return lock;
	}
	
	public static StockUnitLockState resolve(int lock){
		switch (lock){
		case 102: return LOT_EXPIRED;
		case 103: return QUALITY_FAULT;
		default: throw new IllegalArgumentException();
		}
	}
	
	public String getMessage() {
		
	       String key = getMessageKey();

	        ResourceBundle bundle;
	        String ret = key;
	        try {
	            // check for bundle
	            bundle = ResourceBundle.getBundle("Bundle",  Locale.getDefault(),getBundleResolver().getClassLoader());
	            // resolving key
	            ret = bundle.getString(key);
	            return ret;
	        }
	        catch (MissingResourceException ex) {
	            return name() + "(" + lock + ")";
	        }
	}

	public Class<InventoryBundleResolver> getBundleResolver() {
		return InventoryBundleResolver.class;
	}

	public String getMessageKey() {
		return this.name() + "_" + lock; 
	}
	
}
