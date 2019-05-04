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

import org.mywms.model.ItemUnit;
import org.mywms.service.BasicService;
import org.mywms.service.ItemUnitService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;


/**
 * @author trautm
 *
 */
@Stateless
public class ItemUnitCRUDBean extends BusinessObjectCRUDBean<ItemUnit> implements ItemUnitCRUDRemote {

	@EJB 
	ItemUnitService service;
	
	@Override
	protected BasicService<ItemUnit> getBasicService() {
		
		return service;
	}
}
