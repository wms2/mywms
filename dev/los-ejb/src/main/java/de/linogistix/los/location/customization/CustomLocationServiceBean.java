/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.customization;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
public class CustomLocationServiceBean implements CustomLocationService {
	
    @PersistenceContext(unitName = "myWMS")
    private EntityManager manager;
    
	@Override
	public void onLocationGetsEmpty(LOSStorageLocation location) throws FacadeException {
		onLocationGetsEmpty(location, true);
	}
	
	@Override
	public void onLocationGetsEmpty(LOSStorageLocation location, boolean checkEmptyLocation) throws FacadeException {
		location.setAllocation(BigDecimal.ZERO);
		location.setCurrentTypeCapacityConstraint(null);
	}
	
	public void checkAllocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, LOSTypeCapacityConstraint constraint) throws LOSLocationAlreadyFullException,LOSLocationNotSuitableException,LOSLocationWrongClientException,LOSLocationReservedException {
	}
	
	public void allocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, LOSTypeCapacityConstraint constraint) throws FacadeException {
	}
	public void deallocateLocation(LOSStorageLocation location, LOSUnitLoad unitLoad, LOSTypeCapacityConstraint constraint, boolean checkEmptyLocation) throws FacadeException {
	}

	public void onUnitLoadRemoved(LOSStorageLocation location, LOSUnitLoad unitLoad) throws FacadeException {
	}
	public void onUnitLoadPlaced(LOSStorageLocation location, LOSUnitLoad unitLoad) throws FacadeException {
	}
	
	@SuppressWarnings("unchecked")
	public int setLocationOrderIndex( LOSRack rack, int startValue, int diffValue ) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT location FROM " + LOSStorageLocation.class.getSimpleName() + " location ");
        sb.append(" WHERE rack=:rack ");
        sb.append(" ORDER BY location.XPos, location.YPos, location.name ");
        
        Query query = manager.createQuery(sb.toString());
        query.setParameter("rack", rack);
        List<LOSStorageLocation> locations = query.getResultList();
        
        int orderIndex = startValue-diffValue;
        for( LOSStorageLocation location : locations ) {
        	orderIndex += diffValue;
        	location.setOrderIndex(orderIndex);
        }
        

        return orderIndex;
	}

}
