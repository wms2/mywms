/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

/**
 * @author okrause
 */
@Stateless
public class LotServiceBean
    extends BasicServiceBean<Lot>
    implements LotService
{
    /**
     * @see org.mywms.service.LotService#create(org.mywms.model.Client,
     *      org.mywms.model.ItemData, java.lang.String)
     */
    public Lot create(Client client, ItemData itemData, String name) {
        return create(client, itemData, name, new Date());
    }

    /**
     * @see org.mywms.service.LotService#create(org.mywms.model.Client,
     *      org.mywms.model.ItemData, java.lang.String, java.util.Date)
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
     * @see org.mywms.service.LotService#getListByItemData(org.mywms.model.ItemData)
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
