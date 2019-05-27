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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.Zone;

/**
 * This class is based on myWMS:StorageLocation and myWMS-LOS:StorageLocation
 */
@Entity
@Table
@NamedQueries({
		@NamedQuery(name = "LOSStorageLocation.queryByName", query = "FROM StorageLocation sl WHERE sl.name=:name") })
public class StorageLocation extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * May be used as identifier for mobile application when identifying the
	 * location with a barcode scanner
	 */
	@Column(nullable = false)
	private String scanCode;

	/**
	 * May be used as identifier for automated systems
	 */
	private String plcCode;

	/**
	 * The type of the storage location
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private LocationType locationType;

	/**
	 * The role (logical grouping) of the storage location
	 */
	@ManyToOne(optional = false)
	private Area area;

	/**
	 * The cluster (Physical grouping) of the storage location
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private LocationCluster locationCluster;

	/**
	 * The last time the location has been counted
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date stockTakingDate;

	/**
	 * The zone of the storage location
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Zone zone = null;

	/**
	 * The calculated allocation of the storage location
	 */
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal allocation = BigDecimal.ZERO;

	@ManyToOne(optional = true)
	private TypeCapacityConstraint currentTypeCapacityConstraint;

	/**
	 * The rack or aisle of the storage location
	 */
	private String rack;

	/**
	 * The X position of the storage location
	 */
	@Column(nullable = false)
	private int XPos;

	/**
	 * The Y position of the storage location
	 */
	@Column(nullable = false)
	private int YPos;

	/**
	 * The Z position of the storage location
	 */
	@Column(nullable = false)
	private int ZPos;

	/**
	 * The place between two rack stands
	 */
	private String field;

	/**
	 * The position within one field.
	 * <p>
	 * Used to calculate storage location occupation when more than one storage
	 * location is occupied by one unit load.
	 */
	@Column(nullable = false)
	private int fieldIndex;

	@Column(nullable = false)
	private int allocationState = 0;

	/**
	 * Used to sort storage locations. E.g. generation of picking runs.
	 */
	@Column(nullable = false)
	private int orderIndex = 0;

	/**
	 * @deprecated Use a service to read the UnitLoads on a StorageLocation.
	 */
	@OneToMany(mappedBy = "storageLocation")
	@Deprecated
	private List<UnitLoad> unitLoads = new ArrayList<>();

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

	@PrePersist
	@PreUpdate
	public void preUpdate() {
		if (scanCode == null) {
			scanCode = name;
		}
	}

	/**
	 * @deprecated Use getLocationType
	 */
	@Transient
	@Deprecated
	public LocationType getType() {
		return locationType;
	}

	/**
	 * @deprecated Use setLocationType
	 */
	@Transient
	@Deprecated
	public void setType(LocationType locationType) {
		this.locationType = locationType;
	}

	/**
	 * @deprecated Use getLocationCluster
	 */
	@Transient
	@Deprecated
	public LocationCluster getCluster() {
		return locationCluster;
	}

	/**
	 * @deprecated Use setLocationCluster
	 */
	@Transient
	@Deprecated
	public void setCluster(LocationCluster locationCluster) {
		this.locationCluster = locationCluster;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScanCode() {
		return scanCode;
	}

	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}

	public String getPlcCode() {
		return plcCode;
	}

	public void setPlcCode(String plcCode) {
		this.plcCode = plcCode;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public LocationCluster getLocationCluster() {
		return locationCluster;
	}

	public void setLocationCluster(LocationCluster locationCluster) {
		this.locationCluster = locationCluster;
	}

	public Date getStockTakingDate() {
		return stockTakingDate;
	}

	public void setStockTakingDate(Date stockTakingDate) {
		this.stockTakingDate = stockTakingDate;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public BigDecimal getAllocation() {
		return allocation;
	}

	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

	public TypeCapacityConstraint getCurrentTypeCapacityConstraint() {
		return currentTypeCapacityConstraint;
	}

	public void setCurrentTypeCapacityConstraint(TypeCapacityConstraint currentTypeCapacityConstraint) {
		this.currentTypeCapacityConstraint = currentTypeCapacityConstraint;
	}

	public String getRack() {
		return rack;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public int getXPos() {
		return XPos;
	}

	public void setXPos(int xPos) {
		XPos = xPos;
	}

	public int getYPos() {
		return YPos;
	}

	public void setYPos(int yPos) {
		YPos = yPos;
	}

	public int getZPos() {
		return ZPos;
	}

	public void setZPos(int zPos) {
		ZPos = zPos;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}

	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

	public int getAllocationState() {
		return allocationState;
	}

	public void setAllocationState(int allocationState) {
		this.allocationState = allocationState;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public List<UnitLoad> getUnitLoads() {
		return unitLoads;
	}

	public void setUnitLoads(List<UnitLoad> unitLoads) {
		this.unitLoads = unitLoads;
	}
}
