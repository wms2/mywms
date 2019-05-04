/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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
import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.inventory.service.QueryUnitLoadAdvicePositionService;

@Stateless
public class LOSUnitLoadAdvicePositionCRUDBean extends BusinessObjectCRUDBean<LOSUnitLoadAdvicePosition> 
											   implements LOSUnitLoadAdvicePositionCRUDRemote 
{

	@EJB
	private QueryUnitLoadAdvicePositionService queryPositionService;
	
	@Override
	protected BasicService<LOSUnitLoadAdvicePosition> getBasicService() {
		
		return queryPositionService;
	}



}
