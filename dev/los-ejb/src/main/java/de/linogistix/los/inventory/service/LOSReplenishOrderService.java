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

import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishOrderService extends BasicService<LOSReplenishOrder> {

	public LOSReplenishOrder getByNumber(String number);
	
	public List<LOSReplenishOrder> getActive(ItemData item, Lot lot, LOSStorageLocation requestedLocation, LOSRack requestedRack);
	
}
