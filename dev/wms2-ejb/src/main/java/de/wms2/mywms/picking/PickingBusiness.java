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
package de.wms2.mywms.picking;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;

/**
 * @author krane
 *
 */
@Stateless
public class PickingBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;

	/**
	 * Calculate the weight of the given order.
	 */
	public BigDecimal calculateWeight(PickingOrder pickingOrder) {
		if (pickingOrder == null) {
			return null;
		}

		String jpql = "SELECT sum(itemData.weight*line.amount) FROM ";
		jpql += ItemData.class.getName() + " itemData, ";
		jpql += PickingOrderLine.class.getName() + " line ";
		jpql += "WHERE line.pickingOrder=:order ";
		jpql += " AND itemData=line.itemData";
		jpql += " AND line.state<" + OrderState.PICKED;

		Query query = manager.createQuery(jpql);
		query.setParameter("order", pickingOrder);
		BigDecimal weight1 = (BigDecimal) query.getSingleResult();
		if (weight1 == null) {
			weight1 = BigDecimal.ZERO;
		}

		jpql = "SELECT sum(itemData.weight*line.pickedAmount) FROM ";
		jpql += ItemData.class.getName() + " itemData, ";
		jpql += PickingOrderLine.class.getName() + " line ";
		jpql += "WHERE line.pickingOrder=:order ";
		jpql += " AND itemData=line.itemData";
		jpql += " AND line.state<" + OrderState.DELETABLE;
		jpql += " AND line.state!=" + OrderState.CANCELED;
		jpql += " AND line.state>=" + OrderState.PICKED;

		query = manager.createQuery(jpql);
		query.setParameter("order", pickingOrder);
		BigDecimal weight2 = (BigDecimal) query.getSingleResult();
		if (weight2 == null) {
			weight2 = BigDecimal.ZERO;
		}

		BigDecimal weight = weight1.add(weight2);
		if (weight.compareTo(BigDecimal.ZERO) == 0) {
			weight = null;
		}

		return weight;
	}

	/**
	 * Calculate the volume of the given order.
	 */
	public BigDecimal calculateVolume(PickingOrder pickingOrder) {
		logger.info("calculateWeightAndVolume pickingOrder=" + pickingOrder);

		if (pickingOrder == null) {
			return null;
		}

		String jpql = "SELECT sum(itemData.height*itemData.width*itemData.depth*line.amount) FROM ";
		jpql += ItemData.class.getName() + " itemData, ";
		jpql += PickingOrderLine.class.getName() + " line ";
		jpql += "WHERE line.pickingOrder=:order ";
		jpql += " AND itemData=line.itemData";
		jpql += " AND line.state<" + OrderState.PICKED;

		Query query = manager.createQuery(jpql);
		query.setParameter("order", pickingOrder);
		BigDecimal volume1 = (BigDecimal) query.getSingleResult();
		if (volume1 == null) {
			volume1 = BigDecimal.ZERO;
		}

		jpql = "SELECT sum(itemData.height*itemData.width*itemData.depth*line.pickedAmount) FROM ";
		jpql += ItemData.class.getName() + " itemData, ";
		jpql += PickingOrderLine.class.getName() + " line ";
		jpql += "WHERE line.pickingOrder=:order ";
		jpql += " AND itemData=line.itemData";
		jpql += " AND line.state<" + OrderState.DELETABLE;
		jpql += " AND line.state!=" + OrderState.CANCELED;
		jpql += " AND line.state>=" + OrderState.PICKED;

		query = manager.createQuery(jpql);
		query.setParameter("order", pickingOrder);
		BigDecimal volume2 = (BigDecimal) query.getSingleResult();
		if (volume2 == null) {
			volume2 = BigDecimal.ZERO;
		}

		BigDecimal volume = volume1.add(volume2);
		if (volume.compareTo(BigDecimal.ZERO) == 0) {
			volume = null;
		}

		if (volume == null) {
			return volume;
		}
		return volume.stripTrailingZeros();
	}

}
