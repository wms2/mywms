/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.service;

import java.util.List;

import javax.ejb.Local;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;

@Local
public interface QueryStockTakingOrderService {

	/**
	 * Search for {@link LOSStocktakingOrder}s that have a certain state and are targeting the specified location.
	 * For security reasons this will only be allowed for callers who belong to the system client.
	 * 
	 * @param location target location to search for
	 * @param state limit result to {@link LOSStocktakingOrder}s of a certain state.
	 * @return list of {@link LOSStocktakingOrder}s or sub classes.
	 * @throws UnAuthorizedException
	 */
	public List<LOSStocktakingOrder> getListByLocationAndState(String location, LOSStocktakingState... state) throws UnAuthorizedException;
	
}
