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

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;

/**
 * A line of an advice
 * 
 * @see Advice
 * @author krane
 */
@Entity
@Table
public class AdviceLine extends BasicEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AdviceLine.class.getName());

	/**
	 * The parent process
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Advice advice;

	/**
	 * Unique number of the process
	 */
	@Column(unique = true, nullable = false)
	private String lineNumber;

	/**
	 * An optional number to give an association to other systems
	 */
	private String externalNumber;

	/**
	 * An optional id to give an association to other systems
	 */
	private String externalId;

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
	 * The advised itemData
	 */
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private ItemData itemData;

	/**
	 * The advised lot
	 */
	private String lotNumber;

	/**
	 * The advised amount
	 */
	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal amount = BigDecimal.ZERO;

	/**
	 * The confirmed amount
	 */
	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal confirmedAmount = BigDecimal.ZERO;

	@Override
	public String toString() {
		if (lineNumber != null) {
			return lineNumber;
		}
		return super.toString();
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

	public BigDecimal getAmount() {
		if (itemData != null && amount != null) {
			try {
				return this.amount.setScale(itemData.getScale());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. itemData=" + itemData + ", amount=" + amount + ", scale="
						+ itemData.getScale());
			}
		}
		return amount;
	}

	public BigDecimal getConfirmedAmount() {
		if (itemData != null && confirmedAmount != null) {
			try {
				return this.confirmedAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. itemData=" + itemData + ", confirmedAmount="
						+ confirmedAmount + ", scale=" + itemData.getScale());
			}
		}
		return confirmedAmount;
	}

	public Advice getAdvice() {
		return advice;
	}

	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
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

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setConfirmedAmount(BigDecimal confirmedAmount) {
		this.confirmedAmount = confirmedAmount;
	}

}
