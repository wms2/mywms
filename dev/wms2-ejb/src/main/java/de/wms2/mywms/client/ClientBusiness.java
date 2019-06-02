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
package de.wms2.mywms.client;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.wms2.mywms.entity.PersistenceManager;

/**
 * Business service to handle clients
 * 
 * @author krane
 */
@Stateless
public class ClientBusiness {

	@Inject
	private PersistenceManager manager;
	@EJB
	private ClientService clientService;

	/**
	 * Read the special system client. Generate it, if not present.
	 */
	public Client getSystemClient() {

		// Default data is identified by a special id.
		Client system = manager.find(Client.class, 0L);

		if (system == null) {
			String hql = "select entity FROM " + Client.class.getSimpleName() + " entity ";
			hql += "WHERE entity.name=:name or entity.number=:name or entity.code=:name";
			Query query = manager.createQuery(hql);
			query.setParameter("name", "System");
			query.setMaxResults(1);
			try {
				system = (Client) query.getSingleResult();
			} catch (NoResultException ex) {
			}

			if (system == null) {
				system = new Client();

				system.setName("System");
				system.setNumber("System");
				system.setCode("System");

				manager.persist(system);
			}

			manager.flush();

			// this will replace generated id with the special one
			String queryStr = "UPDATE " + Client.class.getName() + " SET id=:idNew WHERE id=:idOld";
			query = manager.createQuery(queryStr);
			query.setParameter("idNew", 0L);
			query.setParameter("idOld", system.getId());
			query.executeUpdate();

			manager.flush();

			// get a clean copy from database
			system = manager.find(Client.class, 0L);
		}

		return system;
	}

}
