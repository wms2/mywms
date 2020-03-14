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
package de.wms2.mywms.delivery;

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
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.util.Wms2Constants;

/**
 * Outgoing order
 * <p>
 * This class is based on myWMS-LOS:LOSCustomerOrder
 * 
 * @author krane
 */
@Entity
@Table
public class DeliveryOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * Unique number of the process
	 */
	@Column(unique = true)
	private String orderNumber;

	/**
	 * An optional number to give an association to other systems
	 */
	private String externalNumber;

	/**
	 * An optional id to give an association to other systems
	 */
	private String externalId;

	@OneToMany(mappedBy = "deliveryOrder")
	@OrderBy("lineNumber ASC")
	private List<DeliveryOrderLine> lines;

	/**
	 * The strategy to handle the order
	 */
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private OrderStrategy orderStrategy;

	/**
	 * Current state of the process
	 */
	@Column(nullable = false)
	private int state = OrderState.UNDEFINED;

	/**
	 * Date when the material is planned to be delivered
	 */
	@Temporal(TemporalType.DATE)
	private Date deliveryDate;

	/**
	 * The storage location where the material is put after picking. Can overwrite
	 * the destination location of the order strategy.
	 */
	@ManyToOne(optional = true)
	private StorageLocation destination;

	/**
	 * A URL to request documents for the order
	 */
	private String documentUrl;

	/**
	 * A URL to request labels for the order
	 */
	private String labelUrl;

	/**
	 * The name of the customer
	 */
	private String customerName;

	/**
	 * The delivery address
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Address address;

	/**
	 * Name of the carrier
	 */
	private String carrierName;

	/**
	 * Service of the carrier
	 */
	private String carrierService;

	/**
	 * Name of the sender
	 */
	private String senderName;

	/**
	 * The priority of the order
	 */
	@Column(nullable = false)
	private int prio = OrderPrio.NORMAL;

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
	 * An optional reservation for an operator
	 */
	@ManyToOne(optional = true)
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

	public List<DeliveryOrderLine> getLines() {
		return lines;
	}

	public void setLines(List<DeliveryOrderLine> lines) {
		this.lines = lines;
	}

	public OrderStrategy getOrderStrategy() {
		return orderStrategy;
	}

	public void setOrderStrategy(OrderStrategy orderStrategy) {
		this.orderStrategy = orderStrategy;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public StorageLocation getDestination() {
		return destination;
	}

	public void setDestination(StorageLocation destination) {
		this.destination = destination;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public String getLabelUrl() {
		return labelUrl;
	}

	public void setLabelUrl(String labelUrl) {
		this.labelUrl = labelUrl;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public String getCarrierService() {
		return carrierService;
	}

	public void setCarrierService(String carrierService) {
		this.carrierService = carrierService;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
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
