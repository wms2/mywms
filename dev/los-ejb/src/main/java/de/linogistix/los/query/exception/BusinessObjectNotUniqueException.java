/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query.exception;


public class BusinessObjectNotUniqueException extends
		BusinessObjectNotFoundException {


	private static final long serialVersionUID = 1L;
	
	public final static String RESOURCE_KEY = "BusinessException.ObjectNotUnique";
	
	/** Creates a new instance of BusinessObjectNotFoundException */	
	public BusinessObjectNotUniqueException(String identity) {
		super("BusinessObject not unique", RESOURCE_KEY,
				identity);
	}

}
