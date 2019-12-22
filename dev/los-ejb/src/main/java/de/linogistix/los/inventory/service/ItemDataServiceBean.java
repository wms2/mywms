/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.product.ItemData;

/**
 * @see de.linogistix.los.inventory.service.ItemDataService
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class ItemDataServiceBean
    extends BasicServiceBean<ItemData>
    implements ItemDataService
{


    /**
     * @see de.linogistix.los.inventory.service.ItemDataService#getByItemNumber(Client,
     *      java.lang.String)
     */
    public ItemData getByItemNumber(String itemNumber){
    	
        Query query =
            manager.createQuery("SELECT id FROM "
                + ItemData.class.getSimpleName()
                + " id "
                + "WHERE id.number=:itemNumber");

        query.setParameter("itemNumber", itemNumber);

        try {
            ItemData id = (ItemData) query.getSingleResult();
            return id;
        }
	        catch (NoResultException ex) {
	            return null;
	        }
    }
}
