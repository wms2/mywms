/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

/**
 * @author okrause
 */
@Stateless
public class LotServiceBean
    extends BasicServiceBean<Lot>
    implements LotService
{
    /**
     * @see de.linogistix.los.inventory.service.LotService#create(org.mywms.model.Client,
     *      de.wms2.mywms.entity.ItemData, java.lang.String)
     */
    public Lot create(Client client, ItemData itemData, String name) {
        return create(client, itemData, name, new Date());
    }

    /**
     * @see de.linogistix.los.inventory.service.LotService#create(org.mywms.model.Client,
     *      de.wms2.mywms.entity.ItemData, java.lang.String, java.util.Date)
     */
    public Lot create(Client client, ItemData itemData, String name, Date date)
    {
        Lot lot = new Lot();

        lot.setItemData(itemData);
        lot.setClient(client);
        lot.setName(name);
        lot.setDate(date);

        manager.persist(lot);
        manager.flush();

        return lot;
    }

    /**
     * @see de.linogistix.los.inventory.service.LotService#getListByItemData(de.wms2.mywms.entity.ItemData)
     */
    @SuppressWarnings("unchecked")
    public List<Lot> getListByItemData(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT DISTINCT l FROM "
                + Lot.class.getSimpleName()
                + " l "
                + "WHERE l.itemData = :itemData "
                + "ORDER BY l.date");

        query.setParameter("itemData", itemData);

        return (List<Lot>) query.getResultList();
    }

}
