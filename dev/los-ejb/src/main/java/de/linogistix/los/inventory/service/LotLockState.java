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
 * Defines locks for Lots from 200..299
 * @author trautm
 *
 */
public enum LotLockState implements BusinessObjectLock{
	LOT_EXPIRED(202),
	LOT_TOO_YOUNG(203),
	PRODUCER_CALL(204),
	QUALITY_FAULT(205);
	int lock;
	
	LotLockState(int lock){
		this.lock = lock;
	}
	
	public int getLock() {
		return lock;
	}
	
	public static LotLockState resolve(int lock){
		switch (lock){
		case 202: return LOT_EXPIRED;
		default: throw new IllegalArgumentException();
		}
	}

	public String getMessage() {
		
	       String key =  getMessageKey();

	        ResourceBundle bundle;
	        String formatString = key;
	        try {
	            // check for bundle
	            bundle = ResourceBundle.getBundle("Bundle",  Locale.getDefault(),getBundleResolver().getClassLoader());
	            // resolving key
	            formatString = bundle.getString(key);
	            return formatString;
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
