/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.wms2.mywms.strategy.StorageStrategy;

/**
 * @author krane
 *
 */
@Remote
public interface LOSStorageStrategyCRUDRemote extends BusinessObjectCRUDRemote<StorageStrategy>{

}
