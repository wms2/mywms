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
package de.wms2.mywms.shipping;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.Packet;

/**
 * @author krane
 *
 */
@Stateless
public class ShippingOrderLineEntityService {

	@Inject
	private PersistenceManager manager;

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param shippingOrder Optional
	 * @param unitLoad      Optional
	 * @param stateMin      Optional
	 * @param stateMax      Optional
	 */
	@SuppressWarnings("unchecked")
	public List<ShippingOrderLine> readList(ShippingOrder shippingOrder, Packet packet, UnitLoad unitLoad,
			Integer stateMin, Integer stateMax) {

		String jpql = " SELECT entity FROM " + ShippingOrderLine.class.getName() + " entity ";
		jpql += " WHERE 1=1";
		if (shippingOrder != null) {
			jpql += " and entity.shippingOrder=:shippingOrder";
		}
		if (packet != null) {
			jpql += " and entity.packet=:packet";
		}
		if (unitLoad != null) {
			jpql += " and entity.packet.unitLoad=:unitLoad";
		}
		if (stateMin != null) {
			jpql += " and entity.state>=:stateMin";
		}
		if (stateMax != null) {
			jpql += " and entity.state<=:stateMax";
		}
		jpql += " ORDER BY entity.id ";
		Query query = manager.createQuery(jpql);
		if (shippingOrder != null) {
			query.setParameter("shippingOrder", shippingOrder);
		}
		if (packet != null) {
			query.setParameter("packet", packet);
		}
		if (unitLoad != null) {
			query.setParameter("unitLoad", unitLoad);
		}
		if (stateMin != null) {
			query.setParameter("stateMin", stateMin);
		}
		if (stateMax != null) {
			query.setParameter("stateMax", stateMax);
		}

		List<ShippingOrderLine> entities = query.getResultList();
		return entities;
	}

	public List<ShippingOrderLine> readListByUnitLoad(UnitLoad unitLoad) {
		return readList(null, null, unitLoad, null, null);
	}

	public List<ShippingOrderLine> readListByPacket(Packet packet) {
		return readList(null, packet, null, null, null);
	}

	public ShippingOrderLine readFirst(ShippingOrder order, UnitLoad unitLoad) {
		List<ShippingOrderLine> lines = readList(order, null, unitLoad, null, null);
		if (lines.size() > 0) {
			return lines.get(0);
		}
		return null;
	}
}
