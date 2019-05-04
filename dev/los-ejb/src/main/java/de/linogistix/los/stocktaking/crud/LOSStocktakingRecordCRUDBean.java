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

package de.linogistix.los.stocktaking.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.service.LOSStocktakingRecordService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSStocktakingRecordCRUDBean extends BusinessObjectCRUDBean<LOSStocktakingRecord> implements LOSStocktakingRecordCRUDRemote {

	@EJB 
	LOSStocktakingRecordService service;
	
	@Override
	protected BasicService<LOSStocktakingRecord> getBasicService() {
		
		return service;
	}
}
