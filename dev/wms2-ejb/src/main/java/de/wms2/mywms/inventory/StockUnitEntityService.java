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

	public List<StockUnit> readByUnitLoad(UnitLoad unitLoad) {
		return readList(null, null, null, unitLoad, null, null, null, null);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param client       Optional
	 * @param itemData     Optional
	 * @param lotNumber    Optional
	 * @param unitLoad     Optional
	 * @param location     Optional
	 * @param serialNumber Optional
	 * @param offset       Optional
	 * @param limit        Optional
	 */
	@SuppressWarnings("unchecked")
	public List<StockUnit> readList(Client client, ItemData itemData, Lot lot, UnitLoad unitLoad,
			StorageLocation location, String serialNumber, Integer offset, Integer limit) {
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
		if (lot != null) {
			hql += " AND stock.lot=:lot";
		}
		if (!StringUtils.isBlank(serialNumber)) {
			hql += " AND stock.serialNumber=:serial";
		}
		hql += " order by stock.id";

		Query query = manager.createQuery(hql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
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
		if (lot != null) {
			query = query.setParameter("lot", lot);
		}
		if (!StringUtils.isBlank(serialNumber)) {
			query = query.setParameter("serial", serialNumber);
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
	 * 
	 * @param client       Optional
	 * @param itemData     Optional
	 * @param lotNumber    Optional
	 * @param unitLoad     Optional
	 * @param location     Optional
	 * @param serialNumber Optional
	 * @return
	 */
	public boolean exists(Client client, ItemData itemData, String lotNumber, UnitLoad unitLoad,
			StorageLocation location, String serialNumber) {
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
			hql += " AND stock.lot.name=:lotNumber";
		}
		if (!StringUtils.isBlank(serialNumber)) {
			hql += " AND stock.serialNumber=:serial";
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

		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (Throwable ex) {
		}

		return false;
	}

	/**
	 * Select the number of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param client       Optional
	 * @param itemData     Optional
	 * @param lotNumber    Optional
	 * @param unitLoad     Optional
	 * @param location     Optional
	 * @param serialNumber Optional
	 * @return
	 */
	public int readCount(Client client, ItemData itemData, String lotNumber, UnitLoad unitLoad,
			StorageLocation location, String serialNumber) {
		String hql = "SELECT count(*) FROM " + StockUnit.class.getName() + " stock";
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
			hql += " AND stock.lot.name=:lotNumber";
		}
		if (!StringUtils.isBlank(serialNumber)) {
			hql += " AND stock.serialNumber=:serial";
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

		Long num = (Long) query.getSingleResult();
		return num.intValue();
	}
}
