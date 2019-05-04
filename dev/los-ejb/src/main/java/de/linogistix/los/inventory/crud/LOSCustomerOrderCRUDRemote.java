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
import de.linogistix.los.inventory.model.LOSCustomerOrder;


/**
 * @author krane
 *
 */
@Remote
public interface LOSCustomerOrderCRUDRemote extends BusinessObjectCRUDRemote<LOSCustomerOrder>{

}
