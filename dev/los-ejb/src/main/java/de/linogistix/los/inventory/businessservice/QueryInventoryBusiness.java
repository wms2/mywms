/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.Map;

import javax.ejb.Local;

import org.mywms.model.Client;

import de.linogistix.los.inventory.exception.InventoryException;
import de.wms2.mywms.product.ItemData;

/**
 * A component for managing {@link LOSInventory} entities and 
 * retrieving inventory information.
 *  
 * @author trautm
 */
@Local
public interface QueryInventoryBusiness {
	
	/**
	 * Returns an array of {@link QueryInventoryTO}. 
	 * One entry per lot i.e. lot if consolidateLot is false. One entry per article otherwise.
	 * 
	 * @param c
	 * @return Array of {@link QueryInventoryTO}
	 * @throws InventoryException 
	 */
	public QueryInventoryTO[] getInventory(Client c, boolean consolidateLot) throws InventoryException;
	public QueryInventoryTO[] getInventory(Client c, boolean consolidateLot, boolean withAmountOnly) throws InventoryException;
	
	/**
	 * Returns an array of {@link QueryInventoryTO}, one entry per lot i.e. lot of given {@link ItemData}.
	 * 
	 * @param c
	 * @return Array of {@link QueryInventoryTO}
	 * @throws InventoryException 
	 */
	public QueryInventoryTO[] getInventory(Client c, ItemData idat, boolean consolidateLot, boolean withAmountOnly) throws InventoryException;
	
	
	/**
	 * Returns one {@link QueryInventoryTO} of given lot.
	 * 
	 * @param c
	 * @return {@link QueryInventoryTO}
	 * @throws InventoryException 
	 */
	public QueryInventoryTO getInventory(Client c, ItemData itemData, String lotNumber, boolean withAmountOnly) throws InventoryException;

	public Map<String, QueryInventoryTO> getInvMap(Client c, String lotNumber, ItemData idat, boolean consolidateLot, boolean withAmountOnly) throws InventoryException ;
		
	
	
}