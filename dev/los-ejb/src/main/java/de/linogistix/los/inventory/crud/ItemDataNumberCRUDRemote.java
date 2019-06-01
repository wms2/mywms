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
import de.wms2.mywms.product.ItemDataNumber;

/**
 * @see  BusinessObjectCRUDRemote
 * 
 * @author krane
 *
 */
@Remote
public interface ItemDataNumberCRUDRemote extends BusinessObjectCRUDRemote<ItemDataNumber>{

}
