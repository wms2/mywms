/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.location.LocationType;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSStorageLocationTypeService 
        extends BasicService<LocationType>
{
    /**
     * Returns the default LOSStorageLocationType for a warehouse
     * @return
     */
    public LocationType getDefaultStorageLocationType();
    
    /**
	 * Returns a LOSStorageLocationType with a fixed unit load attached to it.
	 */
    public LocationType getAttachedUnitLoadType();
    
}
