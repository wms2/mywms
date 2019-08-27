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

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.wms2.mywms.picking.PickingOrder;

/**
 * User exits of goods out processes.
 * 
 * @author krane
 *
 */
@Local
public interface ManageOrderService {
	
	/**
	 * User exit. Is called when the priority of a picking order changes.<br>
	 * This method is only called, when the change is done by business-logic. 
	 * The only change of the database field does not trigger this method.
	 * 
	 * @param pickingOrder
	 * @param prioOld
	 * @throws FacadeException
	 */
	public void onPickingOrderPrioChange(PickingOrder pickingOrder, int prioOld) throws FacadeException;

	/**
	 * User exit. Is called before release of a picking order to process.<br>
	 *  
	 * @param pickingOrder
	 * @return
	 * @throws FacadeException
	 */
	public boolean isPickingOrderReleasable(PickingOrder pickingOrder) throws FacadeException;

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
