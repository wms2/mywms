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
package de.wms2.mywms.strategy;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.LocationType;

/**
 * This class is based on myWMS-LOS:LOSTypeCapacityConstraint.
 * <p>
 * Rules to allocation a StorageLocation with a UnitLoad.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "locationtype_id", "unitloadtype_id" }) })
@NamedQueries({
		@NamedQuery(name = "LOSTypeCapacityConstraint.queryBySLTypeAndULType", query = "FROM TypeCapacityConstraint tcc WHERE tcc.locationType=:SLType AND tcc.unitLoadType=:ULType") })
public class TypeCapacityConstraint extends BasicEntity {
	private static final long serialVersionUID = 1L;

	public final static int ALLOCATE_UNIT_LOAD_TYPE = 1;
	public final static int ALLOCATE_PERCENTAGE = 2;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private LocationType locationType;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private UnitLoadType unitLoadType;

	@Column(nullable = false)
	private int allocationType = ALLOCATE_UNIT_LOAD_TYPE;

	/**
	 * Percentage of location allocation
	 */
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal allocation = BigDecimal.valueOf(100);

	/**
	 * Used by the storage strategy.
	 * <p>
	 * The strategy can use the capacity option as sorting criteria to find more or
	 * less matching locations.
	 */
	@Column(nullable = false)
	private int orderIndex = 0;

	@Override
	public String toString() {
		return "" + (locationType == null ? getId() : locationType.getName()) + " - "
				+ (unitLoadType == null ? "" : unitLoadType.getName());
	}

	@Override
	public String toUniqueString() {
		return unitLoadType == null ? "" + getId() : unitLoadType.getName();
	}

	/**
	 * @deprecated Use getLocationType
	 */
	@Transient
	@Deprecated
	public LocationType getStorageLocationType() {
		return locationType;
	}

	/**
	 * @deprecated Use setLocationType
	 */
	@Transient
	@Deprecated
	public void setStorageLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public UnitLoadType getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(UnitLoadType unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public int getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(int allocationType) {
		this.allocationType = allocationType;
	}

	public BigDecimal getAllocation() {
		return allocation;
	}

	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}
}
