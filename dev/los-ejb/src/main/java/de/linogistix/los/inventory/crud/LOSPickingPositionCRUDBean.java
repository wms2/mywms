/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.service.LOSPickingPositionService;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSPickingPositionCRUDBean extends BusinessObjectCRUDBean<LOSPickingPosition> implements LOSPickingPositionCRUDRemote {

	@EJB 
	LOSPickingPositionService service;
	
	@Override
	protected BasicService<LOSPickingPosition> getBasicService() {
		
		return service;
	}
}
