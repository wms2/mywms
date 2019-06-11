/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.User;

/**
 * @see org.mywms.service.UserService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class UserServiceBean extends BasicServiceBean<User> implements
		UserService {
	
	/**
	 * @see org.mywms.service.UserService#getByUsername(java.lang.String)
	 */
	public User getByUsername(String username) throws EntityNotFoundException {
		if (username == null) {
			throw new NullPointerException("getByUsername: parameter == null");
		}

		Query query = manager.createQuery("SELECT u FROM "
				+ User.class.getSimpleName() + " u " + "WHERE u.name=:name");

		query.setParameter("name", username);

		try {
			User u = (User) query.getSingleResult();
			return u;
		} catch (NoResultException ex) {
			throw new EntityNotFoundException(
					ServiceExceptionKey.NO_USER_WITH_USERNAME);
		}
	}
}
