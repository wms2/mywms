/* 
Copyright 2019-2022 Matthias Krane
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
package de.wms2.mywms.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 * 
 */
@Stateless
public class StockUnitEntityService {
	@Inject
	private PersistenceManager manager;

	public List<StockUnit> readByLocation(StorageLocation location) {
		return readList(null, null, null, null, location, null, null, null);
	}

	public boolean existsByUnitLoad(UnitLoad unitLoad) {
		return exists(null, null, null, unitLoad, null, null, null);
	}

	public List<StockUnit> readByUnitLoad(UnitLoad unitLoad) {
		return readList(null, null, null, unitLoad, null, null, null, null);
	}

	public boolean existsByItemData(ItemData itemData) {
		return exists(null, itemData, null, null, null, null, StockState.DELETABLE - 1);
	}

	public List<StockUnit> readByItemData(ItemData itemData) {
		return readList(null, itemData, null, null, null, null, StockState.DELETABLE - 1, null);
	}

	public boolean existsBySerialNumber(ItemData itemData, String serialNumber) {
		return exists(null, itemData, null, null, null, serialNumber, StockState.DELETABLE - 1);
	}

	public List<StockUnit> readBySerialNumber(ItemData itemData, String serialNumber) {
		return readList(null, itemData, null, null, null, serialNumber, StockState.DELETABLE - 1, null);
	}

	public boolean existsByItemDataLocation(ItemData itemData, StorageLocation location) {
		return exists(null, itemData, null, null, location, null, StockState.DELETABLE - 1);
	}

	public List<StockUnit> readByItemDataLocation(ItemData itemData, StorageLocation location) {
		return readList(null, itemData, null, null, location, null, StockState.DELETABLE - 1, null);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 */
	@SuppressWarnings("unchecked")
	public List<StockUnit> readList(Client client, ItemData itemData, String lotNumber, UnitLoad unitLoad,
			StorageLocation location, String serialNumber, Integer maxState, Integer limit) {
		String hql = "SELECT stock, stock.unitLoad, stock.itemData FROM " + StockUnit.class.getName() + " stock";
		hql += " WHERE 1=1";
		if (client != null) {
			hql += " AND stock.client=:client";
		}
		if (itemData != null) {
			hql += " AND stock.itemData=:itemData";
		}
		if (unitLoad != null) {
			hql += " AND stock.unitLoad=:unitLoad";
		}
		if (location != null) {
			hql += " AND stock.unitLoad.storageLocation=:location";
		}
		if (!StringUtils.isBlank(lotNumber)) {
			hql += " AND stock.lotNumber=:lotNumber";
		}
		if (!StringUtils.isBlank(serialNumber)) {
			hql += " AND stock.serialNumber=:serial";
		}
		if (maxState != null) {
			hql += " AND stock.state<=:maxState";
		}
		hql += " order by stock.strategyDate, stock.id";

		Query query = manager.createQuery(hql);
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (client != null) {
			query.setParameter("client", client);
		}
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (unitLoad != null) {
			query.setParameter("unitLoad", unitLoad);
		}
		if (location != null) {
			query.setParameter("location", location);
		}
		if (!StringUtils.isBlank(lotNumber)) {
			query.setParameter("lotNumber", lotNumber);
		}
		if (!StringUtils.isBlank(serialNumber)) {
			query.setParameter("serial", serialNumber);
		}
		if (maxState != null) {
			query.setParameter("maxState", maxState);
		}

		List<Object[]> results = query.getResultList();
		List<StockUnit> entities = new ArrayList<>(results.size());
		for (Object[] result : results) {
			StockUnit stock = (StockUnit) result[0];
			entities.add(stock);
		}

		return entities;
	}

	/**
	 * Checks whether an entity exists, which is matching the given criteria. All
	 * parameters are optional.
	 */
	public boolean exists(Client client, ItemData itemData, String lotNumber, UnitLoad unitLoad, StorageLocation location,
			String serialNumber, Integer maxState) {
		String hql = "SELECT stock.id FROM " + StockUnit.class.getName() + " stock";
		hql += " WHERE 1=1";
		if (client != null) {
			hql += " AND stock.client=:client";
		}
		if (itemData != null) {
			hql += " AND stock.itemData=:itemData";
		}
		if (unitLoad != null) {
			hql += " AND stock.unitLoad=:unitLoad";
		}
		if (location != null) {
			hql += " AND stock.unitLoad.storageLocation=:location";
		}
		if (!StringUtils.isBlank(lotNumber)) {
			hql += " AND stock.lotNumber=:lotNumber";
		}
		if (!StringUtils.isBlank(serialNumber)) {
			hql += " AND stock.serialNumber=:serial";
		}
		if (maxState != null) {
			hql += " AND stock.state<=:maxState";
		}

		Query query = manager.createQuery(hql);
		if (client != null) {
			query = query.setParameter("client", client);
		}
		if (itemData != null) {
			query = query.setParameter("itemData", itemData);
		}
		if (unitLoad != null) {
			query = query.setParameter("unitLoad", unitLoad);
		}
		if (location != null) {
			query = query.setParameter("location", location);
		}
		if (!StringUtils.isBlank(lotNumber)) {
			query = query.setParameter("lotNumber", lotNumber);
		}
		if (!StringUtils.isBlank(serialNumber)) {
			query = query.setParameter("serial", serialNumber);
		}
		if (maxState != null) {
			query.setParameter("maxState", maxState);
		}

		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (Throwable ex) {
		}

		return false;
	}

}
