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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * @author krane
 *
 */
@Entity
@Table
public class StorageStrategyArea extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private StorageStrategy storageStrategy;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private StorageArea storageArea;

	@Column(nullable = false)
	private int orderIndex = 0;

	public StorageStrategyArea(StorageStrategy storageStrategy, StorageArea storageArea, int orderIndex) {
		this.storageStrategy = storageStrategy;
		this.storageArea = storageArea;
		this.orderIndex = orderIndex;
	}

	@Override
	public String toString() {
		String value="";
		if (storageStrategy != null) {
			value+=storageStrategy.toString();
		}
		if (storageArea != null) {
			value+=" / "+storageArea.toString();
		}
		if(value.length()>0) {
			return value;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		String value="";
		if (storageStrategy != null) {
			value+=storageStrategy.toString();
		}
		if (storageArea != null) {
			value+=" / "+storageArea.toString();
		}
		if(value.length()>0) {
			return value;
		}
		return super.toUniqueString();
	}

	public StorageStrategy getStorageStrategy() {
		return storageStrategy;
	}

	public void setStorageStrategy(StorageStrategy storageStrategy) {
		this.storageStrategy = storageStrategy;
	}

	public StorageArea getStorageArea() {
		return storageArea;
	}

	public void setStorageArea(StorageArea storageArea) {
		this.storageArea = storageArea;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

}
