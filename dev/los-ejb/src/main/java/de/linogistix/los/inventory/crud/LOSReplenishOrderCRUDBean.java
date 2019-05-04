/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.inventory.service.LOSReplenishOrderService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishOrderCRUDBean extends BusinessObjectCRUDBean<LOSReplenishOrder> implements LOSReplenishOrderCRUDRemote {

	@EJB 
	LOSReplenishOrderService service;
	
	@Override
	protected BasicService<LOSReplenishOrder> getBasicService() {
		
		return service;
	}
}
