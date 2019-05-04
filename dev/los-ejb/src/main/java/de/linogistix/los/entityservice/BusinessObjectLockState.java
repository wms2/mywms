/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.entityservice;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.linogistix.los.res.BundleResolver;



/**
 * Defines locks for BasicEntities from 0..99
 * 
 * @author trautm
 *
 */
public enum BusinessObjectLockState implements BusinessObjectLock{
	NOT_LOCKED(0),
	GENERAL(1),
	GOING_TO_DELETE(2);
	
	int lock;
	
	BusinessObjectLockState(int lock){
		this.lock = lock;
	}
	
	public int getLock() {
		return lock;
	}
	
	public static BusinessObjectLockState resolve(int lock){
		switch (lock){
		case 0: return NOT_LOCKED;
		case 1: return GENERAL;
		case 2: return GOING_TO_DELETE;
		default: throw new IllegalArgumentException();
		}
	}

	public String getMessage() {
		
	       String key = getMessageKey();

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

	public String getMessageKey(){
		return this.name() + "_" + lock;
	}
	
	public Class<BundleResolver> getBundleResolver() {
		return BundleResolver.class;
	}

	
}
