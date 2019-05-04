/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.ItemUnit;
import org.mywms.model.ItemUnitType;

/**
 * @see org.mywms.service.ItemDataService
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class ItemUnitServiceBean
    extends BasicServiceBean<ItemUnit>
    implements ItemUnitService
{

	public ItemUnit getDefault()  {
		ItemUnit unit = manager.find(ItemUnit.class, 0L);
		if( unit != null ) {
			return unit;
		}
		String name = "Pce";
		unit = getByName(name);
		//String name = BundleHelper.resolve(BundleResolver.class, nameKey);
		if(unit == null) {
			unit = new ItemUnit();
			unit.setBaseFactor(1);
			unit.setBaseUnit(null);
			unit.setUnitName(name);
			unit.setUnitType(ItemUnitType.PIECE);
			//String comment = BundleHelper.resolve(BundleResolver.class, "SYSTEM_DATA_COMMENT");
			String comment = "This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it.";
			unit.setAdditionalContent(comment);
			manager.persist(unit);
			manager.flush();
		}
		manager.flush();
		
		String queryStr = "UPDATE " + ItemUnit.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", 0L);
		query.setParameter("idOld", unit.getId());
		query.executeUpdate();
		manager.flush();
		unit = null;

		unit = manager.find(ItemUnit.class, 0L);
		return unit;
	}
	
	public ItemUnit getByName(String name) {
		
		StringBuffer b = new StringBuffer();
		
		b.append(" SELECT iu FROM ");
		b.append(ItemUnit.class.getSimpleName()+" iu ");
		b.append(" WHERE iu.unitName=:uname");
		
		Query q = manager.createQuery(new String(b));
		q = q.setParameter("uname", name);
		try{
			return (ItemUnit) q.getSingleResult();
		} catch (NoResultException ex) {
           return null;
		}
	}
	
}
