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
import de.wms2.mywms.strategy.OrderState;

/**
 * @author krane
 *
 */
@Stateless
public class PacketEntityService {

	@Inject
	private PersistenceManager manager;

	public Packet create(UnitLoad unitLoad) {
		Packet packet = manager.createInstance(Packet.class);
		packet.setClient(unitLoad.getClient());
		packet.setUnitLoad(unitLoad);

		manager.persist(packet);
		manager.flush();

		return packet;
	}

	/**
	 * Read one packet for the label. Only not shipped packets are queried.
	 */
	public Packet readFirstByLabel(String label) {
		return readFirst(null, label, null, null, OrderState.SHIPPED-1);
	}

	/**
	 * Read one packet for the unit load. Only not shipped packets are queried.
	 */
	public Packet readFirstByUnitLoad(UnitLoad unitLoad) {
		return readFirst(unitLoad, null, null, null, OrderState.SHIPPED-1);
	}

	public List<Packet> readByPickingOrder(PickingOrder pickingOrder) {
		return readList(null, null, pickingOrder, null);
	}

	public List<Packet> readByDeliveryOrder(DeliveryOrder order) {
		return readList(null, order, null, null);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 */
	@SuppressWarnings("unchecked")
	public List<Packet> readList(UnitLoad unitLoad, DeliveryOrder deliveryOrder, PickingOrder pickingOrder, Integer maxState) {

		String jpql = " SELECT packet FROM ";
		jpql += Packet.class.getName() + " packet ";
		jpql += " WHERE 1=1";
		if (unitLoad != null) {
			jpql += " and packet.unitLoad=:unitLoad ";
		}
		if (pickingOrder != null) {
			jpql += " and packet.pickingOrder=:pickingOrder ";
		}
		if (deliveryOrder != null) {
			jpql += " and packet.deliveryOrder=:deliveryOrder ";
		}
		if (maxState != null) {
			jpql += " and packet.state<=:maxState ";
		}
		jpql += " order by packet.unitLoad.labelId, packet.id";
		Query query = manager.createQuery(jpql);
		if (unitLoad != null) {
			query = query.setParameter("unitLoad", unitLoad);
		}
		if (pickingOrder != null) {
			query = query.setParameter("pickingOrder", pickingOrder);
		}
		if (deliveryOrder != null) {
			query = query.setParameter("deliveryOrder", deliveryOrder);
		}
		if (maxState != null) {
			query = query.setParameter("maxState", maxState);
		}

		return query.getResultList();
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 */
	public Packet readFirst(UnitLoad unitLoad, String label, DeliveryOrder deliveryOrder,
			PickingOrder pickingOrder, Integer maxState) {

		String jpql = " SELECT packet FROM ";
		jpql += Packet.class.getName() + " packet ";
		jpql += " WHERE 1=1";
		if (unitLoad != null) {
			jpql += " and packet.unitLoad=:unitLoad ";
		}
		if (!StringUtils.isEmpty(label)) {
			jpql += " and packet.unitLoad.labelId=:label";
		}
		if (pickingOrder != null) {
			jpql += " and packet.pickingOrder=:pickingOrder ";
		}
		if (deliveryOrder != null) {
			jpql += " and packet.deliveryOrder=:deliveryOrder ";
		}
		if (maxState != null) {
			jpql += " and packet.state<=:maxState ";
		}
		jpql += " order by packet.state, packet.unitLoad.labelId, packet.id ";

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
		if (maxState != null) {
			query = query.setParameter("maxState", maxState);
		}

		try {
			Packet packet = (Packet) query.getSingleResult();
			return packet;
		} catch (NoResultException e) {
		}

		return null;
	}

	/**
	 * Select whether an entity matches the given criteria. All parameters are
	 * optional.
	 */
	public boolean exists(DeliveryOrder deliveryOrder, Integer minState, Integer maxState) {
		String queryStr = "SELECT entity.id FROM " + Packet.class.getName() + " entity " + "WHERE 1=1";
		if (deliveryOrder != null) {
			queryStr += " and entity.deliveryOrder=:deliveryOrder ";
		}
		if (minState != null) {
			queryStr += " and entity.state>=:minState ";
		}
		if (maxState != null) {
			queryStr += " and entity.state<=:maxState ";
		}

		Query query = manager.createQuery(queryStr);
		query.setMaxResults(1);

		if (deliveryOrder != null) {
			query.setParameter("deliveryOrder", deliveryOrder);
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
