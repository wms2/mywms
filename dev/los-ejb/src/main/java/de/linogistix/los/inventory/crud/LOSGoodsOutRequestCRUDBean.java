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

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.wms2.mywms.shipping.ShippingOrder;



/**
 * @author trautm
 *
 */
@Stateless
public class LOSGoodsOutRequestCRUDBean extends BusinessObjectCRUDBean<ShippingOrder> implements LOSGoodsOutRequestCRUDRemote{

	@EJB 
	LOSGoodsOutRequestService service;
	
	@Override
	protected BasicService<ShippingOrder> getBasicService() {
		return service;
	}
	
}
