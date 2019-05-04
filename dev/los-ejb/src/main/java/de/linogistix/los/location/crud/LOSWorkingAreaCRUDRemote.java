/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.model.LOSWorkingArea;



/**
 * @author krane
 *
 */
@Remote
public interface LOSWorkingAreaCRUDRemote extends BusinessObjectCRUDRemote<LOSWorkingArea>{

}
