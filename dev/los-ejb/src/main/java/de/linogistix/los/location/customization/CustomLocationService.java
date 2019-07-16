/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.customization;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 * User exits for storage location allocation purposes.
 * 
 * @author krane
 *
 */
@Local
public interface CustomLocationService {

	/**
	 * User exit. This method is called, when a location becomes empty.
	 * Use this to correct reservations and allocations.
	 * 
	 * @param location
	 * @throws FacadeException
	 */
	public void onLocationGetsEmpty( StorageLocation location ) throws FacadeException;
	public void onLocationGetsEmpty( StorageLocation location, boolean checkEmptyLocation ) throws FacadeException;

	/**
	 * User exit. This method checks, whether the allocation of a location with the given parameters will be valid.
	 * All thrown exceptions will NOT roll back any transaction. 
	 * 
	 * @param location
	 * @param unitLoad
	 * @param constraint
	 * @throws LOSLocationAlreadyFullException
	 * @throws LOSLocationNotSuitableException
	 * @throws LOSLocationWrongClientException
	 * @throws LOSLocationReservedException
	 */
	public void checkAllocateLocation(StorageLocation location, UnitLoad unitLoad, TypeCapacityConstraint constraint) throws LOSLocationAlreadyFullException,LOSLocationNotSuitableException,LOSLocationWrongClientException,LOSLocationReservedException;
	
	/**
	 * User exit. This method writes an allocation.<br>
	 * 
	 * @param location
	 * @param unitLoad
	 * @param constraint
	 * @throws FacadeException
	 */
	public void allocateLocation(StorageLocation location, UnitLoad unitLoad, TypeCapacityConstraint constraint) throws FacadeException;
	
	/**
	 * User exit. This method removes an allocation.<br>
	 * 
	 * @param location
	 * @param unitLoad
	 * @param constraint
	 * @throws FacadeException
	 */
	public void deallocateLocation(StorageLocation location, UnitLoad unitLoad, TypeCapacityConstraint constraint, boolean checkEmptyLocation) throws FacadeException;

	/**
	 * User exit. This method is called, after a unit load has been removed from a location
	 * @param location
	 * @param unitLoad
	 * @throws FacadeException
	 */
	public void onUnitLoadRemoved(StorageLocation location, UnitLoad unitLoad) throws FacadeException;

	/**
	 * User exit. This method is called, after a unit load has been placed on a location
	 * @param location
	 * @param unitLoad
	 * @throws FacadeException
	 */
	public void onUnitLoadPlaced(StorageLocation location, UnitLoad unitLoad) throws FacadeException;

	
}
