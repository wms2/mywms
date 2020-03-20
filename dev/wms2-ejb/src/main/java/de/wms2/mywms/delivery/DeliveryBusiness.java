/* 
Copyright 2020 Matthias Krane

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
package de.wms2.mywms.delivery;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Stateless
public class DeliveryBusiness {
	@Inject
	private PersistenceManager manager;

	/**
	 * Calculate the weight of the given order.
	 */
	public BigDecimal calculateWeight(DeliveryOrder deliveryOrder) {
		String jpql = "SELECT sum(itemData.weight*line.amount) FROM ";
		jpql += ItemData.class.getName() + " itemData, ";
		jpql += DeliveryOrderLine.class.getName() + " line ";
		jpql += "WHERE line.deliveryOrder=:order ";
		jpql += " AND itemData=line.itemData";

		Query query = manager.createQuery(jpql);
		query.setParameter("order", deliveryOrder);
		BigDecimal weight = (BigDecimal) query.getSingleResult();

		return weight;
	}

	/**
	 * Calculate the volume of the given order.
	 */
	public BigDecimal calculateVolume(DeliveryOrder deliveryOrder) {
		String jpql = "SELECT sum(itemData.height*itemData.width*itemData.depth*line.amount)  FROM ";
		jpql += ItemData.class.getName() + " itemData, ";
		jpql += DeliveryOrderLine.class.getName() + " line ";
		jpql += "WHERE line.deliveryOrder=:order ";
		jpql += " AND itemData=line.itemData";

		Query query = manager.createQuery(jpql);
		query.setParameter("order", deliveryOrder);
		BigDecimal volume = (BigDecimal) query.getSingleResult();

		if (volume == null) {
			return volume;
		}
		return volume.stripTrailingZeros();
	}
}
