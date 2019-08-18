/* 
Copyright 2019 Matthias Krane
info@krane.engineer

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.project;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.customization.ManageStockService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.exception.WrappedFacadeException;
import de.wms2.mywms.inventory.StockUnitChangeAmountEvent;
import de.wms2.mywms.picking.PickingOrderLineStateChangeEvent;

/**
 * Calls to the customization services are or will be replaced by CDI events.
 * <p>
 * This class dispatches all events which are send by basic services to the
 * corresponding custom-services. This is just to bring compatibility as long as
 * some services call the custom-services and other send events.
 * <p>
 * In future versions the customization services will be removed completely.
 * 
 * @author krane
 *
 */
public class CustomizationEventObserver {

	@EJB
	private ManageOrderService manageOrderService;
	@EJB
	private ManageStockService manageStockService;

	public void listen(@Observes PickingOrderLineStateChangeEvent event) throws BusinessException {
		if (event == null || event.getPickingOrderLine() == null) {
			return;
		}

		try {
			manageOrderService.onPickingPositionStateChange(event.getPickingOrderLine(), event.getOldState());
		} catch (FacadeException e) {
			throw new WrappedFacadeException(e);
		}
	}

	public void listen(@Observes StockUnitChangeAmountEvent event) throws BusinessException {
		if (event == null || event.getStock() == null) {
			return;
		}

		try {
			manageStockService.onStockAmountChange(event.getStock(), event.getOldAmount());
		} catch (FacadeException e) {
			throw new WrappedFacadeException(e);
		}
	}

}
