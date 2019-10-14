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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.strategy.Zone;
import de.wms2.mywms.util.Wms2Constants;

/**
 * This class replaces myWMS:ItemData
 * <p>
 * Product master data
 */
@Entity
public class ItemData extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * The number of the product.
	 * <p>
	 * This field is required and has to be unique.
	 */
	@Column(unique = true, nullable = false)
	private String number = null;

	@Column(nullable = false)
	private int state = ItemDataState.ACTIVE;

	/**
	 * The name of the entity.
	 * <p>
	 * An optional short description.
	 */
	@Column(nullable = false)
	private String name = "";

	@Column(length = Wms2Constants.FIELDSIZE_DESCRIPTION)
	private String description;

	/**
	 * A default value for the unit load type when generating new stocks of the
	 * product.
	 * <p>
	 * The way how this is handled is depending on the used processes and
	 * applications.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private UnitLoadType defaultUnitLoadType;

	/**
	 * The zone assigned to the product
	 */
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private Zone zone = null;

	/**
	 * Stocks of the product must have a lot assignment
	 */
	@Column(nullable = false)
	private boolean lotMandatory = false;

	/**
	 * Minimum required days before best before ends.
	 * <p>
	 * The way how this is handled is depending on the used processes and
	 * applications.
	 */
	private Integer shelflife;

	@Column(nullable = false)
	@Deprecated
	private boolean adviceMandatory = false;

	/**
	 * Method to handle serial numbers
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SerialNoRecordType serialNoRecordType = SerialNoRecordType.NO_RECORD;

	/**
	 * The number of decimals in the amounts of the product
	 */
	@Column(nullable = false)
	private int scale = 0;

	/**
	 * The height of one product
	 */
	@Column(precision = 15, scale = 2)
	private BigDecimal height;

	/**
	 * The width of one product
	 */
	@Column(precision = 15, scale = 2)
	private BigDecimal width;

	/**
	 * The depth of one product
	 */
	@Column(precision = 15, scale = 2)
	private BigDecimal depth;

	/**
	 * The weight of one product
	 */
	@Column(precision = 16, scale = 3)
	private BigDecimal weight;

	/**
	 * The volume of one product
	 */
	@Column(precision = 19, scale = 6)
	private BigDecimal volume;

	/**
	 * Default value for the storage strategy.
	 * <p>
	 * The way how this is handled is depending on the used processes and
	 * applications.
	 */
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private StorageStrategy defaultStorageStrategy;

	/**
	 * The base unit of the product.<br>
	 * All amounts are stored in this unit.<br>
	 * Additional units like packaging units are calculated values based on this
	 * unit.
	 */
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private ItemUnit itemUnit;

	private String tradeGroup;

	/**
	 * Default value for a packaging.<br>
	 * The way how this is handled is depending on the used processes and
	 * applications.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private PackagingUnit defaultPackagingUnit;

	@Override
	public String toString() {
		if (number != null) {
			return number;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (number != null) {
			return number;
		}
		return super.toUniqueString();
	}

	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
		if (getAdditionalContent() != null && getAdditionalContent().length() > 255) {
			setAdditionalContent(getAdditionalContent().substring(0, 255));
		}
	}

	@PrePersist
	public void prePersist() {
		super.prePersist();
		if (getAdditionalContent() != null && getAdditionalContent().length() > 255) {
			setAdditionalContent(getAdditionalContent().substring(0, 255));
		}
	}

	/**
	 * @deprecated replaced by attribute shelflife
	 */
	@Deprecated
	@Transient
	public int getResidualTermOfUsageGI() {
		if (shelflife == null) {
			return -1;
		}
		return shelflife.intValue();
	}

	/**
	 * @deprecated replaced by attribute shelflife
	 */
	@Deprecated
	@Transient
	public void setResidualTermOfUsageGI(int residualTermOfUsageGI) {
		shelflife = residualTermOfUsageGI;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UnitLoadType getDefaultUnitLoadType() {
		return defaultUnitLoadType;
	}

	public void setDefaultUnitLoadType(UnitLoadType defaultUnitLoadType) {
		this.defaultUnitLoadType = defaultUnitLoadType;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public boolean isLotMandatory() {
		return lotMandatory;
	}

	public void setLotMandatory(boolean lotMandatory) {
		this.lotMandatory = lotMandatory;
	}

	public Integer getShelflife() {
		return shelflife;
	}

	public void setShelflife(Integer shelflife) {
		this.shelflife = shelflife;
	}

	public boolean isAdviceMandatory() {
		return adviceMandatory;
	}

	public void setAdviceMandatory(boolean adviceMandatory) {
		this.adviceMandatory = adviceMandatory;
	}

	public SerialNoRecordType getSerialNoRecordType() {
		return serialNoRecordType;
	}

	public void setSerialNoRecordType(SerialNoRecordType serialNoRecordType) {
		this.serialNoRecordType = serialNoRecordType;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
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

	public StorageStrategy getDefaultStorageStrategy() {
		return defaultStorageStrategy;
	}

	public void setDefaultStorageStrategy(StorageStrategy defaultStorageStrategy) {
		this.defaultStorageStrategy = defaultStorageStrategy;
	}

	public ItemUnit getItemUnit() {
		return itemUnit;
	}

	public void setItemUnit(ItemUnit itemUnit) {
		this.itemUnit = itemUnit;
	}

	public String getTradeGroup() {
		return tradeGroup;
	}

	public void setTradeGroup(String tradeGroup) {
		this.tradeGroup = tradeGroup;
	}

	public PackagingUnit getDefaultPackagingUnit() {
		return defaultPackagingUnit;
	}

	public void setDefaultPackagingUnit(PackagingUnit defaultPackagingUnit) {
		this.defaultPackagingUnit = defaultPackagingUnit;
	}
}
