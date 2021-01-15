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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.util.ListUtils;

/**
 * This class replaces myWMS:Area and myWMS-LOS:LOSArea
 *
 * The Area is a logical grouping of StorageLocations
 * <p>
 * Some basic logical operations are defined in {@link AreaUsages}
 */
@Entity
@Table
public class Area extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name = null;

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
		return super.toUniqueString();
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
	 * @deprecated Replaced by Area.isUseFor(AreaUsages.GOODS_IN);
	 */
	@Deprecated
	@Transient
	public boolean isUseForGoodsIn() {
		return isUseFor(AreaUsages.GOODS_IN);
	}

	/**
	 * @deprecated Replaced by Area.setUseFor(AreaUsages.GOODS_IN, useForGoodsIn);
	 */
	@Deprecated
	@Transient
	public void setUseForGoodsIn(boolean useForGoodsIn) {
		setUseFor(AreaUsages.GOODS_IN, useForGoodsIn);
	}

	/**
	 * @deprecated Replaced by Area.isUseFor(AreaUsages.STORAGE);
	 */
	@Deprecated
	@Transient
	public boolean isUseForStorage() {
		return isUseFor(AreaUsages.STORAGE);
	}

	/**
	 * @deprecated Replaced by Area.setUseFor(AreaUsages.STORAGE, useForStorage);
	 */
	@Deprecated
	@Transient
	public void setUseForStorage(boolean useForStorage) {
		setUseFor(AreaUsages.STORAGE, useForStorage);
	}

	/**
	 * @deprecated Replaced by Area.isUseFor(AreaUsages.GOODS_OUT);
	 */
	@Deprecated
	@Transient
	public boolean isUseForGoodsOut() {
		return isUseFor(AreaUsages.GOODS_OUT);
	}

	/**
	 * @deprecated Replaced by Area.setUseFor(AreaUsages.GOODS_OUT, useForGoodsOut);
	 */
	@Deprecated
	@Transient
	public void setUseForGoodsOut(boolean useForGoodsOut) {
		setUseFor(AreaUsages.GOODS_OUT, useForGoodsOut);
	}

	/**
	 * @deprecated Replaced by Area.isUseFor(AreaUsages.PICKING);
	 */
	@Deprecated
	@Transient
	public boolean isUseForPicking() {
		return isUseFor(AreaUsages.PICKING);
	}

	/**
	 * @deprecated Replaced by Area.setUseFor(AreaUsages.PICKING, useForPicking);
	 */
	@Deprecated
	@Transient
	public void setUseForPicking(boolean useForPicking) {
		setUseFor(AreaUsages.PICKING, useForPicking);
	}

	/**
	 * @deprecated Replaced by Area.isUseFor(AreaUsages.TRANSFER);
	 */
	@Deprecated
	@Transient
	public boolean isUseForTransfer() {
		return isUseFor(AreaUsages.TRANSFER);
	}

	/**
	 * @deprecated Replaced by Area.setUseFor(AreaUsages.TRANSFER, useForTransfer);
	 */
	@Deprecated
	@Transient
	public void setUseForTransfer(boolean useForTransfer) {
		setUseFor(AreaUsages.TRANSFER, useForTransfer);
	}

	/**
	 * @deprecated Replaced by Area.isUseFor(AreaUsages.REPLENISH);
	 */
	@Deprecated
	@Transient
	public boolean isUseForReplenish() {
		return isUseFor(AreaUsages.REPLENISH);
	}

	/**
	 * @deprecated Replaced by Area.setUseFor(AreaUsages.REPLENISH,
	 *             useForReplenish);
	 */
	@Deprecated
	@Transient
	public void setUseForReplenish(boolean useForReplenish) {
		setUseFor(AreaUsages.REPLENISH, useForReplenish);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsages() {
		return usages;
	}

	public void setUsages(String usages) {
		this.usages = usages;
	}

}
