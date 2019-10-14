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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.util.Wms2Constants;

/**
 * A position of a customer order
 * <p>
 * This class is based on myWMS-LOS:LOSCustomerOrderPosition
 * 
 * @see DeliveryOrder
 * @author krane
 */
@Entity
@Table
public class DeliveryOrderLine extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DeliveryOrderLine.class.getName());

	/**
	 * Unique number of the process
	 */
	@Column(unique = true)
	private String lineNumber;

	/**
	 * An optional number to give an association to other systems
	 */
	private String externalNumber;

	/**
	 * An optional id to give an association to other systems
	 */
	private String externalId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private DeliveryOrder deliveryOrder;

	/**
	 * The ordered itemData
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ItemData itemData;

	/**
	 * The ordered amount
	 */
	@Column(precision = 17, scale = 4, nullable = false)
	private BigDecimal amount = BigDecimal.ZERO;

	/**
	 * The ordered lot
	 */
	@ManyToOne(optional = true)
	private Lot lot;

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
	 * A hint for shipping operation
	 */
	@Column(length = Wms2Constants.FIELDSIZE_DESCRIPTION)
	private String shippingHint;

	/**
	 * The price of a base unit
	 */
	private BigDecimal unitPrice;

	/**
	 * Timestamp of start processing
	 */
	private Date started;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	/**
	 * Current state of the process
	 */
	@Column(nullable = false)
	private int state = OrderState.UNDEFINED;

	/**
	 * The picked amount
	 */
	@Column(precision = 17, scale = 4, nullable = false)
	private BigDecimal pickedAmount = BigDecimal.ZERO;

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
		return super.toUniqueString();
	}

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		if (state >= OrderState.PICKED && finished == null) {
			finished = new Date();
		}
		if (state >= OrderState.STARTED && started == null) {
			started = new Date();
		}
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

	public BigDecimal getPickedAmount() {
		if (itemData != null && pickedAmount != null) {
			try {
				return pickedAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. itemData=" + itemData + ", pickedAmount=" + pickedAmount
						+ ", scale=" + itemData.getScale());
			}
		}
		return pickedAmount;
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

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setPickedAmount(BigDecimal pickedAmount) {
		this.pickedAmount = pickedAmount;
	}

	public String getShippingHint() {
		return shippingHint;
	}

	public void setShippingHint(String shippingHint) {
		this.shippingHint = shippingHint;
	}

}
