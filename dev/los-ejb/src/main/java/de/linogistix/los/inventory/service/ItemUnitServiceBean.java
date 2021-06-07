/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.product.ItemUnit;

/**
 * @see de.linogistix.los.inventory.service.ItemDataService
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class ItemUnitServiceBean extends BasicServiceBean<ItemUnit> implements ItemUnitService {

	public ItemUnit getByName(String name) {

		StringBuffer b = new StringBuffer();

		b.append(" SELECT iu FROM ");
		b.append(ItemUnit.class.getSimpleName() + " iu ");
		b.append(" WHERE iu.name=:uname");

		Query q = manager.createQuery(new String(b));
		q = q.setParameter("uname", name);
		try {
			return (ItemUnit) q.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

}
