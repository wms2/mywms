/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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

import de.linogistix.los.inventory.model.OrderReceipt;
@Stateless
public class OrderReceiptServiceBean extends BasicServiceBean<OrderReceipt> implements OrderReceiptService{

	public OrderReceipt getByOrderNumber( String orderNumber ) {
		StringBuffer b = new StringBuffer();	
		b.append("SELECT receipt FROM ");
		b.append(OrderReceipt.class.getSimpleName());
		b.append(" receipt ");
		b.append(" WHERE orderNumber=:orderNumber " );
	
		Query query = manager.createQuery(new String(b));
		query.setParameter("orderNumber", orderNumber);

		try{
			return (OrderReceipt) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
