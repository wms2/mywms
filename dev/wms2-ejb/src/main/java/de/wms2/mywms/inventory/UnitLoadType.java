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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.util.ListUtils;

/**
 * The type of a UnitLoad
 * <p>
 * This class replaces myWMS:UnitLoadType
 */
@Entity
@Table
public class UnitLoadType extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(unique = true, nullable = false)
	private String name = null;

	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal height;

	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal width;

	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal depth;

	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal weight;

	/**
	 * The max weight that such a unit load can handle
	 */
	@Column(nullable = true, precision = 16, scale = 3)
	private BigDecimal liftingCapacity;

	/**
	 * Activate the manage empties process for the type.
	 * <p>
	 * Empty unit loads will not be automatically removed.
	 */
	@Column(nullable = false)
	private boolean manageEmpties = false;

	/**
	 * On this type stocks will be aggregated automatically.
	 * <p>
	 * If you place two identical stock units on a unit load of this type, the
	 * result will be one stock unit with aggregated amount.
	 */
	@Column(nullable = false)
	private boolean aggregateStocks = true;

	/**
	 * Calculate the sum weight for unit loads with this type.
	 * <p>
	 * Do not activate on container types. The calculation will use many
	 * processing performance.
	 */
	@Column(nullable = false)
	private boolean calculateWeight = true;

	/**
	 * A comma separated list of the usage keys.
	 */
	private String usages;

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

	@Transient
	public boolean isUseFor(String usageKey) {
		if (usages == null) {
			return false;
		}
		return usages.contains(usageKey);
	}

	@Transient
	public void setUseFor(String usageKey, boolean activated) {
		if (usageKey == null) {
			return;
		}
		if (usages == null) {
			usages = "";
		}
		if (activated) {
			if (!usages.contains(usageKey)) {
				List<String> usageList = new ArrayList<>();
				usageList.addAll(ListUtils.stringToList(usages));
				usageList.add(usageKey);
				usages = ListUtils.listToString(usageList);
			}
		} else {
			if (usages.contains(usageKey)) {
				List<String> usageList = new ArrayList<>();
				usageList.addAll(ListUtils.stringToList(usages));
				usageList.remove(usageKey);
				usages = ListUtils.listToString(usageList);
			}
		}
	}

	/**
	 * @deprecated Replaced by UnitLoadType.isUseFor(UnitLoadTypeUsages.SHIPPING);
	 */
	@Deprecated
	@Transient
	public boolean isUseForShipping() {
		return isUseFor(UnitLoadTypeUsages.SHIPPING);
	}

	/**
	 * @deprecated Replaced by UnitLoadType.setUseFor(UnitLoadTypeUsages.SHIPPING,
	 *             useForPicking);
	 */
	@Deprecated
	@Transient
	public void setUseForShipping(boolean useForShipping) {
		setUseFor(UnitLoadTypeUsages.SHIPPING, useForShipping);
	}

	@Transient
	public BigDecimal getVolume() {
		if (height != null && width != null && depth != null) {
			return height.multiply(width).multiply(depth);
		}
		return null;
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

	public BigDecimal getLiftingCapacity() {
		return liftingCapacity;
	}

	public void setLiftingCapacity(BigDecimal liftingCapacity) {
		this.liftingCapacity = liftingCapacity;
	}

	public boolean isManageEmpties() {
		return manageEmpties;
	}

	public void setManageEmpties(boolean manageEmpties) {
		this.manageEmpties = manageEmpties;
	}

	public boolean isAggregateStocks() {
		return aggregateStocks;
	}

	public void setAggregateStocks(boolean aggregateStocks) {
		this.aggregateStocks = aggregateStocks;
	}

	public boolean isCalculateWeight() {
		return calculateWeight;
	}

	public void setCalculateWeight(boolean calculateWeight) {
		this.calculateWeight = calculateWeight;
	}

	public String getUsages() {
		return usages;
	}

	public void setUsages(String usages) {
		this.usages = usages;
	}

}
