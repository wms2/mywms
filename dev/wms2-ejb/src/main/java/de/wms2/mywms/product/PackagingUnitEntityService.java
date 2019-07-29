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
package de.wms2.mywms.product;

import java.math.BigDecimal;
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
public class PackagingUnitEntityService {
	@Inject
	private PersistenceManager manager;

	public PackagingUnit create(ItemData itemData, String name, BigDecimal amount) throws BusinessException {
		PackagingUnit packagingUnit = manager.createInstance(PackagingUnit.class);
		packagingUnit.setName(name);
		packagingUnit.setItemData(itemData);
		packagingUnit.setAmount(amount);

		manager.persistValidated(packagingUnit);

		return packagingUnit;
	}

	public PackagingUnit read(ItemData itemData, String name) {
		String hql = "SELECT packaging FROM " + PackagingUnit.class.getName() + " packaging ";
		hql += " WHERE packaging.itemData=:itemData and packaging.name=:name";
		Query query = manager.createQuery(hql);
		query.setParameter("itemData", itemData);
		query.setParameter("name", name);
		try {
			return (PackagingUnit) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public PackagingUnit readIgnoreCase(String name, ItemData itemData) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		PackagingUnit packagingUnit = read(itemData, name);
		if (packagingUnit != null) {
			return packagingUnit;
		}

		String hql = "SELECT packaging FROM " + PackagingUnit.class.getName() + " packaging ";
		hql += " WHERE packaging.itemData=:itemData and lower(packaging.name)=:name";
		Query query = manager.createQuery(hql);
		query.setParameter("itemData", itemData);
		query.setParameter("name", name.toLowerCase());
		try {
			return (PackagingUnit) query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
		}
		return null;
	}

	public boolean existsIgnoreCase(ItemData itemData, String name, Long ignoreId) {
		if (StringUtils.isBlank(name)) {
			return false;
		}

		String hql = "SELECT packaging.id FROM " + PackagingUnit.class.getName() + " packaging ";
		hql += " WHERE packaging.itemData=:itemData and lower(packaging.name)=:name";
		if (ignoreId != null) {
			hql += " and packaging.id!=:ignoreId";
		}
		Query query = manager.createQuery(hql);
		query.setParameter("itemData", itemData);
		query.setParameter("name", name.toLowerCase());
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

	/**
	 * Read all PackagingUnits of the given itemData. The result is ordered by
	 * the packingLevel and name.
	 */
	@SuppressWarnings("unchecked")
	public List<PackagingUnit> readListByItemData(ItemData itemData) {
		String jpql = "SELECT packaging FROM " + PackagingUnit.class.getName() + " packaging ";
		jpql += " WHERE packaging.itemData=:itemData";
		jpql += " ORDER BY packaging.packingLevel, packaging.name";
		Query query = manager.createQuery(jpql);
		query.setParameter("itemData", itemData);

		return query.getResultList();
	}
	
	/**
	 * Read all PackagingUnits names of the given itemData. The result is ordered by
	 * the packingLevel and name.
	 */
	@SuppressWarnings("unchecked")
	public List<String> readNamesByItemData(ItemData itemData) {
		String jpql = "SELECT packaging.name FROM " + PackagingUnit.class.getName() + " packaging ";
		jpql += " WHERE packaging.itemData=:itemData";
		jpql += " ORDER BY packaging.packingLevel, packaging.name";
		Query query = manager.createQuery(jpql);
		query.setParameter("itemData", itemData);
		List<String> result = query.getResultList();
		return result;
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.<br>
	 * The result is ordered by packingLevel.
	 * 
	 * @param name
	 *            Optional
	 * @param itemData
	 *            Optional
	 * @param offset
	 *            Optional
	 * @param limit
	 *            Optional
	 */
	@SuppressWarnings("unchecked")
	public List<PackagingUnit> readList(ItemData itemData, String name, Integer offset, Integer limit) {
		String jpql = "SELECT entity FROM " + PackagingUnit.class.getName() + " entity ";
		jpql += " WHERE 1=1 ";
		if (itemData != null) {
			jpql += " and entity.itemData=:itemData";
		}
		if (!StringUtils.isBlank(name)) {
			jpql += " and entity.name=:name";
		}
		jpql += " ORDER BY entity.packingLevel, entity.name, entity.id";
		Query query = manager.createQuery(jpql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (!StringUtils.isBlank(name)) {
			query.setParameter("name", name);
		}
		return query.getResultList();
	}

}
