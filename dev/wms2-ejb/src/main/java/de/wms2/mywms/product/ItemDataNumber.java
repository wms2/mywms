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
package de.wms2.mywms.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

/**
 * This class replaces myWMS:ItemDataNumber
 * <p>
 * Add additional identifiers to an ItemData
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "number", "itemData_id" }) })
public class ItemDataNumber extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * An additional code of the product
	 */
	@Column(nullable = false)
	private String number = null;

	/**
	 * The name of the manufacturer
	 */
	private String manufacturerName = null;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ItemData itemData;

	/**
	 * An index to sort different entities in a dialog.<br>
	 * Not every dialog supports this value.
	 */
	@Column(nullable = false)
	private int index = 0;

	/**
	 * The packaging unit linked to the code.<br>
	 * If not set the base unit will be used.
	 */
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private PackagingUnit packagingUnit;

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		if (itemData != null && !itemData.getClient().equals(getClient())) {
			setClient(itemData.getClient());
		}
	}

	@PrePersist
	public void prePersist() {
		super.prePersist();
		if (itemData != null && !itemData.getClient().equals(getClient())) {
			setClient(itemData.getClient());
		}
	}

	@Override
	public String toString() {
		return "" + (itemData == null ? "/ " : itemData.getNumber() + " / ") + number;
	}

	@Override
	public String toUniqueString() {
		if (number != null) {
			return number;
		}
		return toString();
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public PackagingUnit getPackagingUnit() {
		return packagingUnit;
	}

	public void setPackagingUnit(PackagingUnit packagingUnit) {
		this.packagingUnit = packagingUnit;
	}

}
