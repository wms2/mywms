/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.facade.FacadeException;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.Role;
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

	/**
	 * @see org.mywms.service.UserService#getListByRole(Client, Role)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getListByRole(Client client, Role role) {
		StringBuffer qstr = new StringBuffer();
		qstr
				.append(
						"SELECT DISTINCT u FROM " + User.class.getSimpleName()
								+ " u, ").append("IN (u.roles) role ").append(
						"WHERE role = :role ");

		if (!client.isSystemClient()) {
			qstr.append("AND u.client = :client");
		}

		Query query = manager.createQuery(qstr.toString());
		query.setParameter("role", role);

		if (!client.isSystemClient()) {
			query.setParameter("client", client);
		}

		return (List<User>) query.getResultList();
	}

	/**
	 * @see org.mywms.service.UserService#create(Client, String)
	 */
	public User create(Client client, String username)
			throws UniqueConstraintViolatedException {
		if (client == null || username == null) {
			throw new NullPointerException("createUser: parameter == null");
		}
		client = manager.merge(client);

		User user = new User();
		user.setName(username);
		user.setClient(client);
		manager.persist(user);

		return user;
	}

	public User create(Client client, String username, String firstName,
			String lastName, String pwd) {
		
		if (client == null){
			throw new NullPointerException("Client must not be null");
		}
		
		client = manager.merge(client);

		User user = new User();
		user.setName(username);
		user.setClient(client);
		user.setFirstname(firstName);
		user.setLastname(lastName);

		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalArgumentException("cannot encrypt password");
		}
		md5.reset();
		md5.update(pwd.getBytes());
		StringBuffer hexString = new StringBuffer(32);
		Formatter f = new Formatter(hexString);
		for (byte b : md5.digest()) {
			f.format("%02x", b);
		}
		String password = hexString.toString();
		user.setPassword(password);

		manager.persist(user);

		return user;
	}

	public User changePasswd(User user, String passw, boolean checkWeakPassword) throws UserServiceException{
		MessageDigest md5;
		
		if (checkWeakPassword){
			checkPassword(passw);
		}
		
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalArgumentException("cannot encrypt password");
		}
		md5.reset();
		md5.update(passw.getBytes());
		StringBuffer hexString = new StringBuffer(32);
		Formatter f = new Formatter(hexString);
		for (byte b : md5.digest()) {
			f.format("%02x", b);
		}
		String password = hexString.toString();
		user.setPassword(password);
		return user;
	}
	
	/**
	 * Taken fom http://www.dreamincode.net/forums/showtopic75271.htm
	 * 
	 * Passwords must contain a minimum of 6 to 10 characters and must 
	 *  contain at least one digit and character
	 *  
	 * @param password
	 * @throws FacadeException 
	 */
	public void checkPassword(String password) throws UserServiceException{
	    int length = password.length();
		int charCount = 0, intCount = 0;
		byte b[] = password.getBytes();
		boolean errOccurred = false;

		errOccurred = length < 5 || length > 10;

		if (!errOccurred) {
			for (int i = 0; i < b.length; i++) {
				charCount = ((b[i] > 64 && b[i] < 91) // within A - Z
				|| (b[i] > 96 && b[i] < 123)) == true // within a-z
				? ++charCount
						: charCount; // increment or use same
				intCount = (b[i] > 47 && b[i] < 58) == true // within A - Z
				? ++intCount
						: intCount; // increment or use same
			}
		}

		// error occurred earlier or char count or int count was 0
		errOccurred = errOccurred || charCount == 0 || intCount == 0;

		if (errOccurred) {
			throw new UserServiceException("Weak password", "WEAK_PASSWORD", new Object[0]);
		} else {
			// password OK
		}

	}
}
