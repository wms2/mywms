/* 
Copyright 2021 Matthias Krane

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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.location.LocationCluster;

/**
 * @author krane
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "storageStrategy_id", "storageLayer" }) })
public class StorageStrategyLayer extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private StorageStrategy storageStrategy;

	/**
	 * The level is used for amount limitations of products in certain areas.
	 * <p>
	 * It is referenced by the product and the storage strategy.
	 */
	@Column(nullable = false)
	private int storageLayer = 0;

	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy("name")
	private List<LocationCluster> locationClusters;

	@Override
	public String toString() {
		if (storageStrategy != null) {
			return storageStrategy.getName() + "/" + storageLayer;
		}
		return super.toString();
	}

	public StorageStrategy getStorageStrategy() {
		return storageStrategy;
	}

	public void setStorageStrategy(StorageStrategy storageStrategy) {
		this.storageStrategy = storageStrategy;
	}

	public int getStorageLayer() {
		return storageLayer;
	}

	public void setStorageLayer(int storageLayer) {
		this.storageLayer = storageLayer;
	}

	public List<LocationCluster> getLocationClusters() {
		return locationClusters;
	}

	public void setLocationClusters(List<LocationCluster> locationClusters) {
		this.locationClusters = locationClusters;
	}

}
