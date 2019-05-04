/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.query.exception;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;


/**
 * Thrown if a BusinessObject can't be obtained from database.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@ApplicationException(rollback = false)
public class BusinessObjectNotFoundException extends FacadeException {

	private static final long serialVersionUID = 1L;

	public final static String RESOURCE_KEY = "BusinessException.ObjectNotFound";
    private static final String resourceBundle = "de.linogistix.los.res.Bundle";

	/** Creates a new instance of BusinessObjectNotFoundException */	
	public BusinessObjectNotFoundException() {
		super("BusinessObject not found", RESOURCE_KEY, new Object[0], resourceBundle);
		setBundleResolver(BusinessObjectQueryException.class);
	}
	
	public BusinessObjectNotFoundException(long id, Class<? extends Object> boClass) {
		super("BusinessObject not found", RESOURCE_KEY, new Object[]{id, boClass.getSimpleName()}, resourceBundle);
		setBundleResolver(BusinessObjectQueryException.class);
	}
	
	public BusinessObjectNotFoundException(String identity) {
		super("BusinessObject not found", RESOURCE_KEY, new Object[]{identity}, resourceBundle);
		setBundleResolver(BusinessObjectQueryException.class);
	}
	
	/** Creates a new instance of BusinessObjectNotFoundException */	
	public BusinessObjectNotFoundException(String msg, String key, String identity) {
		super(msg, key, new Object[]{identity}, resourceBundle);
		setBundleResolver(BusinessObjectQueryException.class);
	}

}
