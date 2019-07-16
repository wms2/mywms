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
package de.wms2.mywms.strategy;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * Reservation of a location for a product
 * 
 * @author krane
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "itemdata_id", "storagelocation_id" }) })
public class FixAssignment extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private StorageLocation storageLocation;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private ItemData itemData;

	/**
	 * The minimum amount.
	 * <p>
	 * If the amount is less than the minimum, automatic generation of replenish
	 * orders starts.
	 * <p>
	 * To avoid automatic handling, set the minimum to zero.
	 */
	@Column(nullable = true, precision = 17, scale = 4)
	private BigDecimal minAmount;

	/**
	 * The maximum amount.
	 * <p>
	 * Used to calculate amounts for replenish operations.
	 * <p>
	 * The way how this is handled in detail is depending on the deployed processes
	 * and applications.
	 */
	@Column(nullable = true, precision = 17, scale = 4)
	private BigDecimal maxAmount;

	/**
	 * The maximum amount for a pick on this location.
	 * <p>
	 * Used to calculate picking orders. Picking orders may use different locations
	 * if this amount is reached.
	 * <p>
	 * The way how this is handled in detail is depending on the deployed processes
	 * and applications.
	 */
	@Column(nullable = true, precision = 17, scale = 4)
	private BigDecimal maxPickAmount;

	/**
	 * An index to sort different entities in a dialog.
	 * <p>
	 * Not every dialog supports this value.
	 */
	@Column(nullable = false)
	private int orderIndex = 0;

	@Override
	public String toString() {
		return "" + (storageLocation == null ? getId() : storageLocation.getName()) + " / "
				+ (itemData == null ? "" : itemData.getNumber());
	}

	public BigDecimal getMaxAmount() {
		if (itemData != null && maxAmount != null) {
			try {
				return this.maxAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
			}
		}
		return maxAmount;
	}

	public BigDecimal getMinAmount() {
		if (itemData != null && minAmount != null) {
			try {
				return this.minAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
			}
		}
		return minAmount;
	}

	public BigDecimal getMaxPickAmount() {
		if (itemData != null && maxPickAmount != null) {
			try {
				return this.maxPickAmount.setScale(itemData.getScale());
			} catch (Throwable t) {
			}
		}
		return maxPickAmount;
	}

	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public void setMaxPickAmount(BigDecimal maxPickAmount) {
		this.maxPickAmount = maxPickAmount;
	}
}
