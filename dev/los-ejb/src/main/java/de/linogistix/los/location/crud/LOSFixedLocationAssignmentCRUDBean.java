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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.entityservice.LOSFixedLocationAssignmentService;
import de.wms2.mywms.strategy.FixAssignment;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSFixedLocationAssignmentCRUDBean extends BusinessObjectCRUDBean<FixAssignment> implements LOSFixedLocationAssignmentCRUDRemote {

	@EJB 
	LOSFixedLocationAssignmentService service;
	
	@Override
	protected BasicService<FixAssignment> getBasicService() {
		
		return service;
	}
}
