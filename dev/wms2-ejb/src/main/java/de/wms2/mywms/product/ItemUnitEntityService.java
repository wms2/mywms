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
package de.wms2.mywms.product;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mywms.model.ItemUnitType;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * @author krane
 * 
 */
@Stateless
public class ItemUnitEntityService {
	@Inject
	private PersistenceManager manager;

	public ItemUnit create(String name) throws BusinessException {
		ItemUnit itemUnit = manager.createInstance(ItemUnit.class);
		itemUnit.setName(name);
		itemUnit.setUnitType(ItemUnitType.PIECE);

		manager.persistValidated(itemUnit);
		manager.flush();

		return itemUnit;
	}

	public ItemUnit readByName(String name) {
		String jpql = "SELECT entity FROM " + ItemUnit.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		TypedQuery<ItemUnit> query = manager.createQuery(jpql, ItemUnit.class);
		query.setParameter("name", name);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public ItemUnit getDefault() {
		ItemUnit unit = manager.find(ItemUnit.class, 0L);
		if (unit != null) {
			return unit;
		}

		String name = "Default";
		unit = readByName(name);
		if (unit == null) {
			unit = manager.createInstance(ItemUnit.class);
			unit.setName(name);
			unit.setUnitType(ItemUnitType.PIECE);
			manager.persist(unit);
		}

		String queryStr = "UPDATE " + ItemUnit.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", 0L);
		query.setParameter("idOld", unit.getId());
		query.executeUpdate();
		manager.flush();

		return manager.find(ItemUnit.class, 0L);
	}
}
