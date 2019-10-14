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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * @author krane
 * 
 */
@Stateless
public class ItemDataNumberEntityService {
	@Inject
	private PersistenceManager manager;

	public ItemDataNumber create(ItemData itemData, String number) throws BusinessException {
		ItemDataNumber itemDataNumber = manager.createInstance(ItemDataNumber.class);
		itemDataNumber.setItemData(itemData);
		itemDataNumber.setNumber(number);

		manager.persistValidated(itemDataNumber);
		manager.flush();
		
		return itemDataNumber;
	}

	public ItemDataNumber read(ItemData itemData, String number) {
		String hql = "SELECT entity FROM " + ItemDataNumber.class.getName() + " entity ";
		hql += " WHERE entity.itemData=:itemData and entity.number=:number";
		Query query = manager.createQuery(hql);
		query.setParameter("itemData", itemData);
		query.setParameter("number", number);
		try {
			return (ItemDataNumber) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public ItemDataNumber readIgnoreCase(String number, ItemData itemData) {
		if (StringUtils.isBlank(number)) {
			return null;
		}

		ItemDataNumber itemDataNumber = read(itemData, number);
		if (itemDataNumber != null) {
			return itemDataNumber;
		}

		String hql = "SELECT entity FROM " + ItemDataNumber.class.getName() + " entity ";
		hql += " WHERE entity.itemData=:itemData and lower(entity.number)=:number";
		Query query = manager.createQuery(hql);
		query.setParameter("itemData", itemData);
		query.setParameter("number", number.toLowerCase());
		try {
			return (ItemDataNumber) query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
		}
		return null;
	}

	/**
	 * Read all numbers of the given itemData. The result is ordered by the number.
	 */
	@SuppressWarnings("unchecked")
	public List<ItemDataNumber> readByItemData(ItemData itemData) {
		String jpql = "SELECT entity FROM " + ItemDataNumber.class.getName() + " entity ";
		jpql += " WHERE entity.itemData=:itemData";
		jpql += " ORDER BY entity.number";
		Query query = manager.createQuery(jpql);
		query.setParameter("itemData", itemData);

		return query.getResultList();
	}

	/**
	 * Read all numbers of the given number. The result is ordered by the itemData.
	 */
	@SuppressWarnings("unchecked")
	public List<ItemDataNumber> readByNumber(String number) {
		String jpql = "SELECT entity FROM " + ItemDataNumber.class.getName() + " entity ";
		jpql += " WHERE entity.number=:number";
		jpql += " ORDER BY entity.itemData.number";
		Query query = manager.createQuery(jpql);
		query.setParameter("number", number);

		return query.getResultList();
	}

}
