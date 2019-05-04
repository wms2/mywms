/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Zone;

/**
 * This interface declares the service for the entity ItemData. For this
 * service it is save to call the <code>get(String name)</code>
 * method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Local
public interface ItemDataService
    extends BasicService<ItemData>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Returns a list of ItemDatas, matching the specified zone.
     * 
     * @return list of ItemDatas
     */
    List<ItemData> getListByZone(Zone zone);

    /**
     * Checks the specified number for being already given away. If the
     * number is valid, a new ItemData will be created, assigned to
     * Client client and added to persistence context.
     * 
     * @param client the owning client of the new ItemData.
     * @param number the number of the new ItemData.
     * @return a persistent instance of ItemData.
     * @throws UniqueConstraintViolatedException if Client client has
     *             already an ItemData with Number number.
     * @throws NullPointerException if client or number is null.
     */
    ItemData create(Client client, String number)
        throws UniqueConstraintViolatedException;

    /**
     * Searches for ItemDatas that have a name containing the specified
     * fragment. If specified client is the system client all matching
     * ItemDatas will be returned, otherwise the list will be limited to
     * the ItemDatas which belong to Client client.
     * 
     * @param client the Client ItemDatas should be assigned to or the
     *            system client.
     * @param fragment a substring that names of ItemDatas should
     *            contain. Case insensitiv.
     * @return list of ItemDatas, which may be empty. The list will be
     *         ordered by names ascending.
     */
    List<ItemData> getListByNameFragment(Client client, String fragment);

    /**
     * Returns the entity with the specified item number
     * 
     * @param client the client owning the item data entity
     * @param itemNumber the item number to find
     * @return the found entity or NULL if there is no matching item
     */
    ItemData getByItemNumber(Client client, String itemNumber);

    /**
     * Returns a list of item data entities, of which the safety stock
     * is underflown.
     * 
     * @param client the owning client
     * @return list of item datas
     */
    List<ItemData> getListSafetyStockUnderflow(Client client);
}
