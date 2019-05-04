/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.location.query;

import javax.ejb.Stateless;

import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.query.BusinessObjectQueryBean;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationTypeQueryBean 
        extends BusinessObjectQueryBean<LOSStorageLocationType>
        implements LOSStorageLocationTypeQueryRemote 
{

    @Override
    public String getUniqueNameProp() {
        return "name";
    }
    
    
 
}
