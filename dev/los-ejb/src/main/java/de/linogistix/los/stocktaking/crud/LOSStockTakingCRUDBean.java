/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.stocktaking.model.LOSStockTaking;
import de.linogistix.los.stocktaking.service.LOSStockTakingService;

@Stateless
public class LOSStockTakingCRUDBean extends
		BusinessObjectCRUDBean<LOSStockTaking> implements
		LOSStockTakingCRUDRemote {

	@EJB
	private LOSStockTakingService stockTakingService;
	
	@Override
	protected BasicService<LOSStockTaking> getBasicService() {
		
		return stockTakingService;
	}

	
}
