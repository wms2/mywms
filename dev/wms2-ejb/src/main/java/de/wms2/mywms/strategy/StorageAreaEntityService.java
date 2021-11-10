/* 
Copyright 2021 Matthias Krane
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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 * 
 */
@Stateless
public class StorageAreaEntityService {

	@Inject
	private PersistenceManager manager;

	public StorageArea create(String name) throws BusinessException {
		StorageArea area = manager.createInstance(StorageArea.class);
		area.setName(name);

		manager.persistValidated(area);

		return area;
	}

	public StorageArea read(String name) {
		String jpql = "SELECT entity FROM " + StorageArea.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		TypedQuery<StorageArea> query = manager.createQuery(jpql, StorageArea.class);
		query.setParameter("name", name);
		try {
			return query.getSingleResult();
		} catch (NoResultException t) {
		}
		return null;
	}

	public List<StorageArea> readByStorageStrategy(StorageStrategy storageStrategy) {
		if (storageStrategy == null || storageStrategy.getId() == null) {
			return new ArrayList<>();
		}
		String jpql = "select storageArea from " + StorageArea.class.getName() + " storageArea,";
		jpql += StorageStrategyArea.class.getSimpleName() + " strategyArea";
		jpql += " where strategyArea.storageStrategy=:storageStrategy";
		jpql += " and storageArea=strategyArea.storageArea";
		jpql += " order by strategyArea.orderIndex";
		TypedQuery<StorageArea> query = manager.createQuery(jpql, StorageArea.class);
		query.setParameter("storageStrategy", storageStrategy);
		return query.getResultList();
	}

	public List<StorageArea> readByItemData(ItemData itemdata) {
		if (itemdata == null || itemdata.getId() == null) {
			return new ArrayList<>();
		}
		String jpql = "select entity from " + StorageArea.class.getName() + " entity";
		jpql += " where entity.itemdata=:itemdata";
		jpql += " order by entity.orderIndex";
		TypedQuery<StorageArea> query = manager.createQuery(jpql, StorageArea.class);
		query.setParameter("itemdata", itemdata);
		return query.getResultList();
	}

	public void saveForStorageStrategy(StorageStrategy strategy, List<StorageArea> areas) {
		removeForStorageStrategy(strategy);

		int orderIndex = 1;
		for (StorageArea area : areas) {
			StorageStrategyArea strategyArea = new StorageStrategyArea(strategy, area, orderIndex);
			manager.persist(strategyArea);
			orderIndex++;
		}
	}

	public void removeForStorageStrategy(StorageStrategy strategy) {
		String jpql = "delete from " + StorageStrategyArea.class.getSimpleName() + " strategyArea";
		jpql += " where storageStrategy=:strategy";
		Query query = manager.createQuery(jpql);
		query.setParameter("strategy", strategy);
		query.executeUpdate();
	}
}
