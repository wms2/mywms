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
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSPickingUnitLoadCRUDBean extends BusinessObjectCRUDBean<LOSPickingUnitLoad> implements LOSPickingUnitLoadCRUDRemote {

	@EJB 
	LOSPickingUnitLoadService service;
	
	@Override
	protected BasicService<LOSPickingUnitLoad> getBasicService() {
		
		return service;
	}
}
