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
package de.wms2.mywms.product;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * The packaging unit.
 * <p>
 * Packaging units are related to products.
 * 
 * @author krane
 * 
 */
@Entity
@Table
public class PackagingUnit extends BasicEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PackagingUnit.class.getName());

	/**
	 * The name of the packaging unit.
	 * <p>
	 * This field is required and has to be unique within one product.
	 */
	private String name;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private ItemData itemData;

	/**
	 * The amount of base units contained in the packaging
	 */
	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal amount = BigDecimal.ONE;

	@Column(precision = 15, scale = 2)
	private BigDecimal height;

	@Column(precision = 15, scale = 2)
	private BigDecimal width;

	@Column(precision = 15, scale = 2)
	private BigDecimal depth;

	/**
	 * The packingLevel of the outer unit. e.g. 1 = Carton 2 = Layer 3 = Pallet
	 */
	@Column(nullable = false)
	private int packingLevel = 0;

	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (name != null) {
			return name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	public int getPackingLevel() {
		return packingLevel;
	}

	public void setPackingLevel(int packingLevel) {
		this.packingLevel = packingLevel;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
