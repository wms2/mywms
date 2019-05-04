/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Remote;

import de.linogistix.los.location.model.LOSStorageLocationType;

/**
 * @author krane
 *
 */
@Remote
public interface LOSStorageLocationTypeServiceRemote {
    
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
