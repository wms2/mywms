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

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.ItemData;
import org.mywms.service.BasicService;
import org.mywms.service.ItemDataService;


/**
 * @author trautm
 *
 */
@Stateless
public class ItemDataCRUDBean extends BusinessObjectCRUDBean<ItemData> implements ItemDataCRUDRemote {

	@EJB 
	ItemDataService service;
	
	@Override
	protected BasicService<ItemData> getBasicService() {
		
		return service;
	}
	
	@Override
	public ItemData create(ItemData entity)
			throws BusinessObjectExistsException,
			BusinessObjectCreationException, BusinessObjectSecurityException {
		
		if (entity.getNumber() == null || entity.getNumber().length() == 0) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"number"}, BundleResolver.class);
		if (entity.getClient() == null ) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"client"}, BundleResolver.class);
		if (entity.getName() == null || entity.getName().length() == 0) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"name"}, BundleResolver.class);
		if (entity.getHandlingUnit() == null) throw new BusinessObjectCreationException("missing name", BusinessObjectCreationException.MISSING_FIELD_KEY, new String[]{"handlingUnit"}, BundleResolver.class);
				
		return super.create(entity);
	}
	
}
