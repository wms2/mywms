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
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoad;

/**
 *
 * @author trautm
 */
@Local
public interface LOSStockUnitLabelReport {

	/**
	 * Generation of a goods receipt stock unit label
	 * The object and report is generated. 
	 * It is not persisted.
	 * 
	 * @param unitLoad
	 * @return
	 * @throws FacadeException
	 */
	public Document generateStockUnitLabel(UnitLoad unitLoad) throws BusinessException;
	
}
