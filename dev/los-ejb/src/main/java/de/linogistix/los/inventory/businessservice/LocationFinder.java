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

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.strategy.Zone;

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
	public StorageLocation findLocation( UnitLoad unitLoad ) throws FacadeException;
	
	/**
	 * Search a location for a unit load
	 * 
	 * @param unitLoad
	 * @param zone
	 * @param strategy
	 * @return
	 * @throws FacadeException
	 */
	public StorageLocation findLocation( UnitLoad unitLoad, Zone zone, StorageStrategy strategy ) throws FacadeException;
	
	/**
	 * Search a location for a unit load suitable for picking
	 * 
	 * @param unitLoad
	 * @param zone
	 * @param strategy
	 * @return
	 * @throws FacadeException
	 */
	public StorageLocation findPickingLocation( UnitLoad unitLoad, Zone zone, StorageStrategy strategy ) throws FacadeException;	
	
}
