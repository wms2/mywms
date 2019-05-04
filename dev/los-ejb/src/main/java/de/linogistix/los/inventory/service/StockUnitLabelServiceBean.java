/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.StockUnitLabel;

/**
 *
 * @author trautm
 */
@Stateless
public class StockUnitLabelServiceBean extends BasicServiceBean<StockUnitLabel> implements StockUnitLabelService  {

	public StockUnitLabel getByLabelId(String labelId) {
		
		StringBuffer b = new StringBuffer();
		
		b.append("SELECT o from ");
		b.append(StockUnitLabel.class.getSimpleName());
		b.append(" o ");
		b.append(" WHERE o.labelID=:labelID ");
		
		
		Query q = manager.createQuery(new String(b));
		q = q.setParameter("labelID", labelId);
		
		try{
			StockUnitLabel label = (StockUnitLabel) q.getSingleResult();
			return label;
		} catch (NoResultException ex){
			return null;
		} catch (NonUniqueResultException ex){
			return null;
		}		
	}
}
