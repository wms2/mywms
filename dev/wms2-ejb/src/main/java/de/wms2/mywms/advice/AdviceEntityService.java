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
package de.wms2.mywms.advice;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;

/**
 * @author krane
 *
 */
@Stateless
public class AdviceEntityService {
	@Inject
	private PersistenceManager manager;

	public Advice read(String orderNumber) {
		String jpql = "SELECT entity from " + Advice.class.getName() + " entity ";
		jpql += " WHERE entity.orderNumber=:orderNumber";
		Query query = manager.createQuery(jpql);
		query.setParameter("orderNumber", orderNumber);
		try {
			return (Advice) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param externalNumber Optional
	 * @param minState       Optional
	 * @param maxState       Optional
	 * @param offset         Optional
	 * @param limit          Optional
	 */
	@SuppressWarnings("unchecked")
	public List<Advice> readList(String externalNumber, Integer minState, Integer maxState, Integer offset,
			Integer limit) {

		String jpql = " SELECT entity FROM " + Advice.class.getName() + " entity ";
		jpql += " WHERE 1=1";
		if (externalNumber != null) {
			jpql += " and entity.externalNumber=:externalNumber";
		}
		if (minState != null) {
			jpql += " and entity.state>=:minState";
		}
		if (maxState != null) {
			jpql += " and entity.state<=:maxState";
		}
		jpql += " order by entity.orderNumber";

		Query query = manager.createQuery(jpql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (externalNumber != null) {
			query.setParameter("externalNumber", externalNumber);
		}
		if (minState != null) {
			query.setParameter("minState", minState);
		}
		if (maxState != null) {
			query.setParameter("maxState", maxState);
		}

		return query.getResultList();
	}

}
