/*
 * Copyright (c) 2009 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;

/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishOrderServiceBean extends BasicServiceBean<LOSReplenishOrder> implements LOSReplenishOrderService {


    
	public LOSReplenishOrder getByNumber(String number) {
		String queryStr = 
				"SELECT o FROM " + LOSReplenishOrder.class.getSimpleName() + " o " +
				"WHERE o.number=:number";
		
		Query query = manager.createQuery(queryStr);

		query.setParameter("number", number);

		try {
			return (LOSReplenishOrder) query.getSingleResult();
		} catch (Exception ex) {
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<LOSReplenishOrder> getActive(ItemData item, Lot lot, LOSStorageLocation requestedLocation, LOSRack requestedRack) {
		String queryStr = 
				"SELECT o FROM " + LOSReplenishOrder.class.getSimpleName() + " o " +
				"WHERE o.state<:state";
		if( item != null ) {
			queryStr += " and o.itemData=:item ";
		}
		if( lot != null ) {
			queryStr += " and o.lot=:lot ";
		}
		if( requestedLocation != null ) {
			queryStr += " and o.requestedLocation=:requestedLocation ";
		}
		if( requestedRack != null ) {
			queryStr += " and o.requestedRack=:requestedRack ";
		}
		
		Query query = manager.createQuery(queryStr);

		query.setParameter("state", State.FINISHED);
		if( item != null ) {
			query.setParameter("item", item);
		}
		if( lot != null ) {
			query.setParameter("lot", lot);
		}
		if( requestedLocation != null ) {
			query.setParameter("requestedLocation", requestedLocation);
		}
		if( requestedRack != null ) {
			query.setParameter("requestedRack", requestedRack);
		}
		
		return query.getResultList();
	}
	
}
