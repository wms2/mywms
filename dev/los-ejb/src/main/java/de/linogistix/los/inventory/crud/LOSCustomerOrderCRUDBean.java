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
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.wms2.mywms.delivery.DeliveryOrder;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSCustomerOrderCRUDBean extends BusinessObjectCRUDBean<DeliveryOrder> implements LOSCustomerOrderCRUDRemote {

	@EJB 
	LOSCustomerOrderService service;
	
	@Override
	protected BasicService<DeliveryOrder> getBasicService() {
		
		return service;
	}
}
