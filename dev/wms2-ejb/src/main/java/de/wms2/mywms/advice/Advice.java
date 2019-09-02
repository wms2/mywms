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
package de.wms2.mywms.advice;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.strategy.OrderState;

/**
 * Advised material for incoming processes
 * 
 * @author krane
 *
 */
@Entity
@Table
public class Advice extends BasicClientAssignedEntity {
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
	 * Timestamp of start processing
	 */
	private Date started;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	/**
	 * The material and amounts
	 */
	@OneToMany(mappedBy = "advice")
	@OrderBy("lineNumber ASC")
	private List<AdviceLine> lines;

	/**
	 * Name of the sender
	 */
	private String senderName;

	/**
	 * Name of the carrier
	 */
	private String carrierName;

	/**
	 * Date when the material is planned to be delivered
	 */
	@Temporal(TemporalType.DATE)
	private Date deliveryDate;

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

	public List<AdviceLine> getLines() {
		return lines;
	}

	public void setLines(List<AdviceLine> lines) {
		this.lines = lines;
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

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

}
