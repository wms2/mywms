/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.pick.model.PickReceipt;

@Stateless
public class PickReceiptServiceBean extends BasicServiceBean<PickReceipt> implements PickReceiptService{
	public PickReceipt getByLabelId(String labelId) {
		
		StringBuffer b = new StringBuffer();
		
		b.append("SELECT o from ");
		b.append(PickReceipt.class.getSimpleName());
		b.append(" o ");
		b.append(" WHERE o.labelID=:labelId ");
		
		
		Query q = manager.createQuery(new String(b));
		q = q.setParameter("labelId", labelId);
		
		try{
			return (PickReceipt) q.getSingleResult();
		} catch (NoResultException ex){
		} catch (NonUniqueResultException ex){
		}
		return null;
	}
}
