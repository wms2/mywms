/* 
Copyright 2019 Matthias Krane
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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;

/**
 * An order to relocate unit loads
 * 
 * @author krane
 *
 */
@Entity
@Table
public class TransportOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * Unique number of the process
	 */
	@Column(nullable = false, unique = true, updatable = false)
	private String orderNumber;

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
	 * The user who handles the process
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User operator;

	/**
	 * Logical type of the order
	 */
	private int orderType = TransportOrderType.UNDEFINED;

	/**
	 * Optional identifier
	 */
	private String externalNumber;

	/**
	 * Timestamp of start processing
	 */
	private Date started;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	/**
	 * The unit load to transport.
	 * <p>
	 * After processing, the reference to the unit load can be removed.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private UnitLoad unitLoad;

	/**
	 * The label of the unit load to store.
	 * <p>
	 * After processing, the reference to the unit load can be removed.
	 */
	private String unitLoadLabel;

	/**
	 * The source storage location
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation sourceLocation;

	/**
	 * The planned destination storage location of the transport.
	 * <p>
	 * The really confirmed location is stored in the confirmedDetination field.
	 * Maybe different.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation destinationLocation;

	/**
	 * Transports may have several steps. The chain is connected by the successor
	 * attribute.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private TransportOrder successor;

	/**
	 * The confirmed destination storage location
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation confirmedDestination;

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
		setRedundantValues();
	}

	@PrePersist
	public void prePersist() {
		super.prePersist();
		setRedundantValues();
	}

	@Transient
	private void setRedundantValues() {
		if (unitLoad != null) {
			if (StringUtils.isBlank(unitLoadLabel)) {
				unitLoadLabel = unitLoad.getLabelId();
			}
			if (sourceLocation == null) {
				sourceLocation = unitLoad.getStorageLocation();
			}
		}
		if (finished == null && state >= OrderState.FINISHED) {
			finished = new Date();
		}
		if (started == null && state >= OrderState.STARTED) {
			started = new Date();
		}
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
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

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
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

	public UnitLoad getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public StorageLocation getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(StorageLocation sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public StorageLocation getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(StorageLocation destinationLocation) {
		this.destinationLocation = destinationLocation;
	}

	public TransportOrder getSuccessor() {
		return successor;
	}

	public void setSuccessor(TransportOrder successor) {
		this.successor = successor;
	}

	public StorageLocation getConfirmedDestination() {
		return confirmedDestination;
	}

	public void setConfirmedDestination(StorageLocation confirmedDestination) {
		this.confirmedDestination = confirmedDestination;
	}

}
