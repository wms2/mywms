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
package de.wms2.mywms.replenish;

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

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;

/**
 * The replenish order
 * 
 * @author krane
 *
 */
@Entity
@Table
public class ReplenishOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReplenishOrder.class.getName());

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
	 * The requested itemData
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ItemData itemData;

	/**
	 * Optional. The requested lot. If set, this lot has to be used.
	 */
	private String lotNumber;

	/**
	 * The requested amount.
	 * <p>
	 * For empty values the amount will be calculated and recorded at operation
	 * time.
	 */
	@Column(nullable = true, precision = 17, scale = 4)
	private BigDecimal amount = null;

	/**
	 * The confirmed amount.
	 */
	@Column(nullable = true, precision = 17, scale = 4)
	private BigDecimal confirmedAmount = null;

	/**
	 * The source location
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation sourceLocation;

	/**
	 * The id of the calculated source StockUnit
	 */
	private Long sourceStockUnitId;

	/**
	 * The destination location to refill
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation destinationLocation;

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

	public BigDecimal getAmount() {
		if (itemData != null && amount != null) {
			try {
				return amount.setScale(itemData.getScale());
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
				return confirmedAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. itemData=" + itemData + ", confirmedAmount="
						+ confirmedAmount + ", scale=" + itemData.getScale());
			}
		}
		return confirmedAmount;
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

	public StorageLocation getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(StorageLocation sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public Long getSourceStockUnitId() {
		return sourceStockUnitId;
	}

	public void setSourceStockUnitId(Long sourceStockUnitId) {
		this.sourceStockUnitId = sourceStockUnitId;
	}

	public StorageLocation getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(StorageLocation destinationLocation) {
		this.destinationLocation = destinationLocation;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setConfirmedAmount(BigDecimal confirmedAmount) {
		this.confirmedAmount = confirmedAmount;
	}

}
