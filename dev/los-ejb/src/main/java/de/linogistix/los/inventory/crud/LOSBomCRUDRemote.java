/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.model.LOSBom;

/**
 * @author krane
 *
 */
@Remote
public interface LOSBomCRUDRemote extends BusinessObjectCRUDRemote<LOSBom>{

}
