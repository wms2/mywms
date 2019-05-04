/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import javax.ejb.Local;

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

/**
 * User exits of goods out processes.
 * 
 * @author krane
 *
 */
@Local
public interface ManageOrderService {
	
	/**
	 * User exit. Is called when the state of a picking order changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param pickingOrder
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onPickingOrderStateChange(LOSPickingOrder pickingOrder, int stateOld) throws FacadeException;

	/**
	 * User exit. Is called when the priority of a picking order changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param pickingOrder
	 * @param prioOld
	 * @throws FacadeException
	 */
	public void onPickingOrderPrioChange(LOSPickingOrder pickingOrder, int prioOld) throws FacadeException;

	/**
	 * User exit. Is called when the state of a picking position changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param pick
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onPickingPositionStateChange(LOSPickingPosition pick, int stateOld) throws FacadeException;
	
	/**
	 * User exit. Is called when the state of a picking unit load changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param unitLoad
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onPickingUnitLoadStateChange(LOSPickingUnitLoad unitLoad, int stateOld) throws FacadeException;
	
	/**
	 * User exit. Is called when the state of a customer order changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param customerOrder
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onCustomerOrderStateChange(LOSCustomerOrder customerOrder, int stateOld) throws FacadeException;

	/**
	 * User exit. Is called when the state of a customer order posiion changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param customerOrderPosition
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onCustomerOrderPositionStateChange(LOSCustomerOrderPosition customerOrderPosition, int stateOld) throws FacadeException;
	
	/**
	 * User exit. Is called when the state of a shipping order changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param goodsOutOrder
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onGoodsOutOrderStateChange(LOSGoodsOutRequest goodsOutOrder, LOSGoodsOutRequestState stateOld) throws FacadeException;
	
	/**
	 * User exit. Is called when the state of a shipping position changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param goodsOutPosition
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onGoodsOutPositionStateChange(LOSGoodsOutRequestPosition goodsOutPosition, LOSGoodsOutRequestPositionState stateOld) throws FacadeException;

	/**
	 * User exit. Is called before release of a picking order to process.<br>
	 *  
	 * @param pickingOrder
	 * @return
	 * @throws FacadeException
	 */
	public boolean isPickingOrderReleasable(LOSPickingOrder pickingOrder) throws FacadeException;

	/**
	 * User exit. Is called when the state of a replenish order changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param replenishOrder
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onReplenishStateChange(LOSReplenishOrder replenishOrder, int stateOld) throws FacadeException;
	
	
}
