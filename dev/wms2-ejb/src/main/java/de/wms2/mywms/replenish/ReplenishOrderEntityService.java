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
package de.wms2.mywms.replenish;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;

/**
 * @author krane
 *
 */
@Stateless
public class ReplenishOrderEntityService {

	@Inject
	private PersistenceManager manager;

	public ReplenishOrder read(String orderNumber) {
		String jpql = "SELECT entity from " + ReplenishOrder.class.getName() + " entity ";
		jpql += " WHERE entity.orderNumber=:orderNumber";
		Query query = manager.createQuery(jpql);
		query.setParameter("orderNumber", orderNumber);
		try {
			return (ReplenishOrder) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Searches a List of all ReplenishOrders that have the given source UnitLoad
	 * and are not finished
	 */
	@SuppressWarnings("unchecked")
	public List<ReplenishOrder> readActiveBySourceUnitLoad(UnitLoad sourceUnitLoad) {
		String queryStr = "select replenishOrder from " + ReplenishOrder.class.getName() + " replenishOrder,";
		queryStr += StockUnit.class.getName() + " stock";
		queryStr += " where stock.id=replenishOrder.sourceStockUnitId";
		queryStr += " and stock.unitLoad=:unitLoad";
		queryStr += " and replenishOrder.state<:finished";
		queryStr += " order by replenishOrder.orderNumber";

		Query query = manager.createQuery(queryStr);

		query.setParameter("unitLoad", sourceUnitLoad);
		query.setParameter("finished", OrderState.FINISHED);

		return query.getResultList();
	}

	public List<ReplenishOrder> readOpenByDestination(StorageLocation destination) {
		return readList(null, destination, null, OrderState.FINISHED - 1, false, null, null);

	}

	/**
	 * Select a list of entities matching the given criteria.
	 * 
	 * @param itemData    Optional
	 * @param destination Optional
	 * @param minState    Optional
	 * @param maxState    Optional
	 * @param sort        If true, the result is ordered by state and orderNumber
	 * @param offset      Optional
	 * @param limit       Optional
	 */
	// TODO krane aufrufe pruefen
	@SuppressWarnings("unchecked")
	public List<ReplenishOrder> readList(ItemData itemData, StorageLocation destination, Integer minState,
			Integer maxState, boolean sort, Integer offset, Integer limit) {
		String queryStr = "SELECT entity FROM " + ReplenishOrder.class.getName() + " entity " + "WHERE 1=1";
		if (itemData != null) {
			queryStr += " and entity.itemData=:itemData ";
		}
		if (destination != null) {
			queryStr += " and entity.destinationLocation=:destinationLocation ";
		}
		if (minState != null) {
			queryStr += " and entity.state>=:minState ";
		}
		if (maxState != null) {
			queryStr += " and entity.state<=:maxState ";
		}
		if (sort) {
			queryStr += "ORDER by entity.state, entity.orderNumber";
		}

		Query query = manager.createQuery(queryStr);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (destination != null) {
			query.setParameter("destinationLocation", destination);
		}
		if (minState != null) {
			query.setParameter("minState", minState);
		}
		if (maxState != null) {
			query.setParameter("maxState", maxState);
		}

		return query.getResultList();
	}

	/**
	 * Select whether an entity matches the given criteria. All parameters are
	 * optional.
	 * 
	 * @param itemData    Optional
	 * @param destination Optional
	 * @param minState    Optional
	 * @param maxState    Optional
	 */
	public boolean exists(ItemData itemData, StorageLocation destination, Integer minState, Integer maxState) {
		String queryStr = "SELECT entity.id FROM " + ReplenishOrder.class.getName() + " entity " + "WHERE 1=1";
		if (itemData != null) {
			queryStr += " and entity.itemData=:itemData ";
		}
		if (destination != null) {
			queryStr += " and entity.destinationLocation=:destinationLocation ";
		}
		if (minState != null) {
			queryStr += " and entity.state>=:minState ";
		}
		if (maxState != null) {
			queryStr += " and entity.state<=:maxState ";
		}

		Query query = manager.createQuery(queryStr);
		query.setMaxResults(1);

		if (itemData != null) {
			query.setParameter("itemData", itemData);
		}
		if (destination != null) {
			query.setParameter("destinationLocation", destination);
		}
		if (minState != null) {
			query.setParameter("minState", minState);
		}
		if (maxState != null) {
			query.setParameter("maxState", maxState);
		}

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}

}
