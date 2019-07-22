/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Remote;

import de.wms2.mywms.location.LocationType;

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
    public LocationType getDefaultStorageLocationType();
}
