/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.User;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishBusiness {

	public LOSReplenishOrder finishOrder(LOSReplenishOrder order) throws FacadeException;
	public void removeOrder(LOSReplenishOrder order) throws FacadeException;
	public void startOrder(LOSReplenishOrder order, User user) throws FacadeException;
	public void resetOrder(LOSReplenishOrder order) throws FacadeException;
    public LOSReplenishOrder confirmOrder(LOSReplenishOrder order, StockUnit sourceStock, StorageLocation destinationLocation, BigDecimal amount) throws FacadeException;

}
