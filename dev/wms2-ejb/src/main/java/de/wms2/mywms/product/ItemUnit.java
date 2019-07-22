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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BasicEntity;
import org.mywms.model.ItemUnitType;

/**
 * Some units of measure used in a warehouse.
 */
@Entity
@Table
public class ItemUnit extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * Type of the unit
	 */
	@Enumerated(EnumType.STRING)
	private ItemUnitType unitType;

	/**
	 * @deprecated replaced by PackagingUnit
	 */
	@ManyToOne(optional = true)
	@Deprecated
	private ItemUnit baseUnit = null;

	/**
	 * @deprecated replaced by PackagingUnit
	 */
	@Column(nullable = false)
	@Deprecated
	private int baseFactor = 1;

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

	/**
	 * @deprecated replaced by getName()
	 */
	@Transient
	@Deprecated
	public String getUnitName() {
		return name;
	}

	/**
	 * @deprecated replaced by setName()
	 */
	@Transient
	@Deprecated
	public void setUnitName(String name) {
		this.name = name;
	}

	public ItemUnit getBaseUnit() {
		return baseUnit;
	}

	public void setBaseUnit(ItemUnit baseUnit) {
		this.baseUnit = baseUnit;
	}

	public int getBaseFactor() {
		return baseFactor;
	}

	public void setBaseFactor(int factor) {
		this.baseFactor = factor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemUnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(ItemUnitType unitType) {
		this.unitType = unitType;
	}
}
