/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;

/**
 * Operations for managing locations and unit loads.
 * 
 * @author trautm
 *
 */
@Remote
public interface ManageLocationFacade {
	
	/**
	 * Transferres {@link LOSUnitLoad} ul to destination {@link LOSStorageLocation}
	 *  
	 * @param dest the destination location
	 * @param ul the unit load to transfer
	 * @param index TODO
	 * @param ignoreSlLock TODO
	 * @param info TODO
	 * @throws LOSLocationException
	 */
	void transferUnitLoad(BODTO<LOSStorageLocation> dest, BODTO<LOSUnitLoad> ul, int index, boolean ignoreSlLock, String info) 
		throws FacadeException; 
	
	public void transferToCarrier(BODTO<LOSUnitLoad> source, BODTO<LOSUnitLoad> destination, String info) throws FacadeException;

	/**
	 * Checks whether a transfer of {@link LOSUnitLoad} to the given {@link LOSStorageLocation} will be successful.
     * 
     * @return the current {@link LOSTypeCapacityConstraint} of this {@link LOSStorageLocation} 
	 */
	LOSTypeCapacityConstraint checkUnitLoadSuitable(BODTO<LOSStorageLocation> dest, BODTO<LOSUnitLoad> ul, boolean ignoreLock) throws FacadeException;
	
	/**
	 * Relases any reservation on the given LOSStorageLocation.
	 * 
	 * @param locations
	 * @throws LOSLocationException
	 */
	void releaseReservations(List<BODTO<LOSStorageLocation>> locations) throws FacadeException;
	
	/**
	 * Removes unit load with given label id. Removing means the unit load is
	 * stored an a special storage location called nirwana and locked with 
	 * {@link BusinessObjectLockState#GOING_TO_DELETE}.
	 *  
	 * @param labelId the label id of the unit load to remove
	 * @throws FacadeException
	 */
	void sendUnitLoadToNirwana(String labelId) throws FacadeException;
	
	/**
	 * Removes all given unit loads. Removing means the unit load is
	 * stored an a special storage locaiton called nirwana and locked with 
	 * {@link BusinessObjectLockState#GOING_TO_DELETE}.
	 *  
	 */
	void sendUnitLoadToNirwana(List<BODTO<LOSUnitLoad>> list) throws FacadeException;

	/**
	 * Set the order index of the locations
	 * @param rackTo DODTO of the rack to process
	 * @param startValue The first index
	 * @param diffValue The difference to the next index. May be negative for backward directions.
	 * @return The last used index
	 * @throws FacadeException
	 */
	int setLocationOrderIndex(BODTO<LOSRack> rackTo, int startValue, int diffValue) throws FacadeException;


}
