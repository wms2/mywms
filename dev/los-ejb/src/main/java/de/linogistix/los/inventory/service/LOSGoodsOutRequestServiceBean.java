/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.model.UnitLoad;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;


@Stateless
public class LOSGoodsOutRequestServiceBean extends
		BasicServiceBean<LOSGoodsOutRequest> implements
		LOSGoodsOutRequestService {
//	private static final Logger log = Logger.getLogger(LOSGoodsOutRequestServiceBean.class);

	@EJB 
	private LOSGoodsOutRequestPositionService posService;
	
	@SuppressWarnings("unchecked")
	public List<LOSGoodsOutRequest> getByUnitLoad(UnitLoad ul) {

		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT out FROM ");
		b.append(LOSGoodsOutRequest.class.getName());
		b.append(" out, ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		
		b.append(" WHERE pos.source=:ul ");
		b.append(" AND pos.goodsOutRequest=out ");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("ul", ul);
		
		return q.getResultList();
	}
	
	
	@Override
	public void delete(LOSGoodsOutRequest entity) throws ConstraintViolatedException {
		
		entity.setPositions(new ArrayList<LOSGoodsOutRequestPosition>());
		super.delete(entity);
		
		for (LOSGoodsOutRequestPosition pos : entity.getPositions()){
			posService.delete(pos);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSGoodsOutRequest> getByCustomerOrder(LOSCustomerOrder order) {
		StringBuffer b = new StringBuffer();
		Query q;

		b.append(" SELECT DISTINCT out FROM ");
		b.append(LOSGoodsOutRequest.class.getName());
		b.append(" out ");
		
		b.append(" WHERE out.customerOrder = :order");
		
		q = manager.createQuery(new String(b));
		q = q.setParameter("order", order);
		return q.getResultList();
	}
	
	public LOSGoodsOutRequest getByNumber(String number) {
		return getByNumber(null, number);
	}

	public LOSGoodsOutRequest getByNumber(Client client, String number) {
		String queryStr = 
				"SELECT o FROM " + LOSGoodsOutRequest.class.getSimpleName() + " o " +
				"WHERE o.number=:number";
		if( client != null ) {
			queryStr += " and client=:client";
		}
		
		Query query = manager.createQuery(queryStr);

		query.setParameter("number", number);
		if( client != null ) {
			query.setParameter("client", client);
		}

		try {
			return (LOSGoodsOutRequest) query.getSingleResult();
		} catch (Exception ex) {
//			log.error("Error reading daata: "+ex.getClass().getSimpleName()+", "+ex.getMessage());
		}
		return null;
	}

}
