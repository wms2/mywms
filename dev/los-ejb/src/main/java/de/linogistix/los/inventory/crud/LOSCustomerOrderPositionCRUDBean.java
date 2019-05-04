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
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.service.LOSCustomerOrderPositionService;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSCustomerOrderPositionCRUDBean extends BusinessObjectCRUDBean<LOSCustomerOrderPosition> implements LOSCustomerOrderPositionCRUDRemote {

	@EJB 
	LOSCustomerOrderPositionService service;
	
	@Override
	protected BasicService<LOSCustomerOrderPosition> getBasicService() {
		
		return service;
	}
}
