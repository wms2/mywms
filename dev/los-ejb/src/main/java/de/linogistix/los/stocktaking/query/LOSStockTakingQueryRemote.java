/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.query;

import javax.ejb.Remote;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.stocktaking.model.LOSStockTaking;

@Remote
public interface LOSStockTakingQueryRemote extends
		BusinessObjectQueryRemote<LOSStockTaking> {

}
