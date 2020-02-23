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
package de.wms2.mywms.inventory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

/**
 * Journal to protocol changes of the stock units
 * 
 * @author krane
 *
 */
@Entity
@Table
public class InventoryJournal extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InventoryJournal.class.getName());

	@Column(updatable = false)
	private String fromUnitLoad;

	@Column(updatable = false)
	private String toUnitLoad;

	@Column(updatable = false)
	private String fromStorageLocation;

	@Column(updatable = false)
	private String toStorageLocation;

	@Column(updatable = false)
	private String activityCode;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User operator;

	@Column(nullable = false, updatable = false)
	private int recordType;

	@Column(updatable = false)
	private String productNumber;

	@Column(updatable = false)
	private String productName;

	@Column(updatable = false)
	private String unitName;

	@Column(nullable = false)
	private int scale = 0;

	@Column(updatable = false)
	private String lotNumber;

	@Column(updatable = false)
	private Date bestBefore;

	@Column(updatable = false, precision = 17, scale = 4)
	private BigDecimal amount;

	@Column(updatable = false, precision = 17, scale = 4)
	private BigDecimal stockUnitAmount;

	@Column(updatable = false)
	private String serialNumber;

	@Column(updatable = false)
	private String unitLoadType;

	@Override
	public String toString() {
		return "" + getId();
	}

	public BigDecimal getAmount() {
		if (amount != null) {
			try {
				return amount.setScale(scale);
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. amount=" + amount + ", scale=" + scale);
			}
		}
		return amount;
	}

	public BigDecimal getStockUnitAmount() {
		if (stockUnitAmount != null) {
			try {
				return stockUnitAmount.setScale(scale);
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. stockUnitAmount=" + stockUnitAmount + ", scale=" + scale);
			}
		}
		return stockUnitAmount;
	}

	public String getFromUnitLoad() {
		return fromUnitLoad;
	}

	public void setFromUnitLoad(String fromUnitLoad) {
		this.fromUnitLoad = fromUnitLoad;
	}

	public String getToUnitLoad() {
		return toUnitLoad;
	}

	public void setToUnitLoad(String toUnitLoad) {
		this.toUnitLoad = toUnitLoad;
	}

	public String getFromStorageLocation() {
		return fromStorageLocation;
	}

	public void setFromStorageLocation(String fromStorageLocation) {
		this.fromStorageLocation = fromStorageLocation;
	}

	public String getToStorageLocation() {
		return toStorageLocation;
	}

	public void setToStorageLocation(String toStorageLocation) {
		this.toStorageLocation = toStorageLocation;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Date getBestBefore() {
		return bestBefore;
	}

	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setStockUnitAmount(BigDecimal stockUnitAmount) {
		this.stockUnitAmount = stockUnitAmount;
	}
}
