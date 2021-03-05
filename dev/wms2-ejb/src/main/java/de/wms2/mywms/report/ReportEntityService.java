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
package de.wms2.mywms.report;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.mywms.model.Client;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * @author krane
 *
 */
@Stateless
public class ReportEntityService {
	@Inject
	private PersistenceManager manager;

	public Report create(String name, Client client, String reportVersion) throws BusinessException {
		Report entity = manager.createInstance(Report.class);
		entity.setClient(client);
		entity.setName(name);
		entity.setReportVersion(reportVersion);

		manager.persistValidated(entity);

		return entity;
	}

	public List<Report> readList(String name, Client client) {
		String jpql = "SELECT entity FROM " + Report.class.getName() + " entity where 1=1 ";
		if (client != null) {
			jpql += " AND entity.client = :client ";
		}
		if (name != null) {
			jpql += " AND entity.name = :name ";
		}
		jpql += " order by entity.reportVersion";
		TypedQuery<Report> query = manager.createQuery(jpql, Report.class);
		if (client != null) {
			query.setParameter("client", client);
		}
		if (name != null) {
			query.setParameter("name", name);
		}

		return query.getResultList();
	}

}
