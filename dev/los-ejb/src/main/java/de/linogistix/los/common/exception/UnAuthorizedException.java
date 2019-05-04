/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.exception;

import javax.ejb.ApplicationException;

/**
 * This Exception will not cause a rollback of the transaction.
 * 
 * @author Jordan
 *
 */
@ApplicationException(rollback=false)
public class UnAuthorizedException extends Exception {

	private static final long serialVersionUID = 1L;
}
