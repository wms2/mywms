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
package de.wms2.mywms.product;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * @author krane
 * 
 */
@Stateless
public class ItemDataEntityService {
	@Inject
	private PersistenceManager manager;

	public ItemData create(Client client, String number, String name, ItemUnit unit) throws BusinessException {
		ItemData itemData = manager.createInstance(ItemData.class);
		itemData.setClient(client);
		itemData.setNumber(number);
		itemData.setName(name);
		itemData.setItemUnit(unit);

		manager.persistValidated(itemData);
		manager.flush();

		return itemData;
	}

	public ItemData readByNumber(String number) {
		String jpql = "SELECT entity FROM " + ItemData.class.getName() + " entity ";
		jpql += " where entity.number=:number";
		Query query = manager.createQuery(jpql);
		query.setParameter("number", number);

		try {
			return (ItemData) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public ItemData readByNumberIgnoreCase(String number) {
		if (StringUtils.isBlank(number)) {
			return null;
		}

		ItemData itemData = readByNumber(number);
		if (itemData != null) {
			return itemData;
		}

		String jpql = "SELECT entity FROM " + ItemData.class.getName() + " entity ";
		jpql += " where lower(entity.number)=:number";
		Query query = manager.createQuery(jpql);
		query.setParameter("number", number.toLowerCase());

		try {
			return (ItemData) query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
		}

		return null;
	}

	public boolean existsByNumberIgnoreCase(String number, Long ignoreId) {
		if (StringUtils.isBlank(number)) {
			return false;
		}

		String hql = "SELECT itemData.id FROM " + ItemData.class.getName() + " itemData ";
		hql += " WHERE lower(itemData.number)=:number";
		if (ignoreId != null) {
			hql += " and itemData.id!=:ignoreId";
		}

		Query query = manager.createQuery(hql);
		query.setParameter("number", number.toLowerCase());
		if (ignoreId != null) {
			query.setParameter("ignoreId", ignoreId);
		}
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}
}
