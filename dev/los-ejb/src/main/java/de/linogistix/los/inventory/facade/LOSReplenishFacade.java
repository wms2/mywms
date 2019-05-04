/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;


/**
 * @author krane
 *
 */
@Remote
public interface LOSReplenishFacade {

	public void refillFixedLocations() throws FacadeException;
	public void finishOrder(String orderNumber) throws FacadeException;
	public void removeOrder(String orderNumber) throws FacadeException;
}
