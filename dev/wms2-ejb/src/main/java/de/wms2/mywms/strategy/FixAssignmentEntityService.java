/* 
Copyright 2019 Matthias Krane

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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;

/**
 * @author krane
 *
 */
@Stateless
public class FixAssignmentEntityService {
	@Inject
	private PersistenceManager manager;

	public FixAssignment create(ItemData itemData, StorageLocation location) throws BusinessException {
		FixAssignment entity = manager.createInstance(FixAssignment.class);
		entity.setItemData(itemData);
		entity.setStorageLocation(location);

		manager.persistValidated(entity);

		return entity;
	}

	/**
	 * Read the first entry for the given parameters.
	 */
	public FixAssignment read(ItemData itemData, StorageLocation location) {
		String hql = "SELECT entity FROM " + FixAssignment.class.getName() + " entity WHERE 1=1";
		hql += " and entity.itemData=:itemData";
		hql += " and entity.storageLocation=:location";
		Query query = manager.createQuery(hql);
		query.setParameter("itemData", itemData);
		query.setParameter("location", location);

		try {
			return (FixAssignment) query.getSingleResult();
		} catch (NoResultException x) {
		}
		return null;
	}

	/**
	 * Check whether an entry exists which is matching the given parameters. All
	 * parameters are optional.
	 * 
	 * @param itemData
	 *            Optional
	 * @param location
	 *            Optional
	 */
	public boolean exists(ItemData itemData, StorageLocation location) {
		String hql = "SELECT entity.id FROM " + FixAssignment.class.getName() + " entity WHERE 1=1";
		if (itemData != null) {
			hql += " and entity.itemData=:itemData";
		}
		if (location != null) {
			hql += " and entity.storageLocation=:location";
		}
		Query query = manager.createQuery(hql);
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (location != null) {
			query.setParameter("location", location);
		}
		query.setMaxResults(1);

		try {
			query.getSingleResult();
		} catch (NoResultException x) {
			return false;
		}
		return true;
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional. The result is ordered by itemData and orderIndex.
	 * 
	 * @param itemData
	 *            Optional
	 * @param location
	 *            Optional
	 * @param picking
	 *            Optional
	 * @param offset
	 *            Optional
	 * @param limit
	 *            Optional
	 */
	@SuppressWarnings("unchecked")
	public List<FixAssignment> readList(ItemData itemData, StorageLocation location, Boolean picking, Integer offset,
			Integer limit) {
		String hql = "SELECT entity FROM " + FixAssignment.class.getName() + " entity WHERE 1=1";
		if (itemData != null) {
			hql += " and entity.itemData=:itemData";
		}
		if (location != null) {
			hql += " and entity.storageLocation=:location";
		}
		if (picking != null) {
			hql += " and entity.storageLocation.area.usages like '%" + AreaUsages.PICKING + "%'";
		}
		hql += " ORDER BY entity.itemData, entity.orderIndex";
		Query query = manager.createQuery(hql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (location != null) {
			query.setParameter("location", location);
		}

		return query.getResultList();
	}

	/**
	 * Search all fix assignments, that have a location or itemData with the given
	 * code,
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<FixAssignment> readListByCode(String searchCode) {
		if (StringUtils.isBlank(searchCode)) {
			return new ArrayList<>();
		}
		String hql = "SELECT fix FROM " + FixAssignment.class.getName() + " fix ";
		hql += " where exists( ";
		hql += "   select 1 from " + ItemData.class.getName() + " itemData ";
		hql += "   where fix.itemData=itemData and itemData.number=:code";
		hql += " )";
		hql += " or exists( ";
		hql += "   select 1 from " + ItemDataNumber.class.getName() + " productCode ";
		hql += "   where fix.itemData=productCode.itemData and productCode.productCode=:number";
		hql += " )";
		hql += " or exists( ";
		hql += "   select 1 from " + StorageLocation.class.getName() + " location ";
		hql += "   where fix.storageLocation=location and (location.name=:code or location.barcode=:code)";
		hql += " )";
		hql += " ORDER BY fix.itemData, fix.orderIndex";
		Query query = manager.createQuery(hql);
		query.setParameter("code", searchCode);

		return query.getResultList();
	}

	/**
	 * Select a list of all itemDatas which have an assignment for the given
	 * location. The result is ordered by itemData and orderIndex.
	 */
	@SuppressWarnings("unchecked")
	public List<ItemData> readItemDataListByLocation(StorageLocation location) {
		String hql = "SELECT entity.itemData FROM " + FixAssignment.class.getName() + " entity WHERE 1=1";
		hql += " and entity.storageLocation=:location";
		hql += " ORDER BY entity.itemData, entity.orderIndex";
		Query query = manager.createQuery(hql);
		query.setParameter("location", location);

		return query.getResultList();
	}

	/**
	 * Read the first entry for the given parameters. All parameters are optional.
	 * The result is ordered by itemData and orderIndex.
	 * 
	 * @param itemData
	 *            Optional
	 * @param location
	 *            Optional
	 */
	public FixAssignment readFirst(ItemData itemData, StorageLocation location) {
		String hql = "SELECT entity FROM " + FixAssignment.class.getName() + " entity WHERE 1=1";
		if (itemData != null) {
			hql += " and entity.itemData=:itemData";
		}
		if (location != null) {
			hql += " and entity.storageLocation=:location";
		}
		hql += " ORDER BY entity.itemData, entity.orderIndex";
		Query query = manager.createQuery(hql);
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (location != null) {
			query.setParameter("location", location);
		}
		query.setMaxResults(1);
		try {
			return (FixAssignment) query.getSingleResult();
		} catch (NoResultException x) {
		}
		return null;
	}

}
