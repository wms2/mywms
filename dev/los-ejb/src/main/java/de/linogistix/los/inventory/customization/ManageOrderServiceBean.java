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
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;


public class ManageOrderServiceBean implements ManageOrderService {
	
	public void onPickingOrderStateChange(PickingOrder pickingOrder, int stateOld) throws FacadeException {
	}
	public void onPickingOrderPrioChange(PickingOrder pickingOrder, int prioOld) throws FacadeException {
	}

	public void onPickingPositionStateChange(PickingOrderLine pick, int stateOld) throws FacadeException{
	}
	
	public void onPickingUnitLoadStateChange(Packet unitLoad, int stateOld) throws FacadeException {
	}
	
	public void onDeliveryOrderStateChange(DeliveryOrder customerOrder, int stateOld) throws FacadeException {
	}

	public void onDeliveryOrderLineStateChange(DeliveryOrderLine customerOrderPosition, int stateOld) throws FacadeException {
	}
	
	public boolean isPickingOrderReleasable(PickingOrder pickingOrder) throws FacadeException {
		return true;
	}
	
	public void onReplenishStateChange(LOSReplenishOrder replenishOrder, int stateOld) throws FacadeException {
		
	}
	

}
