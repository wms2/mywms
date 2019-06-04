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
package de.wms2.mywms.user;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.User;

import de.wms2.mywms.entity.PersistenceManager;

/**
 * Service to handle basic db access of an entity
 * 
 * @author krane
 *
 */
@Stateless
public class UserEntityService {
	@Inject
	private PersistenceManager manager;

	public User read(String name) {
		String jpql = "SELECT entity FROM " + User.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			User entity = (User) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	public User readIgnoreCase(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		User user = read(name);
		if (user != null) {
			return user;
		}

		String jpql = "SELECT entity from " + User.class.getName() + " entity ";
		jpql += " WHERE lower(name)=:name";

		Query query = manager.createQuery(jpql);
		query.setParameter("name", name.toLowerCase());
		try {
			return (User) query.getSingleResult();
		} catch (NoResultException | NonUniqueResultException x) {
		}
		return null;
	}

	public boolean existsIgnoreCase(String name) {
		if (StringUtils.isBlank(name)) {
			return false;
		}

		String jpql = "SELECT entity.id from " + User.class.getName() + " entity ";
		jpql += " WHERE lower(name)=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name.toLowerCase());
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}
}
