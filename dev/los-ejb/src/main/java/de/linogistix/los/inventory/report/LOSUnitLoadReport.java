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

import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.location.model.LOSUnitLoad;

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
	public PickReceipt generateUnitLoadReport(LOSUnitLoad unitLoad) throws FacadeException;
	
	/**
	 * Persist an order receipt.
	 * If it is already existing, it will be replaced.
	 * 
	 * @param receipt
	 * @return
	 * @throws FacadeException
	 */
	public PickReceipt storeUnitLoadReport(PickReceipt label) throws FacadeException;
	
}
