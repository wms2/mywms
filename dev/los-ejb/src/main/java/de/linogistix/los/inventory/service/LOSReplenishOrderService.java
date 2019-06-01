/*
 * Copyright (c) 2009 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishOrderService extends BasicService<LOSReplenishOrder> {

	public LOSReplenishOrder getByNumber(String number);
	
	public List<LOSReplenishOrder> getActive(ItemData item, Lot lot, StorageLocation requestedLocation, String requestedRack);
	
}
