/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.wms2.mywms.replenish.ReplenishBusiness;

/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishFacadeBean implements LOSReplenishFacade {
	private Logger log = Logger.getLogger(LOSReplenishFacadeBean.class);

	@Inject
	private ReplenishBusiness replenishBusiness;

	public void refillFixedLocations() throws FacadeException {
		String logStr = "refillFixedLocations ";
		log.debug(logStr);

		replenishBusiness.refillFixAssignments();
	}
}
