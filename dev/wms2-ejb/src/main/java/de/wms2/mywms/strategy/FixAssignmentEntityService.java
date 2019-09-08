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
package de.wms2.mywms.strategy;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

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
	 */
	@SuppressWarnings("unchecked")
	public List<FixAssignment> readList(ItemData itemData, StorageLocation location, Boolean picking) {
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
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (location != null) {
			query.setParameter("location", location);
		}

		return query.getResultList();
	}

	public List<FixAssignment> readByItemData(ItemData itemData) {
		return readList(itemData, null, null);
	}

	public List<FixAssignment> readByLocation(StorageLocation location) {
		return readList(null, location, null);
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

	public FixAssignment readFirstByLocation(StorageLocation location) {
		return readFirst(null, location, null);
	}

	public FixAssignment readFirstByItemData(ItemData itemData, Boolean useForPicking) {
		return readFirst(itemData, null, useForPicking);
	}

	/**
	 * Read the first entry for the given parameters. All parameters are optional.
	 * The result is ordered by itemData and orderIndex.
	 */
	public FixAssignment readFirst(ItemData itemData, StorageLocation location, Boolean useForPicking) {
		String hql = "SELECT entity FROM " + FixAssignment.class.getName() + " entity WHERE 1=1";
		if (itemData != null) {
			hql += " and entity.itemData=:itemData";
		}
		if (location != null) {
			hql += " and entity.storageLocation=:location";
		}
		if (useForPicking != null && useForPicking.booleanValue()) {
			hql += " and entity.storageLocation.area.usages like '%" + AreaUsages.PICKING + "%'";
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
