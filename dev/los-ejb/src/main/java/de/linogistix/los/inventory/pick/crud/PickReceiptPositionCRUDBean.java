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

package de.linogistix.los.inventory.pick.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.pick.model.PickReceiptPosition;
import de.linogistix.los.inventory.pick.service.PickReceiptPositionService;



/**
 * @author trautm
 *
 */
@Stateless
public class PickReceiptPositionCRUDBean extends BusinessObjectCRUDBean<PickReceiptPosition> implements PickReceiptPositionCRUDRemote{

	@EJB 
	PickReceiptPositionService service;
	
	@Override
	protected BasicService<PickReceiptPosition> getBasicService() {	
		return service;
	}
}
