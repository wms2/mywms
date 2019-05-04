/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Area;
import org.mywms.model.Client;

/**
 * @see org.mywms.service.AreaService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class AreaServiceBean
    extends BasicServiceBean<Area>
    implements AreaService
{

    /**
     * @see org.mywms.service.AreaService#createArea(Client, String)
     */
    public Area create(Client client, String name)
        throws UniqueConstraintViolatedException
    {
        if (client == null || name == null) {
            throw new NullPointerException("createArea: parameter == null");
        }
        client = manager.merge(client);

        Area area = new Area();
        area.setClient(client);
        area.setName(name);
        manager.persist(area);

        try {
            manager.flush();
        }
        catch (PersistenceException pe) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.AREA_NAME_NOT_UNIQUE);
        }
        return area;
    }

    /**
     * @see org.mywms.service.AreaService#getByNameClient, String)
     */
    public Area getByName(Client client, String name)
        throws EntityNotFoundException
    {
        Query query =
            manager.createQuery("SELECT ar FROM "
                + Area.class.getSimpleName()
                + " ar "
                + "WHERE ar.name=:name "
                + "AND ar.client=:cl");

        query.setParameter("name", name);
        query.setParameter("cl", client);

        try {
            Area area = (Area) query.getSingleResult();
            return area;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_AREA_WITH_NAME);
        }
    }

}
