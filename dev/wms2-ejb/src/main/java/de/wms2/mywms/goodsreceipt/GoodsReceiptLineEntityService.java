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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.inventory.UnitLoad;

/**
 * @author krane
 *
 */
@Stateless
public class GoodsReceiptLineEntityService {
	@Inject
	private PersistenceManager manager;

	public GoodsReceiptLine readFirstByUnitLoad(UnitLoad unitLoad) {
		if (unitLoad == null) {
			return null;
		}
		String jpql = "SELECT entity FROM " + GoodsReceiptLine.class.getName() + " entity ";
		jpql += " where entity.unitLoadLabelId=:label";
		jpql += " order by entity.id desc";
		Query query = manager.createQuery(jpql);
		query.setParameter("label", unitLoad.getLabelId());
		query.setMaxResults(1);

		try {
			GoodsReceiptLine line = (GoodsReceiptLine) query.getSingleResult();
			return line;
		} catch (Throwable t) {
		}
		return null;
	}
}
