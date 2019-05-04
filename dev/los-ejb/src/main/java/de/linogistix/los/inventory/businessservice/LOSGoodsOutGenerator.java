/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.Date;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * Generation of shipping orders
 * @author krane
 *
 */
@Local
public interface LOSGoodsOutGenerator {

	public LOSGoodsOutRequest createOrder( LOSCustomerOrder customerOrder ) throws FacadeException;
	
	public LOSGoodsOutRequest createOrder( Client client, LOSStorageLocation outLocation, String shipmentNumber, Date shippingDate, String courier, String additionalInfo) throws FacadeException;

	public LOSGoodsOutRequestPosition addPosition(LOSGoodsOutRequest out, LOSUnitLoad unitLoad) throws FacadeException;

	public void removePosition(LOSGoodsOutRequest out, LOSUnitLoad unitLoad) throws InventoryException;

}
