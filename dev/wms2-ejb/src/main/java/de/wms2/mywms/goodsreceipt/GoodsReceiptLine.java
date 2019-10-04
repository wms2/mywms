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
package de.wms2.mywms.goodsreceipt;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicEntity;
import org.mywms.model.User;

import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.util.Wms2Constants;

/**
 * A line of a goods receipt
 * <p>
 * This class is based on myWMS-LOS:LOSGoodsReceiptPosition
 * 
 * @see GoodsReceipt
 * @author krane
 */
@Entity
@Table
public class GoodsReceiptLine extends BasicEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GoodsReceiptLine.class.getName());

	/**
	 * The parent process
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private GoodsReceipt goodsReceipt;

	/**
	 * Unique number of the process
	 */
	@Column(unique = true, nullable = false, updatable = false)
	private String lineNumber;

	/**
	 * Current state of the process
	 */
	@Column(nullable = false)
	private int state = OrderState.CREATED;

	/**
	 * The user who handles the process
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User operator;

	/**
	 * The received itemData
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ItemData itemData;

	/**
	 * The received lot
	 */
	private String lotNumber;

	/**
	 * The received best before date
	 */
	@Temporal(TemporalType.DATE)
	private Date bestBefore;

	/**
	 * The received serial number
	 */
	private String serialNumber;

	/**
	 * The received amount
	 */
	@Column(precision = 17, scale = 4)
	private BigDecimal amount;

	/**
	 * The label of the unit load
	 */
	private String unitLoadLabel;

	/**
	 * Optional. Only set if material is locked.
	 */
	private int lockType;

	/**
	 * The reason of the lock
	 */
	@Column(length = Wms2Constants.FIELDSIZE_NOTE)
	private String lockNote;

	/**
	 * The id of the generated stock unit.
	 * <p>
	 * No reference because transaction data will change quickly.
	 */
	private Long stockUnitId;

	/**
	 * Optional. The referenced advice line.
	 */
	@ManyToOne(optional = true)
	private AdviceLine adviceLine;

	/**
	 * Optional. Set if a special strategy should be used for the first storage
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageStrategy storageStrategy;

	@Override
	public String toString() {
		if (lineNumber != null) {
			return lineNumber;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (lineNumber != null) {
			return lineNumber;
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

	public GoodsReceipt getGoodsReceipt() {
		return goodsReceipt;
	}

	public void setGoodsReceipt(GoodsReceipt goodsReceipt) {
		this.goodsReceipt = goodsReceipt;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public int getLockType() {
		return lockType;
	}

	public void setLockType(int lockType) {
		this.lockType = lockType;
	}

	public String getLockNote() {
		return lockNote;
	}

	public void setLockNote(String lockNote) {
		this.lockNote = lockNote;
	}

	public Long getStockUnitId() {
		return stockUnitId;
	}

	public void setStockUnitId(Long stockUnitId) {
		this.stockUnitId = stockUnitId;
	}

	public AdviceLine getAdviceLine() {
		return adviceLine;
	}

	public void setAdviceLine(AdviceLine adviceLine) {
		this.adviceLine = adviceLine;
	}

	public StorageStrategy getStorageStrategy() {
		return storageStrategy;
	}

	public void setStorageStrategy(StorageStrategy storageStrategy) {
		this.storageStrategy = storageStrategy;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
