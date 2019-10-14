/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.wms2.mywms.product.ItemData;

@Remote
public interface QueryItemDataServiceRemote {
	
	/**
     * Search for an {@link ItemData} having the specified number
     * and belonging to specified {@link Client}.
     * 
     * @param client the client owning the item data entity
     * @param itemNumber the item number to find
     * @return matching {@link ItemData} or NULL if there is none
     */
    ItemData getByItemNumber(Client client, String itemNumber);

    /**
     * Search for an {@link ItemData} having the specified number.
     * and belonging to callers {@link Client}.
     * 
     * @param itemNumber the item number to find
     * @return matching {@link ItemData} or NULL if there is none
     */
	public ItemData getByItemNumber(String itemNumber);

}
