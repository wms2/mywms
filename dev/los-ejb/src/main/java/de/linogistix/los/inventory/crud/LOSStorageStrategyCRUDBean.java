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

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.inventory.service.LOSStorageStrategyService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.strategy.StorageStrategyRichClientConverter;

/**
 * @author krane
 *
 */
@Stateless
public class LOSStorageStrategyCRUDBean extends BusinessObjectCRUDBean<StorageStrategy>
		implements LOSStorageStrategyCRUDRemote {

	@EJB
	LOSStorageStrategyService service;

	@Override
	protected BasicService<StorageStrategy> getBasicService() {
		return service;
	}

	@Override
	public void update(StorageStrategy entity) throws BusinessObjectNotFoundException, BusinessObjectModifiedException,
			BusinessObjectMergeException, BusinessObjectSecurityException, FacadeException {
		String sorts = StorageStrategyRichClientConverter.convertOrderByModeToSorts(entity.getOrderByMode());
		if (sorts != null) {
			entity.setSorts(sorts);
		}
		super.update(entity);
	}
}
