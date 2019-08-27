/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.wms2.mywms.picking.PickingOrder;


public class ManageOrderServiceBean implements ManageOrderService {
	
	public void onPickingOrderPrioChange(PickingOrder pickingOrder, int prioOld) throws FacadeException {
	}

	public boolean isPickingOrderReleasable(PickingOrder pickingOrder) throws FacadeException {
		return true;
	}
	
	public void onReplenishStateChange(LOSReplenishOrder replenishOrder, int stateOld) throws FacadeException {
		
	}
	

}
