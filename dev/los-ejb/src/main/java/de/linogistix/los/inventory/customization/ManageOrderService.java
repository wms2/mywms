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
	 * User exit. Is called before release of a picking order to process.<br>
	 *  
	 * @param pickingOrder
	 * @return
	 * @throws FacadeException
	 */
	public boolean isPickingOrderReleasable(PickingOrder pickingOrder) throws FacadeException;

}
