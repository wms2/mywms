/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query;

import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.query.BusinessObjectQueryRemote;

import javax.ejb.Remote;

/**
 *
 * @author Jordan
 */
@Remote
public interface LOSStorageLocationTypeQueryRemote 
        extends BusinessObjectQueryRemote<LOSStorageLocationType>
{
    
}
