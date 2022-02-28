/* 
Copyright 2019-2022 Matthias Krane
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
package de.wms2.mywms.transport;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadTransferLocationEvent;
import de.wms2.mywms.inventory.UnitLoadTrashEvent;

/**
 * @author krane
 *
 */
public class TransportEventObserver {

	@Inject
	private TransportOrderEntityService relocateService;
	@Inject
	private TransportBusiness transportBusiness;

	public void listen(@Observes UnitLoadTrashEvent event) throws BusinessException {
		UnitLoad unitLoad = event.getUnitLoad();
		if (unitLoad == null) {
			return;
		}

		List<TransportOrder> transportOrders = relocateService.readOpen(unitLoad);
		for (TransportOrder transportOrder : transportOrders) {
			transportBusiness.removeUnitLoadReference(transportOrder);
		}
	}

	public void listen(@Observes UnitLoadTransferLocationEvent event) throws BusinessException {
		UnitLoad unitLoad = event.getUnitLoad();
		if (unitLoad == null) {
			return;
		}
		List<TransportOrder> transportOrders = relocateService.readOpen(unitLoad);
		for (TransportOrder transportOrder : transportOrders) {
			transportOrder.setSourceLocation(unitLoad.getStorageLocation());
		}
	}

}
