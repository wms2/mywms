/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSStorageLocationTypeService 
        extends BasicService<LocationType>
{
    
    public LocationType create(String name)
            throws UniqueConstraintViolatedException;
    
    public LocationType getByName(String name)
            throws EntityNotFoundException;

    public List<TypeCapacityConstraint> getByLocationType(LocationType slType);
    
    public List<TypeCapacityConstraint> getByUnitLoadType(UnitLoadType ulType);
    
    /**
     * Returns the default LOSStorageLocationType for a warehouse
     * @return
     */
    public LocationType getDefaultStorageLocationType();
    
    /**
     * Returns a LOSStorageLocationType without any restrictions. 
     * @return
     */
    public LocationType getNoRestrictionType();
    
    /**
	 * Returns a LOSStorageLocationType with a fixed unit load attached to it.
	 */
    public LocationType getAttachedUnitLoadType();
    
}
