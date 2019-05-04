/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.entityservice.LOSWorkingAreaPositionService;
import de.linogistix.los.location.model.LOSWorkingAreaPosition;



/**
 * @author krane
 *
 */
@Stateless
public class LOSWorkingAreaPositionCRUDBean extends BusinessObjectCRUDBean<LOSWorkingAreaPosition> implements LOSWorkingAreaPositionCRUDRemote {

	@EJB 
	LOSWorkingAreaPositionService service;
	
	@Override
	protected BasicService<LOSWorkingAreaPosition> getBasicService() {
		
		return service;
	}
}
