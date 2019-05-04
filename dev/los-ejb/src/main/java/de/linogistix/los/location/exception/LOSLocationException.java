/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.exception;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.res.BundleResolver;

@ApplicationException(rollback=true)
public class LOSLocationException extends FacadeException {

	private static final long serialVersionUID = 1L;

	private static String resourceBundle = "de.linogistix.los.location.res.Bundle";
	
	private LOSLocationExceptionKey locationExceptionKey;
	
	public LOSLocationException(LOSLocationExceptionKey key, Object[] parameters){
		super("", key.name(), parameters, resourceBundle);
		setBundleResolver(BundleResolver.class);
        locationExceptionKey = key;
	}

	public LOSLocationExceptionKey getLocationExceptionKey() {
		return locationExceptionKey;
	}
}
