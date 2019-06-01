/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.location.LocationType;

import javax.ejb.Remote;

/**
 *
 * @author Jordan
 */
@Remote
public interface LOSStorageLocationTypeQueryRemote 
        extends BusinessObjectQueryRemote<LocationType>
{
    
}
