/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=false)
public class LOSLocationWrongClientException extends LOSLocationException {

	public LOSLocationWrongClientException(String slCLient, String ulCLient) {
		super(LOSLocationExceptionKey.WRONG_CLIENT,new Object[]{slCLient, ulCLient} );
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LOSLocationException createRollbackException() {
		return new LOSLocationException(getLocationExceptionKey(), getParameters());
	}

}
