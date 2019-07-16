/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.wms2.mywms.delivery.DeliveryOrderLine;


/**
 * @author krane
 *
 */
@Remote
public interface LOSCustomerOrderPositionCRUDRemote extends BusinessObjectCRUDRemote<DeliveryOrderLine>{

}
