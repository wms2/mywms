/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.wms2.mywms.document.Document;
import de.wms2.mywms.inventory.UnitLoad;

/**
 * @author krane
 *
 */
@Local
public interface LOSUnitLoadReport {
	
	/**
	 * Generation of an order receipt.
	 * The object and report is generated. 
	 * It is not persisted.
	 * 
	 * @param order
	 * @return
	 * @throws FacadeException
	 */
	public Document generateUnitLoadReport(UnitLoad unitLoad) throws FacadeException;
	
}
