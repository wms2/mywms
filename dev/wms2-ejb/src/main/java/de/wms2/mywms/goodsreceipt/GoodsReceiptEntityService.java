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
package de.wms2.mywms.goodsreceipt;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.entity.PersistenceManager;

/**
 * @author krane
 *
 */
@Stateless
public class GoodsReceiptEntityService {

	@Inject
	private PersistenceManager manager;

	public GoodsReceipt read(String orderNumber) {
		String jpql = "SELECT entity from " + GoodsReceipt.class.getName() + " entity ";
		jpql += " WHERE entity.orderNumber=:orderNumber";
		Query query = manager.createQuery(jpql);
		query.setParameter("orderNumber", orderNumber);
		try {
			return (GoodsReceipt) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public List<GoodsReceipt> readByAdviceLine(AdviceLine adviceLine) {
		return readList(adviceLine, null, null, null, null);
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param adviceLine Optional. AdviceLine is member of of the assigned
	 *                   AdviceLines
	 * @param stateMin   Optional
	 * @param stateMax   Optional
	 * @param offset     Optional
	 * @param limit      Optional
	 */
	@SuppressWarnings("unchecked")
	public List<GoodsReceipt> readList(AdviceLine adviceLine, Integer stateMin, Integer stateMax, Integer offset,
			Integer limit) {

		String jpql = " SELECT entity FROM " + GoodsReceipt.class.getName() + " entity ";
		jpql += " WHERE 1=1";
		if (adviceLine != null) {
			jpql += " and :adviceLine MEMBER entity.adviceLines";
		}
		if (stateMin != null) {
			jpql += " and entity.state>=:stateMin";
		}
		if (stateMax != null) {
			jpql += " and entity.state<=:stateMax";
		}
		jpql += " order by entity.orderNumber";

		Query query = manager.createQuery(jpql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (adviceLine != null) {
			query.setParameter("adviceLine", adviceLine);
		}
		if (stateMin != null) {
			query.setParameter("stateMin", stateMin);
		}
		if (stateMax != null) {
			query.setParameter("stateMax", stateMax);
		}

		return query.getResultList();
	}
}
