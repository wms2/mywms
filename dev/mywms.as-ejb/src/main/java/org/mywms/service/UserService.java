/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.Role;
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

    /**
     * Returns a list of users, matching the specified Role. If
     * specified client is the system client all existing Users will be
     * returned, otherwise the list will be limited to the Users who
     * belong to Client client.
     * 
     * @param client the Client Users are assigned to or the system
     *            client.
     * @param role the Role Users should have.
     * @return list of users
     */
    List<User> getListByRole(Client client, Role role);

    /**
     * Checks the specified username for being unique within the system.
     * If the name is valid, a new User will be created, assigned to
     * Client client and added to persistence context.
     * 
     * @param client the pursessor of the new User.
     * @param username the username of the new User, which is used for
     *            login.
     * @return a persistent instance of User.
     * @throws UniqueConstraintViolatedException if there is already a
     *             User with name username.
     * @throws NullPointerException if any of the parameters is null.
     */
    User create(Client client, String username)
        throws UniqueConstraintViolatedException;
    
    User create(Client client, String username, String firstName, String lastName, String pwd);

    /**
     * Changes the password of the user
     * 
     * @param user
     * @param passw the password, it will be transformed to MD5 hash
     * @param checkWeakPassword if true a password check will be performed
     * @return
     * @throws UserServiceException 
     */
    public User changePasswd(User user, String passw, boolean checkWeakPassword) throws UserServiceException;
    
    /**
     * Checks passwords. If password is too weak a UserServiceException is thrown.
     * @param password
     * @throws UserServiceException if password is too weak.
     */
	public void checkPassword(String password) throws UserServiceException;

}
