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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.wms2.mywms.address.Address;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.util.Wms2Constants;

/**
 * 
 * The retrieval process.
 * <p>
 * The PickingOrder is mainly a collection of PickingOrderLines. Just for
 * operation the lines are accumulated into an order.
 * <p>
 * The root of a picking order and its lines is not really required. <br>
 * One is a DeliveryOrder. But this is not necessary. It can be any other
 * reason.
 * <p>
 * This class is based on myWMS-LOS:LOSPickingOrder
 * 
 * @author krane
 */
@Entity
@Table
public class PickingOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String orderNumber;

	/**
	 * An optional number to give an association to other systems
	 */
	private String externalNumber;

	/**
	 * An optional id to give an association to other systems
	 */
	private String externalId;

	@Column(nullable = false)
	private int state = OrderState.UNDEFINED;

	@Column(nullable = false)
	private int prio = OrderPrio.NORMAL;

	@ManyToOne(optional = true)
	private User operator;

	/**
	 * A hint for picking operation
	 */
	@Column(length = Wms2Constants.FIELDSIZE_DESCRIPTION)
	private String pickingHint;

	/**
	 * A hint for packing operation
	 */
	@Column(length = Wms2Constants.FIELDSIZE_DESCRIPTION)
	private String packingHint;

	/**
	 * Timestamp of start processing
	 */
	private Date started;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	@OneToMany(mappedBy = "pickingOrder")
	@OrderBy("id")
	private List<PickingOrderLine> lines;

	/**
	 * Optional. Do only set if all lines belong to a single delivery order
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private DeliveryOrder deliveryOrder;

	@OneToMany(mappedBy = "pickingOrder")
	@OrderBy("id")
	private List<Packet> packets;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation destination;

	/**
	 * Automatically generation of substitution picks.
	 */
	@Column(nullable = false)
	private boolean createFollowUpPicks = false;

	/**
	 * Optional use for pick-to-pack operations
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private UnitLoadType unitLoadType;

	@ManyToOne(optional = false)
	private OrderStrategy orderStrategy;

	/**
	 * Date when the material is planned to be delivered
	 */
	@Temporal(TemporalType.DATE)
	private Date deliveryDate;

	/**
	 * The delivery address
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Address address;

	/**
	 * Approximately the weight of the order.
	 * <p>
	 * Its calculated by product data. The weight of differences, unit loads,
	 * packagings are not considered.
	 */
	@Column(precision = 16, scale = 3)
	private BigDecimal weight;

	/**
	 * Approximately the volume of the order.
	 * <p>
	 * Its calculated by product data. The volume of differences, unit loads,
	 * packagings are not considered.
	 */
	@Column(precision = 19, scale = 6)
	private BigDecimal volume;

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		if (state >= OrderState.FINISHED && finished == null) {
			finished = new Date();
		}
		if (state >= OrderState.STARTED && started == null) {
			started = new Date();
		}
	}

	@Override
	public String toString() {
		if (orderNumber != null) {
			return orderNumber;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (orderNumber != null) {
			return orderNumber;
		}
		return super.toUniqueString();
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public String getPickingHint() {
		return pickingHint;
	}

	public void setPickingHint(String pickingHint) {
		this.pickingHint = pickingHint;
	}

	public String getPackingHint() {
		return packingHint;
	}

	public void setPackingHint(String packingHint) {
		this.packingHint = packingHint;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public List<PickingOrderLine> getLines() {
		return lines;
	}

	public void setLines(List<PickingOrderLine> lines) {
		this.lines = lines;
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public List<Packet> getPackets() {
		return packets;
	}

	public void setPackets(List<Packet> packets) {
		this.packets = packets;
	}

	public StorageLocation getDestination() {
		return destination;
	}

	public void setDestination(StorageLocation destination) {
		this.destination = destination;
	}

	public boolean isCreateFollowUpPicks() {
		return createFollowUpPicks;
	}

	public void setCreateFollowUpPicks(boolean createFollowUpPicks) {
		this.createFollowUpPicks = createFollowUpPicks;
	}

	public UnitLoadType getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(UnitLoadType unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public OrderStrategy getOrderStrategy() {
		return orderStrategy;
	}

	public void setOrderStrategy(OrderStrategy orderStrategy) {
		this.orderStrategy = orderStrategy;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

}
