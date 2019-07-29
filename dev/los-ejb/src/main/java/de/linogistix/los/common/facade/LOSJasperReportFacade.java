/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.wms2.mywms.document.Document;

/**
 * @author krane
 *
 */
@Remote
public interface LOSJasperReportFacade  {
	
	/**
	 * Write new source document to report.
	 * The old compiled document is removed.
	 * 
	 * @param clientNumber
	 * @param name
	 * @param document
	 * @throws FacadeException
	 */
	public void writeSourceDocument( long id, String document ) throws FacadeException;

	/**
	 * Try to compile the report.
	 * The old compiled document is removed.
	 * 
	 * @param clientNumber
	 * @param name
	 * @throws FacadeException
	 */
	public void compileReport( long id ) throws FacadeException;
	
	Document readSource( long id ) throws FacadeException;

}
