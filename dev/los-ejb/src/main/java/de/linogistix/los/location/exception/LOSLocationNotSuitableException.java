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
public class LOSLocationNotSuitableException extends LOSLocationException {

	public LOSLocationNotSuitableException(String ul, String slUlType) {
		super(LOSLocationExceptionKey.UNITLOAD_NOT_SUITABLE, 
				new String[]{ul, slUlType });
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LOSLocationException createRollbackException() {
		return new LOSLocationException(getLocationExceptionKey(), getParameters());
	}

}
