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
import de.linogistix.los.inventory.service.LOSCustomerOrderPositionService;
import de.wms2.mywms.delivery.DeliveryOrderLine;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSCustomerOrderPositionCRUDBean extends BusinessObjectCRUDBean<DeliveryOrderLine> implements LOSCustomerOrderPositionCRUDRemote {

	@EJB 
	LOSCustomerOrderPositionService service;
	
	@Override
	protected BasicService<DeliveryOrderLine> getBasicService() {
		
		return service;
	}
}
