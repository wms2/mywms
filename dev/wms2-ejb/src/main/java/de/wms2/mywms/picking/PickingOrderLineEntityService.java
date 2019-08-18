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
import javax.persistence.Query;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.inventory.StockUnit;

/**
 * @author krane
 *
 */
@Stateless
public class PickingOrderLineEntityService {

	@Inject
	private PersistenceManager manager;

	public List<PickingOrderLine> readByDeliveryOrderLine(DeliveryOrderLine deliveryOrderLine) {
		return readList(null, null, null, deliveryOrderLine, null, null, null);
	}

	public List<PickingOrderLine> readByDeliveryOrder(DeliveryOrder deliveryOrder) {
		return readList(null, null, deliveryOrder, null, null, null, null);
	}

	public List<PickingOrderLine> readByPacket(Packet packet) {
		return readList(null, null, null, null, packet, null, null);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 */
	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> readList(StockUnit sourceStockUnit, PickingOrder pickingOrder,
			DeliveryOrder deliveryOrder, DeliveryOrderLine deliveryOrderLine, Packet paket, Integer stateMin, Integer stateMax) {

		String jpql = " SELECT entity FROM " + PickingOrderLine.class.getName() + " entity ";
		jpql += " WHERE 1=1";
		if (sourceStockUnit != null) {
			jpql += " and entity.sourceStockUnit=:sourceStockUnit";
		}
		if (pickingOrder != null) {
			jpql += " and entity.pickingOrder=:pickingOrder";
		}
		if (deliveryOrderLine != null) {
			jpql += " and entity.deliveryOrderLine=:deliveryOrderLine";
		}
		if (deliveryOrder != null) {
			jpql += " and entity.deliveryOrderLine.deliveryOrder=:deliveryOrder";
		}
		if (stateMin != null) {
			jpql += " and entity.state>=:stateMin";
		}
		if (stateMax != null) {
			jpql += " and entity.state<=:stateMax";
		}
		jpql += " ORDER BY entity.id ";
		Query query = manager.createQuery(jpql);
		if (sourceStockUnit != null) {
			query.setParameter("sourceStockUnit", sourceStockUnit);
		}
		if (pickingOrder != null) {
			query.setParameter("pickingOrder", pickingOrder);
		}
		if (deliveryOrderLine != null) {
			query.setParameter("deliveryOrderLine", deliveryOrderLine);
		}
		if (deliveryOrder != null) {
			query.setParameter("deliveryOrder", deliveryOrder);
		}
		if (stateMin != null) {
			query.setParameter("stateMin", stateMin);
		}
		if (stateMax != null) {
			query.setParameter("stateMax", stateMax);
		}

		List<PickingOrderLine> entities = query.getResultList();
		return entities;
	}

}
