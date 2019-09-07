/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import org.mywms.facade.FacadeException;

import de.wms2.mywms.picking.PickingOrder;

public class ManageOrderServiceBean implements ManageOrderService {

	public boolean isPickingOrderReleasable(PickingOrder pickingOrder) throws FacadeException {
		return true;
	}

}
