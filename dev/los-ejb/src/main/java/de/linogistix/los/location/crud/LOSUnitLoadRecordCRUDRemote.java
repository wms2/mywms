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
import de.linogistix.los.location.model.LOSUnitLoadRecord;

@Remote
public interface LOSUnitLoadRecordCRUDRemote 
		extends	BusinessObjectCRUDRemote<LOSUnitLoadRecord> 

{

}
