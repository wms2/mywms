/*
 * Copyright (c) 2010 LinogistiX GmbH
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
import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.wms2.mywms.product.ItemDataNumber;


/**
 * @author krane
 *
 */
@Stateless
public class ItemDataNumberCRUDBean extends BusinessObjectCRUDBean<ItemDataNumber> implements ItemDataNumberCRUDRemote {

	@EJB 
	ItemDataNumberService service;
	
	@Override
	protected BasicService<ItemDataNumber> getBasicService() {
		return service;
	}
	
}
