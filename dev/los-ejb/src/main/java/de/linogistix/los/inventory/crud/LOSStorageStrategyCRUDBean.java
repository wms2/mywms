/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.service.LOSStorageStrategyService;
import de.wms2.mywms.strategy.StorageStrategy;


/**
 * @author krane
 *
 */
@Stateless
public class LOSStorageStrategyCRUDBean extends BusinessObjectCRUDBean<StorageStrategy> implements LOSStorageStrategyCRUDRemote {

	@EJB 
	LOSStorageStrategyService service;
	
	@Override
	protected BasicService<StorageStrategy> getBasicService() {
		return service;
	}
	

}
