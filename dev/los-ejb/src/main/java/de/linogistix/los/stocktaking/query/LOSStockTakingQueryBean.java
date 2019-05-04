/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.query;

import javax.ejb.Stateless;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.stocktaking.model.LOSStockTaking;

@Stateless
public class LOSStockTakingQueryBean extends
		BusinessObjectQueryBean<LOSStockTaking> implements
		LOSStockTakingQueryRemote {

}
