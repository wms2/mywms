/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;

@Stateless
public class QueryStockTakingOrderServiceBean implements
		QueryStockTakingOrderService {

		
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.stocktaking.service.QueryStockTakingOrderService#getListByLocationAndState(java.lang.String, de.linogistix.los.stocktaking.model.LOSStocktakingState)
	 */
	@SuppressWarnings("unchecked")
	public List<LOSStocktakingOrder> getListByLocationAndState(String location,
															   LOSStocktakingState... states) 
	    throws UnAuthorizedException 
	{
		
		StringBuffer sb = new StringBuffer("SELECT so FROM ");
		sb.append(LOSStocktakingOrder.class.getSimpleName()+" so ");
		sb.append("WHERE so.locationName=:loc ");
		
		if(states.length>0){
			sb.append("AND ( so.state=:s0 ");
		}
		
		for(int i=1;i<states.length;i++){
			sb.append(" OR so.state=:s"+i);
		}
		
		if(states.length>0){
			sb.append(" )");
		}
		
		Query query = manager.createQuery(sb.toString());
		
		query.setParameter("loc", location);
		
		int y = 0;
		for(LOSStocktakingState s:states){
			query.setParameter("s"+y, s);
			y++;
		}
				
		return query.getResultList();
	}

}
