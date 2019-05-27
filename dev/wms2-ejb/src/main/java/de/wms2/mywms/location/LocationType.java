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
package de.wms2.mywms.location;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * This class replaces myWMS-LOS:LOSStorageLocationType
 */
@Entity
@Table
public class LocationType extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = true, precision = 15, scale = 2)
	private BigDecimal height;

	@Column(nullable = true, precision = 15, scale = 2)
	private BigDecimal width;

	@Column(nullable = true, precision = 15, scale = 2)
	private BigDecimal depth;

	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal liftingCapacity;

	@Column(nullable = true, precision = 19, scale = 6)
	private BigDecimal volume;

	@Column(nullable = false)
	private int handlingFlag = 0;

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

	public BigDecimal getLiftingCapacity() {
		return liftingCapacity;
	}

	public void setLiftingCapacity(BigDecimal liftingCapacity) {
		this.liftingCapacity = liftingCapacity;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public int getHandlingFlag() {
		return handlingFlag;
	}

	public void setHandlingFlag(int handlingFlag) {
		this.handlingFlag = handlingFlag;
	}

}
