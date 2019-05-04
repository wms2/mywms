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
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSOrderStrategyCRUDBean extends BusinessObjectCRUDBean<LOSOrderStrategy> implements LOSOrderStrategyCRUDRemote {

	@EJB 
	LOSOrderStrategyService service;
	
	@Override
	protected BasicService<LOSOrderStrategy> getBasicService() {
		
		return service;
	}
}
