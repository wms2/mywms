/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.product.ItemData;

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
     * Returns the entity with the specified item number
     * 
     * @param client the client owning the item data entity
     * @param itemNumber the item number to find
     * @return the found entity or NULL if there is no matching item
     */
    ItemData getByItemNumber(String itemNumber);
}
