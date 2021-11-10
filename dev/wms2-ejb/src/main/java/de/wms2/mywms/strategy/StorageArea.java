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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.location.LocationCluster;

/**
 * @author krane
 *
 */
@Entity
@Table
public class StorageArea extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(unique = true, nullable = false)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy("name")
	private List<LocationCluster> locationClusters;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<LocationCluster> getLocationClusters() {
		return locationClusters;
	}

	public void setLocationClusters(List<LocationCluster> locationClusters) {
		this.locationClusters = locationClusters;
	}

}
