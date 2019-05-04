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
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.service.QueryUnitLoadAdviceService;

@Stateless
public class LOSUnitLoadAdviceCRUDBean extends BusinessObjectCRUDBean<LOSUnitLoadAdvice>
		implements LOSUnitLoadAdviceCRUDRemote {

	@EJB
	private QueryUnitLoadAdviceService unitLoadAdviceService;
	
	@Override
	protected BasicService<LOSUnitLoadAdvice> getBasicService() {
		
		return unitLoadAdviceService;
	}

}
