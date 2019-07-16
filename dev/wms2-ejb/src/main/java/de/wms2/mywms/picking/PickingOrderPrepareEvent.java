/* 
Copyright 2019 Matthias Krane

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
package de.wms2.mywms.picking;

import java.util.Collection;

import de.wms2.mywms.location.StorageLocation;

/**
 * This event fired before PickingLines are assigned to PickingOrders
 * 
 * @author krane
 *
 */
public class PickingOrderPrepareEvent {
	private String externalNumber;
	private Collection<PickingOrderLine> picks;
	private StorageLocation destinationLocation;
	private Integer prio;

	public PickingOrderPrepareEvent(String externalNumber, Collection<PickingOrderLine> picks,
			StorageLocation destinationLocation, Integer prio) {
		this.externalNumber = externalNumber;
		this.picks = picks;
		this.destinationLocation = destinationLocation;
		this.prio = prio;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public Collection<PickingOrderLine> getPicks() {
		return picks;
	}

	public StorageLocation getDestinationLocation() {
		return destinationLocation;
	}

	public Integer getPrio() {
		return prio;
	}

}
