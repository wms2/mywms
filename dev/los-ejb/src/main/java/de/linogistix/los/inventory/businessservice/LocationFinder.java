/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Zone;

import de.linogistix.los.inventory.model.LOSStorageStrategy;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
@Local
public interface LocationFinder {

	/**
	 * Search a location for a unit load
	 * 
	 * @param unitLoad
	 * @return
	 * @throws FacadeException
	 */
	public LOSStorageLocation findLocation( LOSUnitLoad unitLoad ) throws FacadeException;
	
	/**
	 * Search a location for a unit load
	 * 
	 * @param unitLoad
	 * @param zone
	 * @param strategy
	 * @return
	 * @throws FacadeException
	 */
	public LOSStorageLocation findLocation( LOSUnitLoad unitLoad, Zone zone, LOSStorageStrategy strategy ) throws FacadeException;
	
	/**
	 * Search a location for a unit load suitable for picking
	 * 
	 * @param unitLoad
	 * @param zone
	 * @param strategy
	 * @return
	 * @throws FacadeException
	 */
	public LOSStorageLocation findPickingLocation( LOSUnitLoad unitLoad, Zone zone, LOSStorageStrategy strategy ) throws FacadeException;	
	
}
