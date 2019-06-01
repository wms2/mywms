/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.wms2.mywms.strategy.Zone;

/**
 * This interface declares the service for the entity Zone.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface ZoneService
    extends BasicService<Zone>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Creates a new Zone.
     * 
     * @param client the owner of the new Zone.
     * @param name the number of the new Zone.
     * @return a persistent instance of Zone.
     * @throws UniqueConstraintViolatedException if Client client has
     *             already an ItemData with Number number.
     * @throws NullPointerException if client or number is null.
     */
    Zone create(Client client, String name)
        throws UniqueConstraintViolatedException;

    /**
     * Returns the entity with the specified name.
     * 
     * @param client the owner of the zone
     * @param name the name of the zone
     * @return the persistent Zone instance
     * @throws EntityNotFoundException if a Zone with the specified
     *             name, owned by the specified client could not be
     *             found
     */
    Zone getByName(Client client, String name) throws EntityNotFoundException;
}
