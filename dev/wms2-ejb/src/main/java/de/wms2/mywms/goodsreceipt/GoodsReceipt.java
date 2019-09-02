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
package de.wms2.mywms.goodsreceipt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;

/**
 * The incoming processes
 * 
 * @author krane
 *
 */
@Entity
@Table
public class GoodsReceipt extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * Unique number of the process
	 */
	@Column(unique = true, nullable = false, updatable = false)
	private String orderNumber;

	/**
	 * An optional number to give an association to other systems
	 */
	private String externalNumber;

	/**
	 * Current state of the process
	 */
	@Column(nullable = false)
	private int state = OrderState.CREATED;

	/**
	 * The priority of the process
	 */
	@Column(nullable = false)
	private int prio = OrderPrio.NORMAL;

	/**
	 * Timestamp of start processing
	 */
	private Date started;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	/**
	 * The user who handles the process
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User operator;

	/**
	 * The processed material and amounts
	 */
	@OneToMany(mappedBy = "goodsReceipt")
	@OrderBy("lineNumber ASC")
	private List<GoodsReceiptLine> lines = new ArrayList<GoodsReceiptLine>();

	/**
	 * The type of the receipt. Normal, return, ...
	 */
	@Column(nullable = false)
	private int orderType = GoodsReceiptType.NORMAL;

	/**
	 * If true, only adviced material will be handled. Otherwise no advice is
	 * considered.
	 */
	@Column(nullable = false)
	private boolean useAdvice = true;

	/**
	 * The name of the sender
	 */
	private String senderName;

	/**
	 * The name of the carrier
	 */
	private String carrierName;

	/**
	 * The deliverynote number
	 */
	private String deliveryNoteNumber;

	@Temporal(TemporalType.DATE)
	private Date receiptDate;

	/**
	 * The advice lines that should be delivered with the process
	 */
	@ManyToMany
	@OrderBy("lineNumber ASC")
	private List<AdviceLine> adviceLines = new ArrayList<AdviceLine>();

	/**
	 * The storage location, where the received material is created
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation storageLocation;

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

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public List<GoodsReceiptLine> getLines() {
		return lines;
	}

	public void setLines(List<GoodsReceiptLine> lines) {
		this.lines = lines;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public boolean isUseAdvice() {
		return useAdvice;
	}

	public void setUseAdvice(boolean useAdvice) {
		this.useAdvice = useAdvice;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getDeliveryNoteNumber() {
		return deliveryNoteNumber;
	}

	public void setDeliveryNoteNumber(String deliveryNoteNumber) {
		this.deliveryNoteNumber = deliveryNoteNumber;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public List<AdviceLine> getAdviceLines() {
		return adviceLines;
	}

	public void setAdviceLines(List<AdviceLine> adviceLines) {
		this.adviceLines = adviceLines;
	}

	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}

}
