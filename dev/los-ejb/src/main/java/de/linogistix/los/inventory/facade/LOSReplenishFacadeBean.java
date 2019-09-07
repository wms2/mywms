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

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.wms2.mywms.replenish.ReplenishBusiness;
import de.wms2.mywms.replenish.ReplenishOrder;
import de.wms2.mywms.replenish.ReplenishOrderEntityService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishFacadeBean implements LOSReplenishFacade {
	private Logger log = Logger.getLogger(LOSReplenishFacadeBean.class);

	@Inject
	private ReplenishOrderEntityService replenishServie;
	@Inject
	private ReplenishBusiness replenishBusiness;

	public void refillFixedLocations() throws FacadeException {
		String logStr = "refillFixedLocations ";
		log.debug(logStr);

		replenishBusiness.refillFixAssignments();
	}

	public void finishOrder(String orderNumber) throws FacadeException {
		String logStr = "finishOrder ";
		log.debug(logStr + "orderNumber=" + orderNumber);

		ReplenishOrder order = replenishServie.read(orderNumber);
		if (order == null) {
			String msg = "Replenish order does not exist. number=" + orderNumber;
			log.error(logStr + msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		replenishBusiness.cancelOrder(order);
	}

	public void removeOrder(String orderNumber) throws FacadeException {
		String logStr = "removeOrder ";
		log.debug(logStr + "orderNumber=" + orderNumber);

		ReplenishOrder order = replenishServie.read(orderNumber);
		if (order == null) {
			String msg = "Replenish order does not exist. number=" + orderNumber;
			log.error(logStr + msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		replenishBusiness.removeOrder(order);
	}

}
