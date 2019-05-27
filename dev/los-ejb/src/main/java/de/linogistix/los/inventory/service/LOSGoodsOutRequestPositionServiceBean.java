/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.wms2.mywms.inventory.UnitLoad;


@Stateless
public class LOSGoodsOutRequestPositionServiceBean extends
		BasicServiceBean<LOSGoodsOutRequestPosition> implements
		LOSGoodsOutRequestPositionService{

	@SuppressWarnings("unchecked")
	public List<LOSGoodsOutRequestPosition> getByUnitLoad(UnitLoad ul) {

		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT pos FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		
		b.append(" WHERE pos.source=:ul ");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("ul", ul);
		
		return q.getResultList();
	}
	
	public LOSGoodsOutRequestPosition getByUnitLoad(LOSGoodsOutRequest out, UnitLoad ul) {

		LOSGoodsOutRequestPosition outPos;
		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT pos FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		
		b.append(" WHERE pos.goodsOutRequest=:out and pos.source=:ul ");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("out", out);
		q = q.setParameter("ul", ul);
		q.setMaxResults(1);
		
		try{
			outPos = (LOSGoodsOutRequestPosition) q.getSingleResult();
			return outPos;
		} catch (Throwable t){
			return null;
		}

	}

}
