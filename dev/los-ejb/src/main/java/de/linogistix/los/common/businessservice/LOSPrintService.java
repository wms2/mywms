/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.los.common.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;

/**
 * @author krane
 *
 */
@Local
public interface LOSPrintService {

	public static String DEFAULT_PRINTER = "default";
	public static String NO_PRINTER = "none";
	
	/**
	 * Print a document on a given printer.
	 * 
	 * @param printer
	 * @param bytes
	 * @param type (@see {@link DocumentTypes})
	 * @throws FacadeException
	 */
	public void print(String printer, byte[] bytes, String type) throws FacadeException;


}
