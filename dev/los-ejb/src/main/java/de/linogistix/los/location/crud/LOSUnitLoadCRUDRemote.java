/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.model.LOSUnitLoad;

@Remote
public interface LOSUnitLoadCRUDRemote 
		extends	BusinessObjectCRUDRemote<LOSUnitLoad> 

{

}
