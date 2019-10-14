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
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;

/**
 * This class is based on myWMS-LOS:LOSGoodsOutRequest
 * 
 * @author krane
 *
 */
@Entity
@Table
public class ShippingOrder extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String orderNumber;

	@OneToMany(mappedBy = "shippingOrder")
	@OrderBy("id")
	private List<ShippingOrderLine> lines;

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

	@Temporal(TemporalType.DATE)
	private Date shippingDate;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation destination;

	/**
	 * Optional information. Will be set if only one delivery order is associated
	 * with the shipping order.
	 * <p>
	 * In doubt ask every packet of the order. But even these may not be unique.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private DeliveryOrder deliveryOrder;

	/**
	 * The delivery address
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Address address;

	/**
	 * Name of the carrier
	 */
	private String carrierName;

	@Column(nullable = false)
	private int prio = OrderPrio.NORMAL;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User operator;

	/**
	 * Timestamp of start processing
	 */
	private Date started;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

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

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<ShippingOrderLine> getLines() {
		return lines;
	}

	public void setLines(List<ShippingOrderLine> lines) {
		this.lines = lines;
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

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public StorageLocation getDestination() {
		return destination;
	}

	public void setDestination(StorageLocation destination) {
		this.destination = destination;
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
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
