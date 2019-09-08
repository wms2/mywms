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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BasicEntity;

/**
 * This class is based on myWMS-LOS:LOSStorageStrategy
 * <p>
 * Definition of rules to search a storage location
 */
@Entity
@Table
public class StorageStrategy extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Zone zone;

	/**
	 * This field is just to give edit possibility to the rich client. No further
	 * use.
	 */
	@Transient
	private int orderByMode = StorageStrategyRichClientConverter.UNDEFINED;

	/**
	 * Allow mix items on storage location
	 */
	@Column(nullable = false)
	private boolean mixItem = true;

	/**
	 * Allow mix clients on storage location
	 */
	@Column(nullable = false)
	private boolean mixClient = false;

	/**
	 * Use only locations of the client of the stock unit
	 */
	@Column(nullable = false)
	private boolean onlyClientLocation = false;

	/**
	 * Search location manual
	 */
	@Column(nullable = false)
	private boolean manualSearch = false;

	/**
	 * Comma separated list of sort criteria (StorageStrategySortType)
	 */
	private String sorts;

	/**
	 * Search locations in the near of a fix assigned location. This rule is
	 * operated on every storage level separately. The search only considers
	 * locations in the aisle of the fix assigned location. The distance is
	 * calculated with the X-coordinate.
	 */
	private boolean nearPickingLocation = false;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public int getOrderByMode() {
		return orderByMode;
	}

	public void setOrderByMode(int orderByMode) {
		this.orderByMode = orderByMode;
	}

	public boolean isMixItem() {
		return mixItem;
	}

	public void setMixItem(boolean mixItem) {
		this.mixItem = mixItem;
	}

	public boolean isMixClient() {
		return mixClient;
	}

	public void setMixClient(boolean mixClient) {
		this.mixClient = mixClient;
	}

	public boolean isOnlyClientLocation() {
		return onlyClientLocation;
	}

	public void setOnlyClientLocation(boolean onlyClientLocation) {
		this.onlyClientLocation = onlyClientLocation;
	}

	public boolean isManualSearch() {
		return manualSearch;
	}

	public void setManualSearch(boolean manualSearch) {
		this.manualSearch = manualSearch;
	}

	public String getSorts() {
		return sorts;
	}

	public void setSorts(String sorts) {
		this.sorts = sorts;
	}

	public boolean isNearPickingLocation() {
		return nearPickingLocation;
	}

	public void setNearPickingLocation(boolean nearPickingLocation) {
		this.nearPickingLocation = nearPickingLocation;
	}

}
