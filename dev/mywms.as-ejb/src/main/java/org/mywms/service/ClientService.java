/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.Client;

/**
 * This interface declares the service for the entity
 * PluginConfiguration. For this service it is save to call the
 * <code>get(String name)</code> method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Local
public interface ClientService
    extends BasicService<Client>
{
    /**
     * Returns the client with the specified number.
     * 
     * @param number the number of the client
     * @return the client with the specified number or NULL if there is no matching client
     */
    Client getByNumber(String number);

    /**
     * Returns the client with the specified name.
     * 
     * @param name the name of the client
     * @return the client with the specified name
     * @throws EntityNotFoundException if the specified client could not
     *             be found
     */
    Client getByName(String name) throws EntityNotFoundException;

    /**
     * Creates a new client with the specified name.
     * 
     * @param name the name of the new client
     * @param number the unique client number
     * @return the new created client
     */
    Client create(String name, String number, String code);

    /**
     * The system client is the owner of a running warehouse management
     * system and/or the warehouse itself. Users who are assigned to the
     * system client are able to access the system regardless of client
     * partitioning.
     * 
     * @return the unique system client
     */
    Client getSystemClient();
}
