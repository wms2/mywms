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
package de.wms2.mywms.delivery;

/**
 * This event fired when a customer order changes its state.
 * 
 * @author krane
 *
 */
public class DeliveryOrderStateChangeEvent {
	private DeliveryOrder deliveryOrder;
	private int oldState;
	private int newState;

	public DeliveryOrderStateChangeEvent(DeliveryOrder deliveryOrder, int oldState, int newState) {
		this.deliveryOrder = deliveryOrder;
		this.oldState = oldState;
		this.newState = newState;
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public int getOldState() {
		return oldState;
	}

	public int getNewState() {
		return newState;
	}
}
