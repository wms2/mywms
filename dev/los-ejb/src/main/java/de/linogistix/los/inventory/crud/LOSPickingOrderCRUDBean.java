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
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.service.LOSPickingOrderService;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSPickingOrderCRUDBean extends BusinessObjectCRUDBean<LOSPickingOrder> implements LOSPickingOrderCRUDRemote {

	@EJB 
	LOSPickingOrderService service;
	
	@Override
	protected BasicService<LOSPickingOrder> getBasicService() {
		
		return service;
	}
}
