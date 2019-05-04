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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.Zone;
import org.mywms.service.BasicService;
import org.mywms.service.ZoneService;


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
