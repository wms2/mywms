/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

/**
 * This interface declares the service for the entity Lot.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Local
public interface LotService
    extends BasicService<Lot>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Creates a new Lot of the specified ItemData and with the
     * specified name (or id). The date of the Lot will be set
     * implicitly to the current date.
     * 
     * @param client the client owning the new lot
     * @param itemData the ItemData the Lot belongs to
     * @param name the name or id of the lot. The name may be, but must
     *            not be unique.
     */
    Lot create(Client client, ItemData itemData, String name);

    /**
     * Creates a new Lot of the specified ItemData and with the
     * specified name (or id). The date of the Lot will be set
     * implicitly to the current date.
     * 
     * @param client the client owning the new lot
     * @param itemData the ItemData the Lot belongs to
     * @param name the name or id of the lot. The name may be, but must
     *            not be unique.
     * @param date the date of the lot.
     */
    Lot create(Client client, ItemData itemData, String name, Date date);

    /**
     * Returns a list of Lots belonging to the specified ItemData.
     * 
     * @param itemData the ItemData being searched
     * @return a list of Lots
     */
    List<Lot> getListByItemData(ItemData itemData);

}
