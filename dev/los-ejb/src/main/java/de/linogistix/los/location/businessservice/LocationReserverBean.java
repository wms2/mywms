package de.linogistix.los.location.businessservice;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;

@Stateless
public class LocationReserverBean implements LocationReserver {
	private static final Logger log = Logger.getLogger(LocationReserverBean.class);

	@EJB
	private QueryTypeCapacityConstraintService capacityService;
	@EJB
	private CustomLocationService customLocationService;

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	public LOSTypeCapacityConstraint checkAllocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, boolean ignoreLock) throws LOSLocationAlreadyFullException,LOSLocationNotSuitableException,LOSLocationWrongClientException,LOSLocationReservedException {
		String logStr = "checkReserveLocation ";
		
		if( location == null ) {
			log.info(logStr+"Missing parameter loaction. Cannot reserve.");
			throw new LOSLocationNotSuitableException("", "" );
		}
		if( unitLoad == null ) {
			log.info(logStr+"Missing parameter unitLoad. Cannot reserve.");
			throw new LOSLocationNotSuitableException("", "" );
		}
		
		if ((!location.getClient().isSystemClient())
				&& (!unitLoad.getClient().isSystemClient())
				&& (!location.getClient().equals(unitLoad.getClient()))) {
			log.info(logStr+"The client of unitload is not ok. Cannot reserve. client-location="+location.getClient().getNumber()+", client-unit-load="+unitLoad.getClient().getNumber());
			throw new LOSLocationWrongClientException( location.getClient().getNumber(), unitLoad.getClient().getNumber());
		}
		
		if (!ignoreLock && location.getLock() != LOSStorageLocationLockState.NOT_LOCKED.getLock() && location.getLock() != LOSStorageLocationLockState.RETRIEVAL.getLock()) {
			log.info(logStr+"The location is locked. Cannot reserve. name="+location.getName());
			throw new LOSLocationNotSuitableException(unitLoad.getLabelId(), location.getType().getName() );
		}
		
		LOSTypeCapacityConstraint constraintLocation = location.getCurrentTypeCapacityConstraint();
		BigDecimal allocationLocation = location.getAllocation();
		if( constraintLocation == null ) {
			allocationLocation = BigDecimal.ZERO;
		}
		if( allocationLocation.compareTo(HUNDRED) >= 0 ) {
			log.info(logStr+"Not enough space on location. Cannot reserve. name="+location.getName());
			throw new LOSLocationAlreadyFullException(location.getName());
		}

		LOSTypeCapacityConstraint constraintUnitLoad = capacityService.getByTypes(location.getType(), unitLoad.getType());
		
		if( constraintLocation == null && constraintUnitLoad == null ) {
			if( capacityService.getListByLocationType(location.getType()).size() == 0 ) {
				log.debug(logStr+"No constraint defined for location. OK. type="+location.getType().getName());
				return null;
			}
			log.info(logStr+"No constraint defined for location / unitload. Cannot reserve. location-type="+location.getType().getName()+" unitload-type="+unitLoad.getType().getName());
			throw new LOSLocationNotSuitableException(unitLoad.getLabelId(), location.getType().getName() );
		}
		if( constraintLocation != null && constraintUnitLoad == null ) {
			log.info(logStr+"No constraint defined for location / unitload. Cannot reserve. location-type="+location.getType().getName()+" unitload-type="+unitLoad.getType().getName());
			throw new LOSLocationNotSuitableException(unitLoad.getLabelId(), location.getType().getName() );
		}
		if( constraintLocation == null && constraintUnitLoad != null ) {
			log.info(logStr+"Constraint defined for location. OK.");
			customLocationService.checkAllocateLocation( location, unitLoad, constraintUnitLoad );
			return constraintUnitLoad;
		}
		
		if( constraintLocation.getAllocationType() != constraintUnitLoad.getAllocationType() ) {
			log.info(logStr+"Diffenent constraints for location / unitload. Cannot reserve. location-type="+location.getType().getName()+" unitload-type="+unitLoad.getType().getName());
			throw new LOSLocationNotSuitableException(unitLoad.getLabelId(), location.getType().getName() );
		}
		
		if( constraintLocation.getAllocationType() == LOSTypeCapacityConstraint.ALLOCATE_UNIT_LOAD_TYPE ) {
			if( ! unitLoad.getType().equals(constraintLocation.getUnitLoadType()) ) {
				log.info(logStr+"Wrong unit load type on location. Cannot reserve. location-type="+constraintLocation.getUnitLoadType().getName()+" unitload-type="+unitLoad.getType().getName());
				throw new LOSLocationNotSuitableException(unitLoad.getLabelId(), location.getType().getName() );
			}
			allocationLocation = allocationLocation.add(constraintLocation.getAllocation());
			if( allocationLocation.compareTo(HUNDRED) > 0 ) {
				log.info(logStr+"Not enough space on location. Cannot reserve. name="+location.getName());
				throw new LOSLocationAlreadyFullException(location.getName());
			}
		}
		if( constraintLocation.getAllocationType() == LOSTypeCapacityConstraint.ALLOCATE_PERCENTAGE ) {
			allocationLocation = allocationLocation.add(constraintLocation.getAllocation());
			if( allocationLocation.compareTo(HUNDRED) > 0 ) {
				log.info(logStr+"Not enough space on location. Cannot reserve. name="+location.getName());
				throw new LOSLocationAlreadyFullException(location.getName());
			}
		}

		customLocationService.checkAllocateLocation( location, unitLoad, constraintLocation );

		return constraintLocation;
	}

	public LOSTypeCapacityConstraint allocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad) throws FacadeException {
		String logStr = "allocateLocation ";

		if( location == null ) {
			log.info(logStr+"Missing parameter loaction. Cannot reserve.");
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{});
		}
		if( unitLoad == null ) {
			log.info(logStr+"Missing parameter unitLoad. Cannot reserve.");
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new Object[]{});
		}
		
		LOSTypeCapacityConstraint constraintLocation = location.getCurrentTypeCapacityConstraint();
		BigDecimal allocationLocation = location.getAllocation();
		if( constraintLocation == null ) {
			allocationLocation = BigDecimal.ZERO;
		}
		LOSTypeCapacityConstraint constraintUnitLoad = capacityService.getByTypes(location.getType(), unitLoad.getType());
		if( constraintLocation == null ) {
			constraintLocation = constraintUnitLoad;
		}

		if( constraintLocation != null && constraintUnitLoad != null ) {
			allocationLocation = allocationLocation.add(constraintUnitLoad.getAllocation());
		}
		
		location.setAllocation(allocationLocation);
		location.setCurrentTypeCapacityConstraint(constraintLocation);
		log.info(logStr+"location="+location.getName()+", unitLoad="+unitLoad.getLabelId()+", allocation="+allocationLocation);

		customLocationService.allocateLocation( location, unitLoad, constraintLocation );

		return constraintLocation;
	}

	public void deallocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad) throws FacadeException {
		deallocateLocation(location, unitLoad, true);
	}
	
	public void deallocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, boolean checkEmptyLocation) throws FacadeException {
		String logStr = "deallocateLocation ";

		if( location == null ) {
			log.info(logStr+"Missing parameter loaction. Cannot reserve.");
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{});
		}
		if( unitLoad == null ) {
			log.info(logStr+"Missing parameter unitLoad. Cannot reserve.");
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new Object[]{});
		}

		LOSTypeCapacityConstraint constraintLocation = location.getCurrentTypeCapacityConstraint();
		BigDecimal allocationLocationStart = location.getAllocation();
		
		if( constraintLocation == null ) {
			log.warn(logStr+"Relese reservation on location where nothing is reserved. location name="+location.getName());
			location.setAllocation(BigDecimal.ZERO);
			return;
		}

		LOSTypeCapacityConstraint constraintUnitLoad = capacityService.getByTypes(location.getType(), unitLoad.getType());
		
		if( constraintUnitLoad != null ) {
			location.setAllocation(location.getAllocation().subtract(constraintUnitLoad.getAllocation()));
		}
		
		log.info(logStr+"location="+location.getName()+", unitLoad="+unitLoad.getLabelId()+", start="+allocationLocationStart);

		if( location.getAllocation().compareTo(BigDecimal.ZERO)<=0 ) {
			location.setAllocation( BigDecimal.ZERO );
			location.setCurrentTypeCapacityConstraint(null);

			// Do this check only when the location becomes empty
			// Generally not allocated locations should not be checked
			if( checkEmptyLocation ) {
				if( allocationLocationStart.compareTo(BigDecimal.ZERO)!=0 ) {
					for( LOSUnitLoad ul : location.getUnitLoads() ) {
						if( !ul.equals(unitLoad) ) {
							log.warn(logStr+"Something went wrong with location allocation. allocation<=0, but still unitloads on location. Try to correct. location name="+location.getName());
							recalculateAllocation(location, unitLoad);
							break;
						}
					}
				}
			}
		}

		customLocationService.deallocateLocation( location, unitLoad, constraintLocation, checkEmptyLocation );

		if( BigDecimal.ZERO.compareTo(location.getAllocation()) == 0 ){
			customLocationService.onLocationGetsEmpty(location, checkEmptyLocation);
		}

	}
	
	public void deallocateLocationComplete(LOSStorageLocation location) throws FacadeException {
		String logStr = "releaseLocation ";

		if( location == null ) {
			log.info(logStr+"Missing parameter loaction. Cannot release.");
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{});
		}
		
		location.setCurrentTypeCapacityConstraint(null);
		location.setAllocation(BigDecimal.ZERO);
	}
	
	public void recalculateAllocation(LOSStorageLocation location, LOSUnitLoad... knownAsRemoved) throws FacadeException {
		String logStr = "recalculateAllocation ";

		for( LOSUnitLoad unitLoad : location.getUnitLoads() ) {
			boolean ignore = false;
			
			if( knownAsRemoved != null && knownAsRemoved.length>0 ) {
				for( LOSUnitLoad ul : knownAsRemoved ) {
					if( ul.equals(unitLoad) ) {
						ignore = true;
						break;
					}
				}
				if( ignore ) {
					continue;
				}
			}
			
			LOSTypeCapacityConstraint tcc = capacityService.getByTypes(location.getType(), unitLoad.getType());
			if( tcc == null ) {
				log.warn(logStr+"No capacity constraint, no recalculate. location="+location.getName()+", type="+location.getType().getName()+", unitLoadType="+unitLoad.getType().getName());
				continue;
			}
			
			if( location.getCurrentTypeCapacityConstraint() == null ) {
				location.setCurrentTypeCapacityConstraint(tcc);
			}
			
			location.setAllocation( location.getAllocation().add(tcc.getAllocation()));
		}
	}

}
