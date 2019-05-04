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
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.service.LOSStocktakingOrderService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSStocktakingOrderCRUDBean extends BusinessObjectCRUDBean<LOSStocktakingOrder> implements LOSStocktakingOrderCRUDRemote {

	@EJB 
	LOSStocktakingOrderService service;
	
	@Override
	protected BasicService<LOSStocktakingOrder> getBasicService() {
		
		return service;
	}
}
