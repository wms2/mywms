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
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 * @author krane
 *
 */
public class CustomLocationServiceBean implements CustomLocationService {
	
    @PersistenceContext(unitName = "myWMS")
    private EntityManager manager;
    
	@Override
	public void onLocationGetsEmpty(StorageLocation location) throws FacadeException {
		onLocationGetsEmpty(location, true);
	}
	
	@Override
	public void onLocationGetsEmpty(StorageLocation location, boolean checkEmptyLocation) throws FacadeException {
		location.setAllocation(BigDecimal.ZERO);
		location.setCurrentTypeCapacityConstraint(null);
	}
	
	public void checkAllocateLocation(StorageLocation location, UnitLoad unitLoad, TypeCapacityConstraint constraint) throws LOSLocationAlreadyFullException,LOSLocationNotSuitableException,LOSLocationWrongClientException,LOSLocationReservedException {
	}
	
	public void allocateLocation(StorageLocation location, UnitLoad unitLoad, TypeCapacityConstraint constraint) throws FacadeException {
	}
	public void deallocateLocation(StorageLocation location, UnitLoad unitLoad, TypeCapacityConstraint constraint, boolean checkEmptyLocation) throws FacadeException {
	}

	public void onUnitLoadRemoved(StorageLocation location, UnitLoad unitLoad) throws FacadeException {
	}
	public void onUnitLoadPlaced(StorageLocation location, UnitLoad unitLoad) throws FacadeException {
	}
	
	@SuppressWarnings("unchecked")
	public int setLocationOrderIndex( String rack, int startValue, int diffValue ) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT location FROM " + StorageLocation.class.getSimpleName() + " location ");
        sb.append(" WHERE rack=:rack ");
        sb.append(" ORDER BY location.XPos, location.YPos, location.name ");
        
        Query query = manager.createQuery(sb.toString());
        query.setParameter("rack", rack);
        List<StorageLocation> locations = query.getResultList();
        
        int orderIndex = startValue-diffValue;
        for( StorageLocation location : locations ) {
        	orderIndex += diffValue;
        	location.setOrderIndex(orderIndex);
        }
        

        return orderIndex;
	}

}
