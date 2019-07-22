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
package de.wms2.mywms.picking;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadPackageType;

/**
 * @author krane
 *
 */
@Stateless
public class PickingUnitLoadEntityService {

	@Inject
	private PersistenceManager manager;

	public PickingUnitLoad create(UnitLoad unitLoad) {
		PickingUnitLoad pickingUnitLoad = manager.createInstance(PickingUnitLoad.class);
		pickingUnitLoad.setClient(unitLoad.getClient());
		pickingUnitLoad.setUnitLoad(unitLoad);

		unitLoad.setPackageType(UnitLoadPackageType.MIXED_CONSOLIDATE);

		manager.persist(pickingUnitLoad);
		manager.flush();

		return pickingUnitLoad;
	}

	public PickingUnitLoad getByLabel(String label) {
		return readFirst(null, label, null, null);
	}

	public List<PickingUnitLoad> getByPickingOrder(PickingOrder pickingOrder) {
		return readList(null, null, pickingOrder, null, null);
	}

	public List<PickingUnitLoad> getByDeliveryOrder(DeliveryOrder order) {
		return readList(null, order, null, null, null);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param unitLoad      Optional
	 * @param deliveryOrder Optional
	 * @param pickingOrder  Optional
	 * @param offset        Optional
	 * @param limit         Optional
	 */
	@SuppressWarnings("unchecked")
	public List<PickingUnitLoad> readList(UnitLoad unitLoad, DeliveryOrder deliveryOrder, PickingOrder pickingOrder,
			Integer offset, Integer limit) {

		String jpql = " SELECT pickingUnitLoad FROM ";
		jpql += PickingUnitLoad.class.getName() + " pickingUnitLoad ";
		jpql += " WHERE 1=1";
		if (unitLoad != null) {
			jpql += " and pickingUnitLoad.unitLoad=:unitLoad ";
		}
		if (pickingOrder != null) {
			jpql += " and pickingUnitLoad.pickingOrder=:pickingOrder ";
		}
		if (deliveryOrder != null) {
			jpql += " and pickingUnitLoad.deliveryOrder=:deliveryOrder ";
		}
		jpql += " order by pickingUnitLoad.unitLoad.labelId";
		Query query = manager.createQuery(jpql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (unitLoad != null) {
			query = query.setParameter("unitLoad", unitLoad);
		}
		if (pickingOrder != null) {
			query = query.setParameter("pickingOrder", pickingOrder);
		}
		if (deliveryOrder != null) {
			query = query.setParameter("deliveryOrder", deliveryOrder);
		}

		return query.getResultList();
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional. If no result is found an additional query for entities without
	 * pickingOrder or deliveryOrder is done. The result is ordered by the state.
	 * 
	 * @param unitLoad      Optional
	 * @param label         Optional
	 * @param deliveryOrder Optional
	 * @param pickingOrder  Optional
	 * @return
	 */
	public PickingUnitLoad readFirst(UnitLoad unitLoad, String label, DeliveryOrder deliveryOrder,
			PickingOrder pickingOrder) {

		String jpql = " SELECT pickingUnitLoad FROM ";
		jpql += PickingUnitLoad.class.getName() + " pickingUnitLoad ";
		jpql += " WHERE 1=1";
		if (unitLoad != null) {
			jpql += " and pickingUnitLoad.unitLoad=:unitLoad ";
		}
		if (!StringUtils.isEmpty(label)) {
			jpql += " and pickingUnitLoad.unitLoad.labelId=:label";
		}
		if (pickingOrder != null) {
			jpql += " and pickingUnitLoad.pickingOrder=:pickingOrder ";
		}
		if (deliveryOrder != null) {
			jpql += " and pickingUnitLoad.deliveryOrder=:deliveryOrder ";
		}
		jpql += " order by pickingUnitLoad.state, pickingUnitLoad.unitLoad.labelId, pickingUnitLoad.id ";

		Query query = manager.createQuery(jpql);
		query.setMaxResults(1);
		if (unitLoad != null) {
			query = query.setParameter("unitLoad", unitLoad);
		}
		if (!StringUtils.isEmpty(label)) {
			query = query.setParameter("label", label);
		}
		if (pickingOrder != null) {
			query = query.setParameter("pickingOrder", pickingOrder);
		}
		if (deliveryOrder != null) {
			query = query.setParameter("deliveryOrder", deliveryOrder);
		}

		try {
			PickingUnitLoad cart = (PickingUnitLoad) query.getSingleResult();
			return cart;
		} catch (NoResultException e) {
		}

		// try to find cart with not assigned requested order
		jpql = " SELECT pickingUnitLoad FROM ";
		jpql += PickingUnitLoad.class.getName() + " pickingUnitLoad ";
		jpql += " WHERE 1=1";
		if (unitLoad != null) {
			jpql += " and pickingUnitLoad.unitLoad=:unitLoad ";
		}
		if (!StringUtils.isEmpty(label)) {
			jpql += " and pickingUnitLoad.unitLoad.labelId=:label";
		}
		if (pickingOrder != null) {
			jpql += " and pickingUnitLoad.pickingOrder is null ";
		}
		if (deliveryOrder != null) {
			jpql += " and pickingUnitLoad.deliveryOrder is null ";
		}
		jpql += " order by pickingUnitLoad.state, pickingUnitLoad.id ";

		query = manager.createQuery(jpql);
		query.setMaxResults(1);
		if (unitLoad != null) {
			query = query.setParameter("unitLoad", unitLoad);
		}
		if (!StringUtils.isEmpty(label)) {
			query = query.setParameter("label", label);
		}

		try {
			PickingUnitLoad cart = (PickingUnitLoad) query.getSingleResult();
			return cart;
		} catch (NoResultException e) {
		}

		return null;
	}

}
