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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.location.StorageLocation;

/**
 * This class replaces myWMS:UnitLoad and myWMS-LOS:LOSUnitLoad
 * <p>
 * The unit load is a transport help like a pallet, a box, a bin or just a
 * wrapping.
 */
@Entity
@Table
public class UnitLoad extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String labelId = null;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UnitLoadType unitLoadType = null;

	/**
	 * The index is used by the StorageLocation to track the order of UnitLoads
	 * stored in it. Setting a new index is allowed by the StorageLocation only.
	 */
	@Column(nullable = false)
	private int index = -1;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private StorageLocation storageLocation;

	/**
	 * @deprecated Use a service to read the StockUnits of a UnitLoad
	 */
	@OneToMany(mappedBy = "unitLoad")
	@Deprecated
	private List<StockUnit> stockUnitList = new ArrayList<StockUnit>();

	@Temporal(TemporalType.TIMESTAMP)
	private Date stockTakingDate;

	/**
	 * Calculated weight in kg
	 */
	@Column(precision = 16, scale = 3)
	private BigDecimal weightCalculated;

	/**
	 * Measured weight in kg
	 */
	@Column(precision = 16, scale = 3)
	private BigDecimal weightMeasure;

	@Column(precision = 16, scale = 3)
	private BigDecimal weight;

	@Column(nullable = false)
	private boolean opened = false;

	private Long carrierUnitLoadId;

	@Column(nullable = false)
	private boolean isCarrier = false;

	@ManyToOne(optional = true)
	private UnitLoad carrierUnitLoad;

	@Column(nullable = false)
	private int state = StockState.ON_STOCK;

	@Override
	public String toString() {
		if (labelId != null) {
			return labelId;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (labelId != null) {
			return labelId;
		}
		return super.toUniqueString();
	}

	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}

	@PrePersist
	public void prePersist() {
		super.prePersist();
		weight = (weightMeasure == null ? weightCalculated : weightMeasure);
	}

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		weight = (weightMeasure == null ? weightCalculated : weightMeasure);
	}

	// This value is calculated in automatically.
	@SuppressWarnings("unused")
	private void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	@Transient
	@Deprecated
	public UnitLoadType getType() {
		return unitLoadType;
	}

	@Transient
	@Deprecated
	public void setType(UnitLoadType unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public UnitLoadType getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(UnitLoadType unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	public List<StockUnit> getStockUnitList() {
		return stockUnitList;
	}

	public void setStockUnitList(List<StockUnit> stockUnitList) {
		this.stockUnitList = stockUnitList;
	}

	public Date getStockTakingDate() {
		return stockTakingDate;
	}

	public void setStockTakingDate(Date stockTakingDate) {
		this.stockTakingDate = stockTakingDate;
	}

	public BigDecimal getWeightCalculated() {
		return weightCalculated;
	}

	public void setWeightCalculated(BigDecimal weightCalculated) {
		this.weightCalculated = weightCalculated;
	}

	public BigDecimal getWeightMeasure() {
		return weightMeasure;
	}

	public void setWeightMeasure(BigDecimal weightMeasure) {
		this.weightMeasure = weightMeasure;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public Long getCarrierUnitLoadId() {
		return carrierUnitLoadId;
	}

	public void setCarrierUnitLoadId(Long carrierUnitLoadId) {
		this.carrierUnitLoadId = carrierUnitLoadId;
	}

	public boolean isCarrier() {
		return isCarrier;
	}

	public void setCarrier(boolean isCarrier) {
		this.isCarrier = isCarrier;
	}

	public UnitLoad getCarrierUnitLoad() {
		return carrierUnitLoad;
	}

	public void setCarrierUnitLoad(UnitLoad carrierUnitLoad) {
		this.carrierUnitLoad = carrierUnitLoad;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
