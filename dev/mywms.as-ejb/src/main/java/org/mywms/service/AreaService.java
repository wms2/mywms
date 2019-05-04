/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.Area;
import org.mywms.model.Client;

/**
 * This interface declares the service for the entity Area.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface AreaService
    extends BasicService<Area>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Checks the specified name for being unique within the namespace
     * of Client client. If the name is valid, a new Area will be
     * created, assigned to Client client and added to persistence
     * context.
     * 
     * @param client the owning Client of the new Area.
     * @param name the name of the new Area.
     * @throws UniqueConstraintViolatedException if Client client has
     *             already a Area with Name name.
     * @throws NullPointerException if any of the parameters is null.
     */
    Area create(Client client, String name)
        throws UniqueConstraintViolatedException;

    /**
     * Resolves an Area by its name and owning client.
     * 
     * @param client the client, the Area should be assigned to.
     * @param name the name to search for
     * @return matching Area.
     * @throws EntityNotFoundException if there is no Area with Name
     *             name assigned to Client client.
     */
    Area getByName(Client client, String name) throws EntityNotFoundException;
}
