/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.StockUnit;
import org.mywms.service.BasicService;
import org.mywms.service.StockUnitService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.entityservice.BusinessObjectLockService;
import de.linogistix.los.runtime.BusinessObjectSecurityException;


/**
 * @author trautm
 *
 */
@Stateless
public class StockUnitCRUDBean extends BusinessObjectCRUDBean<StockUnit> implements StockUnitCRUDRemote {

	@EJB 
	StockUnitService service;
	
	@EJB
	BusinessObjectLockService lockService;
	
	@Override
	protected BasicService<StockUnit> getBasicService() {
		
		return service;
	}
	
	@Override
	public void lock(StockUnit entity, int lock, String lockCause)
			throws BusinessObjectSecurityException {
		
		lockService.lock(entity, lock, lockCause);
		
	}
}
