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

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.LocationType;

/**
 * @author krane
 *
 */
@Stateless
public class TypeCapacityEntityService {
	@Inject
	private PersistenceManager manager;

	public TypeCapacityConstraint create(LocationType locationType, UnitLoadType unitLoadType, BigDecimal allocation)
			throws BusinessException {
		TypeCapacityConstraint capa = manager.createInstance(TypeCapacityConstraint.class);
		capa.setLocationType(locationType);
		capa.setUnitLoadType(unitLoadType);
		capa.setAllocation(allocation);

		manager.persistValidated(capa);

		return capa;
	}

	public TypeCapacityConstraint read(LocationType locationType, UnitLoadType unitLoadType) {
		String jpql = "SELECT entity FROM " + TypeCapacityConstraint.class.getName() + " entity ";
		jpql += " where entity.locationType=:locationType and entity.unitLoadType=:unitLoadType";

		Query query = manager.createQuery(jpql);
		query.setParameter("locationType", locationType);
		query.setParameter("unitLoadType", unitLoadType);

		try {
			return (TypeCapacityConstraint) query.getSingleResult();
		} catch (NoResultException x) {
		}
		return null;
	}

	public boolean exists(LocationType locationType, UnitLoadType unitLoadType) {
		String jpql = "SELECT entity FROM " + TypeCapacityConstraint.class.getName() + " entity ";
		jpql += " where 1=1";
		if (locationType != null) {
			jpql += " and entity.locationType=:locationType";
		}
		if (unitLoadType != null) {
			jpql += " and entity.unitLoadType=:unitLoadType";
		}

		Query query = manager.createQuery(jpql);
		if (locationType != null) {
			query.setParameter("locationType", locationType);
		}
		if (unitLoadType != null) {
			query.setParameter("unitLoadType", unitLoadType);
		}

		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (Throwable t) {
		}
		return false;
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional. The result is ordered by orderIndex and locationType.
	 * 
	 * @param locationType Optional
	 * @param unitLoadType Optional
	 * @param offset       Optional
	 * @param limit        Optional
	 */
	@SuppressWarnings("unchecked")
	public List<TypeCapacityConstraint> readList(LocationType locationType, UnitLoadType unitLoadType, Integer offset,
			Integer limit) {
		String hql = "SELECT entity FROM " + TypeCapacityConstraint.class.getName() + " entity WHERE 1=1";
		if (locationType != null) {
			hql += " and entity.locationType=:locationType";
		}
		if (unitLoadType != null) {
			hql += " and entity.unitLoadType=:unitLoadType";
		}
		hql += " ORDER BY entity.orderIndex, entity.locationType, entity.id";

		Query query = manager.createQuery(hql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (locationType != null) {
			query.setParameter("locationType", locationType);
		}
		if (unitLoadType != null) {
			query.setParameter("unitLoadType", unitLoadType);
		}

		return query.getResultList();
	}

}
