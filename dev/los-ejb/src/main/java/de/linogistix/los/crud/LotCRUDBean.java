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

package de.linogistix.los.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.Lot;
import org.mywms.service.BasicService;
import org.mywms.service.LotService;


/**
 * @author trautm
 *
 */
@Stateless
public class LotCRUDBean extends BusinessObjectCRUDBean<Lot> implements LotCRUDRemote {

	@EJB 
	LotService service;
	
	@Override
	protected BasicService<Lot> getBasicService() {
		return service;
	}
}
