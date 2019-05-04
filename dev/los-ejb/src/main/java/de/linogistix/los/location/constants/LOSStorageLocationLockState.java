/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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
 * Defines locks for BasicEntities from 300..399
 * 
 * @author trautm
 *
 */
public enum LOSStorageLocationLockState implements BusinessObjectLock{
	NOT_LOCKED(0),
	GENERAL(1),
	GOING_TO_DELETE(2),
	STORAGE(300),
	RETRIEVAL(301),
	CLEARING(302);
	
	int lock;
	  
	LOSStorageLocationLockState(int lock){
		this.lock = lock;
	}
	
	public int getLock() {
		return lock;
	}
	
	public static LOSStorageLocationLockState resolve(int lock){
		switch (lock){
		case 0: return NOT_LOCKED;
		case 1: return GENERAL;
		case 2: return GOING_TO_DELETE;
		case 300: return STORAGE;
		case 301: return RETRIEVAL;
		case 302: return CLEARING;
		default: throw new IllegalArgumentException();
		}
	}

	public Class<BundleResolver> getBundleResolver() {
		
		return BundleResolver.class;
	}

	public String getMessage() {
		
            String key = getMessageKey(); 

            ResourceBundle bundle;
            String formatString = key;
            try {
                // check for bundle
                bundle = ResourceBundle.getBundle("de.linogistix.los.location.res.Bundle",  
                								  Locale.getDefault(), 
                								  getBundleResolver().getClassLoader());
                // resolving key
                formatString = bundle.getString(key);
                return formatString;
            }
            catch (MissingResourceException ex) {
                return name() + "(" + lock + ")";
            }
	}

	public String getMessageKey() {
		return this.name() + "_" + lock;
	}
	
	
	
}
