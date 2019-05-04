/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.Role;

/**
 * This interface declares the service for the entity
 * PluginConfiguration. For this service it is save to call the
 * <code>get(String name)</code> method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface RoleService
    extends BasicService<Role>
{
    /**
     * Returns the specified role entity.
     * 
     * @param name the name of the entity
     * @return the found role entity
     * @throws EntityNotFoundException if an entity with the specified
     *             name could not be found
     */
    Role getByName(String name) throws EntityNotFoundException;

    /**
     * Checks if there is already a role with Name name. If the name is
     * valid, a new Role with Name name will be created and added to the
     * persistence context.
     * 
     * @param name the name of the new Role.
     * @return a persistent instance of Role.
     * @throws UniqueConstraintViolatedException if there is already a
     *             role with Name name.
     * @throws NullPointerException if name is null.
     */
    Role create(String name) throws UniqueConstraintViolatedException;

}
