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

import de.linogistix.los.inventory.model.LOSCustomerOrder;

/**
 * @author krane
 *
 */
@Stateless
public class LOSCustomerOrderServiceBean extends BasicServiceBean<LOSCustomerOrder> implements LOSCustomerOrderService {


    public List<LOSCustomerOrder> getByExternalId(String externalId) {
    	return getByExternalId(null, externalId);
    }
    @SuppressWarnings("unchecked")
	public List<LOSCustomerOrder> getByExternalId(Client client, String externalId) {
		String queryStr = 
				"SELECT o FROM " + LOSCustomerOrder.class.getSimpleName() + " o " +
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

    public List<LOSCustomerOrder> getByExternalNumber(String externalNumber) {
    	return getByExternalNumber(null, externalNumber);
    }
    @SuppressWarnings("unchecked")
	public List<LOSCustomerOrder> getByExternalNumber(Client client, String externalNumber) {
		String queryStr = 
				"SELECT o FROM " + LOSCustomerOrder.class.getSimpleName() + " o " +
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

	public LOSCustomerOrder getByNumber(String number) {
		Query q = manager.createNamedQuery("LOSCustomerOrder.queryByNumber");
		q = q.setParameter("number", number);
        try {
            return (LOSCustomerOrder) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
	
	public boolean existsByNumber(String number) {
		manager.flush();
		Query q = manager.createNamedQuery("LOSCustomerOrder.idByNumber");
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
