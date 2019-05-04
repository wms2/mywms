/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.model.LOSUnitLoad;

@Stateless
public class LOSUnitLoadCRUDBean 
			extends BusinessObjectCRUDBean<LOSUnitLoad>
			implements LOSUnitLoadCRUDRemote 
{

	@EJB
	private LOSUnitLoadService ulService;
	
	@Override
	protected BasicService<LOSUnitLoad> getBasicService() {
		return ulService; 
	}

	
}
