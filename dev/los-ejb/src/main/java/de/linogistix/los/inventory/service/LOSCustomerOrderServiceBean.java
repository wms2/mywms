/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.delivery.DeliveryOrder;

/**
 * @author krane
 *
 */
@Stateless
public class LOSCustomerOrderServiceBean extends BasicServiceBean<DeliveryOrder> implements LOSCustomerOrderService {


    public List<DeliveryOrder> getByExternalId(String externalId) {
    	return getByExternalId(null, externalId);
    }
    @SuppressWarnings("unchecked")
	public List<DeliveryOrder> getByExternalId(Client client, String externalId) {
		String queryStr = 
				"SELECT o FROM " + DeliveryOrder.class.getSimpleName() + " o " +
				"WHERE o.externalId=:externalId";
		if( client != null ) {
			queryStr += " and client=:client";
		}
		
		Query query = manager.createQuery(queryStr);

		query.setParameter("externalId", externalId);
		if( client != null ) {
			query.setParameter("client", client);
		}

		return query.getResultList();
    }

    public List<DeliveryOrder> getByExternalNumber(String externalNumber) {
    	return getByExternalNumber(null, externalNumber);
    }
    @SuppressWarnings("unchecked")
	public List<DeliveryOrder> getByExternalNumber(Client client, String externalNumber) {
		String queryStr = 
				"SELECT o FROM " + DeliveryOrder.class.getSimpleName() + " o " +
				"WHERE o.externalNumber=:externalNumber";
		if( client != null ) {
			queryStr += " and client=:client";
		}
		
		Query query = manager.createQuery(queryStr);

		query.setParameter("externalNumber", externalNumber);
		if( client != null ) {
			query.setParameter("client", client);
		}

		return query.getResultList();
    }

	public DeliveryOrder getByNumber(String number) {
		Query q = manager.createQuery(
				"SELECT order FROM " + DeliveryOrder.class.getSimpleName() + " order WHERE order.orderNumber=:number");
		q = q.setParameter("number", number);
        try {
            return (DeliveryOrder) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
	
	public boolean existsByNumber(String number) {
		manager.flush();
		Query q = manager.createQuery(
				"SELECT order.id FROM " + DeliveryOrder.class.getSimpleName() + " order WHERE order.orderNumber=:number");
		q = q.setParameter("number", number);
		int x;
        try {
//            x = q.executeUpdate();
            q.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
//        return x>0;
        return true;
	}
}
