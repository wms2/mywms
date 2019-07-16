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
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.wms2.mywms.picking.PickingOrderLine;


/**
 * @author trautm
 *
 */
@Stateless
public class LOSPickingPositionCRUDBean extends BusinessObjectCRUDBean<PickingOrderLine> implements LOSPickingPositionCRUDRemote {

	@EJB 
	LOSPickingPositionService service;
	
	@Override
	protected BasicService<PickingOrderLine> getBasicService() {
		
		return service;
	}
}
