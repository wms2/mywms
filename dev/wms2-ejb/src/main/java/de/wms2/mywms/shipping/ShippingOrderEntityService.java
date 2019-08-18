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
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.picking.Packet;

/**
 * @author krane
 *
 */
@Stateless
public class ShippingOrderEntityService {

	@Inject
	private PersistenceManager manager;

	public ShippingOrder readByOrderNumber(String orderNumber) {
		String jpql = "SELECT entity from " + ShippingOrder.class.getName() + " entity ";
		jpql += " WHERE entity.orderNumber=:orderNumber";
		Query query = manager.createQuery(jpql);
		query.setParameter("orderNumber", orderNumber);
		try {
			return (ShippingOrder) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ShippingOrder> readByPacket(Packet packet) {
		String jpql = "select distinct entity FROM ";
		jpql += ShippingOrder.class.getName() + " entity, ";
		jpql += ShippingOrderLine.class.getName() + " line ";
		jpql += "where line.shippingOrder = entity";
		jpql += " and line.packet=:packet";
		jpql += " order by entity.orderNumber ";
		Query query = manager.createQuery(jpql);
		query.setParameter("packet", packet);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ShippingOrder> readByDeliveryOrder(DeliveryOrder deliveryOrder) {
		String jpql = "select order FROM ";
		jpql += ShippingOrder.class.getName() + " entity ";
		jpql += "where deliveryOrder = :deliveryOrder";
		jpql += " order by order.orderNumber ";
		Query query = manager.createQuery(jpql);
		query.setParameter("deliveryOrder", deliveryOrder);
		return query.getResultList();
	}

}
