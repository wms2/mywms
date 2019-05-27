package de.linogistix.los.location.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

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
	public TypeCapacityConstraint checkAllocateLocation(StorageLocation location, UnitLoad unitLoad, boolean ignoreLock) throws LOSLocationAlreadyFullException,LOSLocationNotSuitableException,LOSLocationWrongClientException,LOSLocationReservedException;
	
	public TypeCapacityConstraint allocateLocation(StorageLocation location, UnitLoad unitLoad) throws FacadeException;
	public void deallocateLocation(StorageLocation location, UnitLoad unitLoad, boolean checkEmptyLocation) throws FacadeException;
	public void deallocateLocation(StorageLocation location, UnitLoad unitLoad) throws FacadeException;
	public void deallocateLocationComplete(StorageLocation location) throws FacadeException;

	public void recalculateAllocation(StorageLocation location, UnitLoad... knownAsRemoved) throws FacadeException;
	
}
