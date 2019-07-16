/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingUnitLoad;


public class ManageOrderServiceBean implements ManageOrderService {
	
	public void onPickingOrderStateChange(PickingOrder pickingOrder, int stateOld) throws FacadeException {
	}
	public void onPickingOrderPrioChange(PickingOrder pickingOrder, int prioOld) throws FacadeException {
	}

	public void onPickingPositionStateChange(PickingOrderLine pick, int stateOld) throws FacadeException{
	}
	
	public void onPickingUnitLoadStateChange(PickingUnitLoad unitLoad, int stateOld) throws FacadeException {
	}
	
	public void onDeliveryOrderStateChange(DeliveryOrder customerOrder, int stateOld) throws FacadeException {
	}

	public void onDeliveryOrderLineStateChange(DeliveryOrderLine customerOrderPosition, int stateOld) throws FacadeException {
	}
	
	public void onGoodsOutOrderStateChange(LOSGoodsOutRequest goodsOutOrder, LOSGoodsOutRequestState stateOld) throws FacadeException {
	}
	public void onGoodsOutPositionStateChange(LOSGoodsOutRequestPosition goodsOutPosition, LOSGoodsOutRequestPositionState stateOld) throws FacadeException {
	}

	
	public boolean isPickingOrderReleasable(PickingOrder pickingOrder) throws FacadeException {
		return true;
	}
	
	public void onReplenishStateChange(LOSReplenishOrder replenishOrder, int stateOld) throws FacadeException {
		
	}
	

}
