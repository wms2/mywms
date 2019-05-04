/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.crud;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.model.LOSStorageLocationType;

import javax.ejb.Remote;

/**
 *
 * @author Jordan
 */
@Remote
public interface LOSStorageLocationTypeCRUDRemote
        extends BusinessObjectCRUDRemote<LOSStorageLocationType>
{
    
}
