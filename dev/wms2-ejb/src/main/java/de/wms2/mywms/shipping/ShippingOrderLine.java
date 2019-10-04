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
package de.wms2.mywms.shipping;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.strategy.OrderState;

/**
 * This class is based on myWMS-LOS:LOSGoodsOutRequestPosition
 * 
 * @author krane
 *
 */
@Entity
@Table
public class ShippingOrderLine extends BasicEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false)
	private ShippingOrder shippingOrder;

	@ManyToOne(optional = false)
	private Packet packet;

	@Column(nullable = false)
	private int state = OrderState.UNDEFINED;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		if (state >= OrderState.PICKED && finished == null) {
			finished = new Date();
		}
	}

	@Override
	public String toString() {
		if (packet != null) {
			return packet.toString();
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (packet != null) {
			return packet.toUniqueString();
		}
		return super.toString();
	}

	public ShippingOrder getShippingOrder() {
		return shippingOrder;
	}

	public void setShippingOrder(ShippingOrder shippingOrder) {
		this.shippingOrder = shippingOrder;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}
}
