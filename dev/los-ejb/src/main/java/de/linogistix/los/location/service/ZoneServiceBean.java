/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.wms2.mywms.strategy.Zone;

/**
 * @see de.linogistix.los.location.service.ZoneService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class ZoneServiceBean
    extends BasicServiceBean<Zone>
    implements ZoneService
{
    /**
     * @see de.linogistix.los.location.service.ZoneService#create(org.mywms.model.Client,
     *      java.lang.String)
     */
    public Zone create(Client client, String name)
        throws UniqueConstraintViolatedException
    {
        if (client == null || name == null) {
            throw new NullPointerException("create Zone: parameter == null");
        }
        client = manager.merge(client);

        Zone zone = new Zone();
        zone.setName(name);
        manager.persist(zone);

        try {
            manager.flush();
        }
        catch (PersistenceException pe) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.ZONE_NAME_NOT_UNIQUE);
        }

        return zone;
    }

    /**
     * @see de.linogistix.los.location.service.ZoneService#getByName(org.mywms.model.Client,
     *      java.lang.String)
     */
    public Zone getByName(Client client, String name)
        throws EntityNotFoundException
    {
        Query query =
            manager.createQuery("SELECT z FROM "
                + Zone.class.getSimpleName()
                + " z "
                + "WHERE z.name=:name ");

        query.setParameter("name", name);

        try {
            Zone z = (Zone) query.getSingleResult();
            return z;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ZONE_WITH_NAME);
        }
    }
}
