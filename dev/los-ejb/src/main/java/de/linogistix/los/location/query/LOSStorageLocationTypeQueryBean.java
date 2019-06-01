/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.location.query;

import javax.ejb.Stateless;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.wms2.mywms.location.LocationType;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationTypeQueryBean 
        extends BusinessObjectQueryBean<LocationType>
        implements LOSStorageLocationTypeQueryRemote 
{

    @Override
    public String getUniqueNameProp() {
        return "name";
    }
    
    
 
}
