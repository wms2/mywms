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
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.service.LOSStockUnitRecordService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;



/**
 * @author trautm
 *
 */
@Stateless
public class LOSStockUnitRecordCRUDBean extends BusinessObjectCRUDBean<LOSStockUnitRecord> implements LOSStockUnitRecordCRUDRemote {

	@EJB 
	LOSStockUnitRecordService service;
	
	@Override
	protected BasicService<LOSStockUnitRecord> getBasicService() {
		
		return service;
	}
}
