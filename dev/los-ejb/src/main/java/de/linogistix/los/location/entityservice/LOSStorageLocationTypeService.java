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

import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSStorageLocationTypeService 
        extends BasicService<LOSStorageLocationType>
{
    
    public LOSStorageLocationType create(String name)
            throws UniqueConstraintViolatedException;
    
    public LOSStorageLocationType getByName(String name)
            throws EntityNotFoundException;

    public List<LOSTypeCapacityConstraint> getByLocationType(LOSStorageLocationType slType);
    
    public List<LOSTypeCapacityConstraint> getByUnitLoadType(UnitLoadType ulType);
    
    /**
     * Returns the default LOSStorageLocationType for a warehouse
     * @return
     */
    public LOSStorageLocationType getDefaultStorageLocationType();
    
    /**
     * Returns a LOSStorageLocationType without any restrictions. 
     * @return
     */
    public LOSStorageLocationType getNoRestrictionType();
    
    /**
	 * Returns a LOSStorageLocationType with a fixed unit load attached to it.
	 */
    public LOSStorageLocationType getAttachedUnitLoadType();
    
}
