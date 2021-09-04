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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * @author krane
 *
 */
@Stateless
public class StorageStrategyLayerEntityService {

	@Inject
	private PersistenceManager manager;

	public StorageStrategyLayer create(StorageStrategy storageStrategy, int storageLayer) throws BusinessException {
		StorageStrategyLayer entity = manager.createInstance(StorageStrategyLayer.class);
		entity.setStorageStrategy(storageStrategy);
		entity.setStorageLayer(storageLayer);
		entity.setLocationClusters(new ArrayList<>());

		manager.persistValidated(entity);

		return entity;
	}

	public StorageStrategyLayer read(StorageStrategy storageStrategy, int storageLayer) {
		String jpql = "select entity from " + StorageStrategyLayer.class.getName() + " entity";
		jpql += " where entity.storageStrategy=:storageStrategy and entity.storageLayer=:storageLayer";
		TypedQuery<StorageStrategyLayer> query = manager.createQuery(jpql, StorageStrategyLayer.class);
		query.setParameter("storageStrategy", storageStrategy);
		query.setParameter("storageLayer", storageLayer);
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public List<StorageStrategyLayer> readByStorageStrategy(StorageStrategy storageStrategy) {
		if (storageStrategy == null || storageStrategy.getId() == null) {
			return new ArrayList<>();
		}
		String jpql = "select entity from " + StorageStrategyLayer.class.getName() + " entity ";
		jpql += "where entity.storageStrategy=:storageStrategy";
		jpql += " order by entity.storageLayer";
		TypedQuery<StorageStrategyLayer> query = manager.createQuery(jpql, StorageStrategyLayer.class);
		query.setParameter("storageStrategy", storageStrategy);
		return query.getResultList();
	}

	public void removeByStorageStrategy(StorageStrategy storageStrategy) {
		if (storageStrategy == null || storageStrategy.getId() == null) {
			return;
		}
		String jpql = "delete from " + StorageStrategyLayer.class.getName() + " entity ";
		jpql += "where entity.storageStrategy=:storageStrategy";
		Query query = manager.createQuery(jpql);
		query.setParameter("storageStrategy", storageStrategy);
		query.executeUpdate();
	}
}
