/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.wms2.mywms.product.ItemData;

@Local
public interface QueryItemDataService {
	
	/**
     * Search for an {@link ItemData} having the specified number
     * and belonging to specified {@link Client}.
     * 
     * @param client the client owning the item data entity
     * @param itemNumber the item number to find
     * @return matching {@link ItemData} or NULL if there is none
     */
    public ItemData getByItemNumber(Client client, String itemNumber);
    
	/**
     * Search for a List of {@link ItemData} having the specified number
     * and belonging to specified {@link Client}.
     * 
     * @param client the client owning the item data entity
     * @param itemNumber the item number to find
     * @return matching {@link ItemData} or NULL if there is none
     */
	public List<ItemData> getListByItemNumber(Client client, String itemNumber);

    /**
     * Search for an {@link ItemData} having the specified number.
     * and belonging to callers {@link Client}.
     * 
     * @param itemNumber the item number to find
     * @return matching {@link ItemData} or NULL if there is none
     */
	public ItemData getByItemNumber(String itemNumber);
	
    /**
     * Search for a List of {@link ItemData} having the specified number.
     * and belonging to callers {@link Client}.
     * 
     * @param itemNumber the item number to find
     * @return matching {@link ItemData} or NULL if there is none
     */
	public List<ItemData> getListByItemNumber(String itemNumber);

	/**
	 * Call this method to get the numbers of all {@link ItemData}s in the system.
	 * For security reasons this will only be allowed for callers that are assigned to the system client.
	 * 
	 * @return List of {@link ClientItemNumberTO}
	 * @throws UnAuthorizedException if the caller is not assigned to the system client.
	 */
	public List<ClientItemNumberTO> getItemNumbers() throws UnAuthorizedException;
	
}
