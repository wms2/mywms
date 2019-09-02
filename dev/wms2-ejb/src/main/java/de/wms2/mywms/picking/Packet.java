/* 
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.strategy.OrderState;

/**
 * Add some more information for outgoing processes to a UnitLoad
 * <p>
 * This class is based on myWMS-LOS:LOSPickingUnitLoad
 * 
 * @author krane
 */
@Entity
@Table
public class Packet extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private UnitLoad unitLoad;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private PickingOrder pickingOrder;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private DeliveryOrder deliveryOrder;

	@Column(nullable = false)
	private int positionIndex;

	@Column(nullable = false)
	private int state = OrderState.UNDEFINED;

	@Override
	public String toString() {
		if (unitLoad != null) {
			return unitLoad.getLabelId();
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (unitLoad != null) {
			return unitLoad.getLabelId();
		}
		return super.toUniqueString();
	}

	public UnitLoad getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	public PickingOrder getPickingOrder() {
		return pickingOrder;
	}

	public void setPickingOrder(PickingOrder pickingOrder) {
		this.pickingOrder = pickingOrder;
	}

	public int getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

}
