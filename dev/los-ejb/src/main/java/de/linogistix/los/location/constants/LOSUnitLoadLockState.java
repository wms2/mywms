/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.constants;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.location.res.BundleResolver;

/**
 * From 400..499
 * @author trautm
 *
 */
public enum LOSUnitLoadLockState implements BusinessObjectLock {
	
	NOT_LOCKED(0),
	GENERAL(1),
	GOING_TO_DELETE(2),
	STORAGE(400),
	RETRIEVAL(401),
	CLEARING(402),
	NOT_FOUND(403),
	QUALITY_CHECK(404),
	SHIPPED(405);
	
	int lock;
	  
	LOSUnitLoadLockState(int lock){
		this.lock = lock;
	}
	
	public int getLock() {
		return lock;
	}
	
	public static LOSUnitLoadLockState resolve(int lock){
		switch (lock){
		case 0: return NOT_LOCKED;
		case 1: return GENERAL;
		case 2: return GOING_TO_DELETE;
		case 400: return STORAGE;
		case 401: return RETRIEVAL;
		case 402: return CLEARING;
		case 403: return NOT_FOUND;
		case 404: return QUALITY_CHECK;
		case 405: return SHIPPED;
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

	public Class<BundleResolver> getBundleResolver() {
		return BundleResolver.class;
	}

	public String getMessageKey() {
		return this.name() + "_" + lock; 
	}
}
