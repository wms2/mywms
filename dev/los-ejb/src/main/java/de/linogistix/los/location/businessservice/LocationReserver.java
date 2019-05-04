package de.linogistix.los.location.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LocationReserver {

	/**
	 * Checks whether a transfer of unit load to the given location is allowed
     * 
     * Exceptions thrown are all NONE-Rollback Exceptions! 
     * 
	 * @param location
	 * @param unitLoad
	 * @return
     * @throws LOSLocationAlreadyFullException doesn't rollback transaction
     * @throws LOSLocationNotSuitableException doesn't rollback transaction
     * @throws LOSLocationWrongClientException doesn't rollback transaction
     * @throws LOSLocationReservedException doesn't rollback transaction
	 */
	public LOSTypeCapacityConstraint checkAllocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, boolean ignoreLock) throws LOSLocationAlreadyFullException,LOSLocationNotSuitableException,LOSLocationWrongClientException,LOSLocationReservedException;
	
	public LOSTypeCapacityConstraint allocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad) throws FacadeException;
	public void deallocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, boolean checkEmptyLocation) throws FacadeException;
	public void deallocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad) throws FacadeException;
	public void deallocateLocationComplete(LOSStorageLocation location) throws FacadeException;

	public void recalculateAllocation(LOSStorageLocation location, LOSUnitLoad... knownAsRemoved) throws FacadeException;
	
}
