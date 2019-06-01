/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

@Stateless
public class QueryLotServiceBean 
	implements QueryLotService, QueryLotServiceRemote 
{

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	public Lot getByNameAndItemData(String name, ItemData item) {
		
		StringBuffer sb = new StringBuffer("SELECT l FROM ");
		sb.append(Lot.class.getSimpleName()+" l ");
		sb.append("WHERE l.name=:na ");
		sb.append("AND l.itemData=:it");
		
		Query query = manager.createQuery(sb.toString());
		
		query.setParameter("na", name);
		query.setParameter("it", item);
		
		try {
            return (Lot) query.getSingleResult();
        }
        catch (NoResultException ex) {
            return null;
        }
		
	}

}
