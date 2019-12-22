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
package de.wms2.mywms.inventory;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Stateless
public class LotEntityService {

	@Inject
	private PersistenceManager manager;

	public Lot create(ItemData itemData, String name, Date lotDate) {
		Lot lot = manager.createInstance(Lot.class);
		lot.setDate(lotDate);
		lot.setItemData(itemData);
		lot.setName(name);

		manager.persist(lot);
		manager.flush();

		return lot;
	}

	public Lot read(ItemData itemData, String name) {
		if (itemData == null) {
			return null;
		}
		if (StringUtils.isBlank(name)) {
			return null;
		}
		String jpql = "SELECT entity FROM " + Lot.class.getName() + " entity ";
		jpql += " WHERE entity.itemData=:itemData and entity.name=:name";
		TypedQuery<Lot> query = manager.createQuery(jpql, Lot.class);
		query.setParameter("itemData", itemData);
		query.setParameter("name", name);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

}
