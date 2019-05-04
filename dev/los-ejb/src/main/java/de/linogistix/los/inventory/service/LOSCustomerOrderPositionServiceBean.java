/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;

/**
 * @author krane
 *
 */
@Stateless
public class LOSCustomerOrderPositionServiceBean extends BasicServiceBean<LOSCustomerOrderPosition> implements LOSCustomerOrderPositionService {

	
	public LOSCustomerOrderPosition getByNumber(String number) {
		Query q = manager.createNamedQuery("LOSCustomerOrderPosition.queryByNumber");
		q = q.setParameter("number", number);
        try {
            return (LOSCustomerOrderPosition) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public boolean existsByNumber(String number) {
		Query q = manager.createNamedQuery("LOSCustomerOrderPosition.idByNumber");
		q = q.setParameter("number", number);
        try {
            q.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
        return true;
    }

}
