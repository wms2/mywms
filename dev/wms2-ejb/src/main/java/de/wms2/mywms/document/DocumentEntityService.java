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
package de.wms2.mywms.document;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;

/**
 * Service to handle basic db access of an entity
 * 
 * @author krane
 *
 */
@Stateless
public class DocumentEntityService {
	@Inject
	private PersistenceManager manager;

	public Document create(String name, String mimeType, byte[] content) {
		Document document = manager.createInstance(Document.class);
		document.setData(content);
		document.setDocumentType(mimeType);
		document.setName(name);

		manager.persist(document);

		return document;
	}

	public Document readByName(String name) {
		String jpql = "SELECT entity FROM " + Document.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			Document entity = (Document) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	public Document readFirstByNamePrefix(String namePrefix) {
		String jpql = "SELECT entity FROM " + Document.class.getName() + " entity ";
		jpql += " where entity.name like :name";
		jpql += " order by entity.id";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", namePrefix + "%");
		query.setMaxResults(1);
		try {
			Document entity = (Document) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	public int delete(String name) {
		String jpql = "delete FROM " + Document.class.getName() + " entity ";
		jpql += " where entity.name like :name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		int num = query.executeUpdate();

		return num;
	}

	public int deleteByNamePrefix(String namePrefix) {
		String jpql = "delete FROM " + Document.class.getName() + " entity ";
		jpql += " where entity.name like :name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", namePrefix + "%");
		int num = query.executeUpdate();

		return num;
	}

}
