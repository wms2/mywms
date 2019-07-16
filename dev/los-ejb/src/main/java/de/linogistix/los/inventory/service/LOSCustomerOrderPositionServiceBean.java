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

import de.wms2.mywms.delivery.DeliveryOrderLine;

/**
 * @author krane
 *
 */
@Stateless
public class LOSCustomerOrderPositionServiceBean extends BasicServiceBean<DeliveryOrderLine> implements LOSCustomerOrderPositionService {

	
	public DeliveryOrderLine getByNumber(String number) {
		Query q = manager.createQuery(
				"SELECT pos FROM " + DeliveryOrderLine.class.getSimpleName() + " pos WHERE pos.lineNumber=:number");
		q = q.setParameter("number", number);
        try {
            return (DeliveryOrderLine) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public boolean existsByNumber(String number) {
		Query q = manager.createQuery(
				"SELECT pos.id FROM " + DeliveryOrderLine.class.getSimpleName() + " pos WHERE pos.lineNumber=:number");
		q = q.setParameter("number", number);
        try {
            q.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
        return true;
    }

}
