/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ImportDataException extends Exception {

	private static final long serialVersionUID = 1L;

}
