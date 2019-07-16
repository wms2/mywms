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
package de.wms2.mywms.picking;

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

import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.util.Wms2Constants;

/**
 * A position of a picking order
 * <p>
 * This class is based on myWMS-LOS:LOSPickingPosition
 *
 * @see PickingOrder
 * @author krane
 */
@Entity
@Table
public class PickingOrderLine extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PickingOrderLine.class.getName());

	@ManyToOne(optional = true)
	private PickingOrder pickingOrder;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private DeliveryOrderLine deliveryOrderLine;

	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal amount = BigDecimal.ZERO;

	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal pickedAmount = BigDecimal.ZERO;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private StockUnit pickFromStockUnit;

	private String pickFromLocationName;

	private String pickFromUnitLoadLabel;

	@ManyToOne(optional = false)
	private ItemData itemData;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private PickingUnitLoad pickToUnitLoad;

	@Column(nullable = false)
	private int state = OrderState.UNDEFINED;

	@Column(nullable = false)
	private int pickingType = PickingType.DEFAULT;

	@ManyToOne(optional = false)
	private OrderStrategy orderStrategy;

	private String pickedLotNumber;

	/**
	 * A hint for picking operation
	 */
	@Column(length = Wms2Constants.FIELDSIZE_DESCRIPTION)
	private String pickingHint;

	/**
	 * Timestamp of finishing
	 */
	private Date finished;

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		if (state >= OrderState.PICKED && finished == null) {
			finished = new Date();
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

	public PickingOrder getPickingOrder() {
		return pickingOrder;
	}

	public void setPickingOrder(PickingOrder pickingOrder) {
		this.pickingOrder = pickingOrder;
	}

	public DeliveryOrderLine getDeliveryOrderLine() {
		return deliveryOrderLine;
	}

	public void setDeliveryOrderLine(DeliveryOrderLine deliveryOrderLine) {
		this.deliveryOrderLine = deliveryOrderLine;
	}

	public StockUnit getPickFromStockUnit() {
		return pickFromStockUnit;
	}

	public void setPickFromStockUnit(StockUnit pickFromStockUnit) {
		this.pickFromStockUnit = pickFromStockUnit;
	}

	public String getPickFromUnitLoadLabel() {
		return pickFromUnitLoadLabel;
	}

	public void setPickFromUnitLoadLabel(String pickFromUnitLoadLabel) {
		this.pickFromUnitLoadLabel = pickFromUnitLoadLabel;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public PickingUnitLoad getPickToUnitLoad() {
		return pickToUnitLoad;
	}

	public void setPickToUnitLoad(PickingUnitLoad pickToUnitLoad) {
		this.pickToUnitLoad = pickToUnitLoad;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getPickingType() {
		return pickingType;
	}

	public void setPickingType(int pickingType) {
		this.pickingType = pickingType;
	}

	public OrderStrategy getOrderStrategy() {
		return orderStrategy;
	}

	public void setOrderStrategy(OrderStrategy orderStrategy) {
		this.orderStrategy = orderStrategy;
	}

	public String getPickedLotNumber() {
		return pickedLotNumber;
	}

	public void setPickedLotNumber(String pickedLotNumber) {
		this.pickedLotNumber = pickedLotNumber;
	}

	public String getPickingHint() {
		return pickingHint;
	}

	public void setPickingHint(String pickingHint) {
		this.pickingHint = pickingHint;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setPickedAmount(BigDecimal pickedAmount) {
		this.pickedAmount = pickedAmount;
	}

	public String getPickFromLocationName() {
		return pickFromLocationName;
	}

	public void setPickFromLocationName(String pickFromLocationName) {
		this.pickFromLocationName = pickFromLocationName;
	}

}
