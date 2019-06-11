/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.User;

/**
 * This interface declares the service for the entity User. For this
 * service it is save to call the <code>get(String name)</code>
 * method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface UserService
    extends BasicService<User>

{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Returns the user with the specified name.
     * 
     * @param username the system unique name of the user
     */
    User getByUsername(String username) throws EntityNotFoundException;

}
