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
public class LOSLocationReservedException extends LOSLocationException {

	public LOSLocationReservedException(String slName, String constr) {
		super(LOSLocationExceptionKey.STORAGELOCATION_ALREADY_RESERVED_FOR_DIFFERENT_TYPE, 
				new String[]{slName, constr});
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LOSLocationException createRollbackException() {
		return new LOSLocationException(getLocationExceptionKey(), getParameters());
	}

}
