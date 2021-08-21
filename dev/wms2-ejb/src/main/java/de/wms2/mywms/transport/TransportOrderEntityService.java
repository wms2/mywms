/* 
Copyright 2019-2021 Matthias Krane
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
package de.wms2.mywms.transport;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.OrderState;

/**
 * @author krane
 *
 */
@Stateless
public class TransportOrderEntityService {
	@Inject
	private PersistenceManager manager;

	public TransportOrder read(String orderNumber) {
		String jpql = "SELECT entity from " + TransportOrder.class.getName() + " entity ";
		jpql += " WHERE entity.orderNumber=:orderNumber";
		Query query = manager.createQuery(jpql);
		query.setParameter("orderNumber", orderNumber);
		try {
			return (TransportOrder) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public TransportOrder readFirstOpen(UnitLoad unitLoad) {
		List<TransportOrder> orders = readList(unitLoad, null, null, null, null, OrderState.FINISHED - 1);
		if (!orders.isEmpty()) {
			return orders.get(0);
		}
		return null;
	}

	public List<TransportOrder> readOpen(UnitLoad unitLoad) {
		return readList(unitLoad, null, null, null, null, OrderState.FINISHED - 1);
	}

	public List<TransportOrder> readOpen(StorageLocation destination) {
		return readList(null, destination, null, null, null, OrderState.FINISHED - 1);
	}

	public List<TransportOrder> readOpenByField(String field) {
		return readList(null, null, field, null, null, OrderState.FINISHED - 1);
	}

	public List<TransportOrder> readOpenBySection(String section) {
		return readList(null, null, null, section, null, OrderState.FINISHED - 1);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param unitLoad    Optional
	 * @param destination Optional
	 * @param stateMin    Optional
	 * @param stateMax    Optional
	 */
	@SuppressWarnings("unchecked")
	public List<TransportOrder> readList(UnitLoad unitLoad, StorageLocation destination, String field, String section, Integer stateMin,
			Integer stateMax) {

		String jpql = " SELECT entity FROM " + TransportOrder.class.getName() + " entity ";
		jpql += " WHERE 1=1";
		if (unitLoad != null) {
			jpql += " and entity.unitLoad=:unitLoad";
		}
		if (destination != null) {
			jpql += " and entity.destinationLocation=:destinationLocation";
		}
		if (!StringUtils.isBlank(field)) {
			jpql += " and entity.destinationLocation.field=:field";
		}
		if (!StringUtils.isBlank(section)) {
			jpql += " and entity.destinationLocation.section=:section";
		}
		if (stateMin != null) {
			jpql += " and entity.state>=:stateMin";
		}
		if (stateMax != null) {
			jpql += " and entity.state<=:stateMax";
		}
		jpql += " ORDER BY entity.id ";

		Query query = manager.createQuery(jpql);
		if (unitLoad != null) {
			query.setParameter("unitLoad", unitLoad);
		}
		if (destination != null) {
			query.setParameter("destinationLocation", destination);
		}
		if (!StringUtils.isBlank(field)) {
			query.setParameter("field", field);
		}
		if (!StringUtils.isBlank(section)) {
			query.setParameter("section", section);
		}
		if (stateMin != null) {
			query.setParameter("stateMin", stateMin);
		}
		if (stateMax != null) {
			query.setParameter("stateMax", stateMax);
		}

		return query.getResultList();
	}

	public boolean existsUnfinished(UnitLoad unitLoad) {
		String jpql = " SELECT entity.id FROM ";
		jpql += TransportOrder.class.getName() + " entity ";
		jpql += " WHERE entity.unitLoad=:unitLoad and entity.state<:finished";

		Query query = manager.createQuery(jpql);
		query = query.setParameter("unitLoad", unitLoad);
		query = query.setParameter("finished", OrderState.FINISHED);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException t) {
		}

		return false;
	}
}
