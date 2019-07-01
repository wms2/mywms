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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.product.PackagingUnit;

/**
 * This class replaces myWMS:StockUnit
 */
@Entity
@Table
public class StockUnit extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StockUnit.class.getName());

	@ManyToOne(optional = false)
	private ItemData itemData;

	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal amount = BigDecimal.ZERO;

	@Column(precision = 17, scale = 4)
	private BigDecimal reservedAmount = BigDecimal.ZERO;

	@ManyToOne(optional = true)
	private Lot lot;

	private String serialNumber;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private PackagingUnit packagingUnit;

	@Column(nullable = false)
	private int state = StockState.ON_STOCK;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date strategyDate = new Date();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UnitLoad unitLoad;

	@Transient
	public String getName() {
		String name = "";
		if (itemData != null) {
			name += itemData.getNumber();
		}
		if (unitLoad != null) {
			StorageLocation location = unitLoad.getStorageLocation();
			if (location != null) {
				name += " / " + location.getName();
			}
		}
		if (amount != null) {
			name += " / " + getAmount().toPlainString();
		}
		if (itemData != null) {
			ItemUnit unit = itemData.getItemUnit();
			if (unit != null) {
				name += " " + unit;
			}
		}

		return name;
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

	public BigDecimal getReservedAmount() {
		if (itemData != null && reservedAmount != null) {
			try {
				return reservedAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. itemData=" + itemData + ", reservedAmount="
						+ reservedAmount + ", scale=" + itemData.getScale());
			}
		}
		return reservedAmount;
	}

	@Transient
	public ItemUnit getItemUnit() {
		if (itemData == null)
			return null;

		return itemData.getItemUnit();
	}

	@Transient
	public void addReservedAmount(BigDecimal amount) {
		this.reservedAmount = this.reservedAmount.add(amount);
	}

	@Transient
	public void releaseReservedAmount(BigDecimal amount) {
		this.reservedAmount = this.reservedAmount.subtract(amount);
	}

	@Transient
	public BigDecimal getAvailableAmount() {
		if (itemData != null) {
			try {
				return amount.subtract(reservedAmount).setScale(itemData.getScale());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Cannot set scale. itemData=" + itemData + ", reservedAmount="
						+ reservedAmount + ", scale=" + itemData.getScale());
			}
		}
		return amount.subtract(reservedAmount);
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public PackagingUnit getPackagingUnit() {
		return packagingUnit;
	}

	public void setPackagingUnit(PackagingUnit packagingUnit) {
		this.packagingUnit = packagingUnit;
	}

	public Date getStrategyDate() {
		return strategyDate;
	}

	public void setStrategyDate(Date strategyDate) {
		this.strategyDate = strategyDate;
	}

	public UnitLoad getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
