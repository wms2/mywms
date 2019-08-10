/* 
Copyright 2019 Matthias Krane
info@krane.engineer

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 * The Class LocationReservation holds reservation objects. As long as the unit
 * load has not reached the location the reservation is stored. After putting
 * the unit load onto the location, the reservation object is deleted
 * 
 * @author krane
 */
@Entity
@Table
public class LocationReservation extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false)
	private StorageLocation storageLocation;

	@ManyToOne(optional = false)
	private UnitLoad unitLoad;

	/**
	 * The name of the class of the object which causes the reservation. Has no effect in operational processes. Just for info.
	 */
	private String reserverType;

	/**
	 * The ID value of the reserving object. It is used to query the entity by the EntityManager
	 */
	@Column(nullable = false)
	private Long reserverId;

	/**
	 * A number or name to display in navigations
	 */
	@Column(nullable = false)
	private String reserverName;

	/**
	 * The calculated allocation of the storage location
	 */
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal allocation = BigDecimal.ZERO;

	@Override
	public String toString() {
		return storageLocation + "/" + unitLoad;
	}

	public UnitLoad getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getReserverType() {
		return reserverType;
	}

	public void setReserverType(String reserverType) {
		this.reserverType = reserverType;
	}

	public Long getReserverId() {
		return reserverId;
	}

	public void setReserverId(Long reserverId) {
		this.reserverId = reserverId;
	}

	public String getReserverName() {
		return reserverName;
	}

	public void setReserverName(String reserverName) {
		this.reserverName = reserverName;
	}

	public BigDecimal getAllocation() {
		return allocation;
	}

	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

}
