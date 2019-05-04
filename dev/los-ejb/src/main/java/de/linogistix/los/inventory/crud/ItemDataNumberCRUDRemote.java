/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import org.mywms.model.ItemDataNumber;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;

/**
 * @see  BusinessObjectCRUDRemote
 * 
 * @author krane
 *
 */
@Remote
public interface ItemDataNumberCRUDRemote extends BusinessObjectCRUDRemote<ItemDataNumber>{

}
