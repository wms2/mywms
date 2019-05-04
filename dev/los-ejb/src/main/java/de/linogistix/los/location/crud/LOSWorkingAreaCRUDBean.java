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
import de.linogistix.los.location.entityservice.LOSWorkingAreaService;
import de.linogistix.los.location.model.LOSWorkingArea;



/**
 * @author krane
 *
 */
@Stateless
public class LOSWorkingAreaCRUDBean extends BusinessObjectCRUDBean<LOSWorkingArea> implements LOSWorkingAreaCRUDRemote {

	@EJB 
	LOSWorkingAreaService service;
	
	@Override
	protected BasicService<LOSWorkingArea> getBasicService() {
		
		return service;
	}
}
