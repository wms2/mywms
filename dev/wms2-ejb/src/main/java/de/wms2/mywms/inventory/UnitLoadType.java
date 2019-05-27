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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * This class replaces myWMS:UnitLoadType
 * <p>
 * The type of a UnitLoad
 */
@Entity
@Table
public class UnitLoadType extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(unique = true, nullable = false)
	private String name = null;

	@Column(nullable = true, precision = 15, scale = 2)
	private BigDecimal height;

	@Column(nullable = true, precision = 15, scale = 2)
	private BigDecimal width;

	@Column(nullable = true, precision = 15, scale = 2)
	private BigDecimal depth;

	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal weight;

	@Column(nullable = true, precision = 19, scale = 6)
	private BigDecimal volume;

	/**
	 * The max weight that such a unit load can handle
	 */
	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal liftingCapacity;

	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		return toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getLiftingCapacity() {
		return liftingCapacity;
	}

	public void setLiftingCapacity(BigDecimal liftingCapacity) {
		this.liftingCapacity = liftingCapacity;
	}
}
