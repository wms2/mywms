/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.businessservice.LOSReplenishBusiness;
import de.linogistix.los.inventory.businessservice.LOSReplenishGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.inventory.service.LOSReplenishOrderService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishFacadeBean implements LOSReplenishFacade{
	private Logger log = Logger.getLogger(LOSReplenishFacadeBean.class);

	@EJB
	private LOSReplenishGenerator replenishGenerator;
	@EJB
	private LOSReplenishOrderService orderService;
	@EJB
	private LOSReplenishBusiness orderBusiness;

	public void refillFixedLocations() throws FacadeException {
		String logStr = "refillFixedLocations ";
		log.debug(logStr);
		
		replenishGenerator.refillFixedLocations();
	}

	public void finishOrder(String orderNumber) throws FacadeException {
		String logStr = "finishOrder ";
		log.debug(logStr+"orderNumber="+orderNumber);
		
		LOSReplenishOrder order = orderService.getByNumber(orderNumber);
		if( order == null ) {
			String msg = "Replenish order does not exist. number="+orderNumber;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		orderBusiness.finishOrder(order);
	}

	public void removeOrder(String orderNumber) throws FacadeException {
		String logStr = "removeOrder ";
		log.debug(logStr+"orderNumber="+orderNumber);
		
		LOSReplenishOrder order = orderService.getByNumber(orderNumber);
		if( order == null ) {
			String msg = "Replenish order does not exist. number="+orderNumber;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		orderBusiness.removeOrder(order);
	}

}
