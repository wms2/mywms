/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.stocktaking.model.LOSStockTaking;

@Remote
public interface LOSStockTakingCRUDRemote extends
		BusinessObjectCRUDRemote<LOSStockTaking> {

}
