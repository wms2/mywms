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

package de.linogistix.los.location.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.service.ZoneService;
import de.wms2.mywms.strategy.Zone;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;


/**
 * @author trautm
 *
 */
@Stateless
public class ZoneCRUDBean extends BusinessObjectCRUDBean<Zone> implements ZoneCRUDRemote {

	@EJB 
	ZoneService service;
	
	@Override
	protected BasicService<Zone> getBasicService() {
		// TODO Auto-generated method stub
		return service;
	}
}
