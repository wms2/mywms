/*
 * Copyright (c) 2010 LinogistiX GmbH
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
import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.inventory.service.LOSBomService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSBomCRUDBean extends BusinessObjectCRUDBean<LOSBom> implements LOSBomCRUDRemote {

	@EJB 
	LOSBomService service;
	
	@Override
	protected BasicService<LOSBom> getBasicService() {
		
		return service;
	}
	
}
