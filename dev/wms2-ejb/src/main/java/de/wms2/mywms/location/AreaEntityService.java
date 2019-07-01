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
package de.wms2.mywms.location;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * @author krane
 * 
 */
@Stateless
public class AreaEntityService {

	@Inject
	private PersistenceManager manager;

	public Area create(String name) throws BusinessException {
		Area area = manager.createInstance(Area.class);
		area.setName(name);

		manager.persistValidated(area);

		return area;
	}

	public Area read(String name) {
		String jpql = "SELECT entity FROM " + Area.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			Area entity = (Area) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Read list of Areas used for the given usage
	 */
	@SuppressWarnings("unchecked")
	public List<Area> getFor(String usage) {
		String jpql = "SELECT entity FROM " + Area.class.getName() + " entity ";
		jpql += " WHERE entity.usages like :usage";
		jpql += " ORDER BY entity.name ";
		Query query = manager.createQuery(jpql);
		query.setParameter("usage", "%" + usage + "%");
		return query.getResultList();
	}
}
