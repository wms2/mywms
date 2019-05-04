/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.model.LOSReplenishOrder;


public class ManageOrderServiceBean implements ManageOrderService {
	
	public void onPickingOrderStateChange(LOSPickingOrder pickingOrder, int stateOld) throws FacadeException {
	}
	public void onPickingOrderPrioChange(LOSPickingOrder pickingOrder, int prioOld) throws FacadeException {
	}

	public void onPickingPositionStateChange(LOSPickingPosition pick, int stateOld) throws FacadeException{
	}
	
	public void onPickingUnitLoadStateChange(LOSPickingUnitLoad unitLoad, int stateOld) throws FacadeException {
	}
	
	public void onCustomerOrderStateChange(LOSCustomerOrder customerOrder, int stateOld) throws FacadeException {
	}

	public void onCustomerOrderPositionStateChange(LOSCustomerOrderPosition customerOrderPosition, int stateOld) throws FacadeException {
	}
	
	public void onGoodsOutOrderStateChange(LOSGoodsOutRequest goodsOutOrder, LOSGoodsOutRequestState stateOld) throws FacadeException {
	}
	public void onGoodsOutPositionStateChange(LOSGoodsOutRequestPosition goodsOutPosition, LOSGoodsOutRequestPositionState stateOld) throws FacadeException {
	}

	
	public boolean isPickingOrderReleasable(LOSPickingOrder pickingOrder) throws FacadeException {
		return true;
	}
	
	public void onReplenishStateChange(LOSReplenishOrder replenishOrder, int stateOld) throws FacadeException {
		
	}
	

}
