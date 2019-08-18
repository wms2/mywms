/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.user;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;
import org.mywms.model.Role;
import org.mywms.model.User;
import org.mywms.res.BundleResolver;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Translator;

/**
 * Business operation for user handling
 * 
 * @author krane
 *
 */
@Stateless
public class UserBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Resource
	private SessionContext sessionContext;
	@Inject
	private Event<UserLoginEvent> loginEvent;
	@Inject
	private Event<UserLogoutEvent> logoutEvent;
	@Inject
	private Instance<PasswordValidator> passwordValidators;
	@Inject
	private PersistenceManager manager;
	@Inject
	private UserEntityService userService;

	/**
	 * Create a new Role
	 * 
	 * @param name        The name of the Role
	 * @param description The description
	 */
	public Role createRole(String name, String description) throws BusinessException {
		String logStr = "createRole ";
		logger.log(Level.INFO, logStr + "name=" + name);

		Role role = manager.createInstance(Role.class);
		role.setName(name);
		role.setDescription(description);
		manager.persist(role);

		return role;
	}

	/**
	 * Create a new User
	 * 
	 * @param name     The name of the user
	 * @param password The password of the user
	 */
	public User createUser(Client client, String name, String password) throws BusinessException {
		String logStr = "createUser ";
		logger.log(Level.INFO, logStr + "name=" + name);

		String encryptedPassword = encryptPassword(password);
		User user = manager.createInstance(User.class);
		user.setClient(client);
		user.setName(name);
		user.setPassword(encryptedPassword);

		Locale locale = Locale.getDefault();
		user.setLocale(locale.toLanguageTag());

		validatePassword(user, password, encryptedPassword);

		manager.persist(user);

		return user;
	}

	/**
	 * Get the roles of the currently logged in user
	 */
	public Collection<Role> getCurrentUsersRoles() {
		User user = getCurrentUser();
		return user.getRoles();
	}

	/**
	 * Read a collection of roles
	 * 
	 * @param user Optional, if not null only the roles of this user are read.
	 */
	@SuppressWarnings("unchecked")
	public Collection<Role> readRoles(User user) {
		if (user != null) {
			user = manager.find(User.class, user.getId());
			return user.getRoles();
		}
		String jpql = "select role FROM " + Role.class.getName() + " role ";

		Query query = manager.createQuery(jpql);

		return (List<Role>) query.getResultList();
	}

	/**
	 * Get the currently logged in user
	 */
	public User getCurrentUser() {
		String logStr = "getCurrentUser ";
		String userName = getCurrentUsersName();
		User user = readUser(userName);

		if (user == null) {
			logger.log(Level.WARNING, logStr + "no user for principal > " + userName);
		}

		return user;
	}

	/**
	 * Check whether the given user has the given role
	 */
	public boolean hasUserRole(User user, String roleName) throws BusinessException {
		user = manager.reload(user, false);
		if (user != null) {
			for (Role role : user.getRoles()) {
				if (role.getName().equals(roleName))
					return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given user has the given role
	 */
	public boolean hasUserRole(User user, Role role) throws BusinessException {
		user = manager.reload(user, false);
		if (user != null) {
			for (Role usersRole : user.getRoles()) {
				if (Objects.equals(usersRole, role)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the name of the currently logged in user
	 */
	public String getCurrentUsersName() {
		String logStr = "getCurrentUsersName ";
		try {
			String userName = sessionContext.getCallerPrincipal().getName();
			return userName;
		} catch (Throwable e) {
			logger.log(Level.SEVERE,
					logStr + "get principal name failed: Exception=" + e.getClass().getName() + ", " + e.getMessage(),
					e);
		}
		return null;
	}

	/**
	 * Read a User
	 * 
	 * @param name The name of the user
	 */
	public User readUser(String name) {
		return userService.readIgnoreCase(name);
	}

	/**
	 * Read a Role
	 * 
	 * @param name The name of the role
	 */
	public Role readRole(String name) {
		String hql = "SELECT entity FROM " + Role.class.getName() + " entity";
		hql += " WHERE entity.name=:name";
		Query query = manager.createQuery(hql);
		query.setParameter("name", name);
		try {
			return (Role) query.getSingleResult();
		} catch (Throwable t) {
		}
		return null;
	}

	/**
	 * Read the users who have the given role
	 */
	@SuppressWarnings("unchecked")
	public List<User> readUsers(Role role) {
		String jpql = "select distinct user FROM " + User.class.getName() + " user";

		if (role != null) {
			jpql += ", in (user.roles) role where role = :role ";
		}

		Query query = manager.createQuery(jpql);
		if (role != null) {
			query.setParameter("role", role);
		}

		return (List<User>) query.getResultList();
	}

	/**
	 * Get the locale of the currently logged in user
	 */
	public Locale getCurrentUsersLocale() {
		User user = getCurrentUser();
		Locale locale = null;

		if (user != null) {
			locale = Translator.parseLocale(user.getLocale());
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	/**
	 * Change the password of a User
	 * 
	 * @param user        The user of whom the password sould be changed
	 * @param oldPassword The old password. Needed for verification
	 * @param newPassword The new password
	 * @param validate    If true, validation will be started
	 * @param agent       Identifier of process, dialog or interface
	 */
	public void changePassword(User user, String oldPassword, String newPassword, boolean validate, String agent)
			throws BusinessException {
		String logStr = "changePassword ";
		logger.log(Level.INFO, logStr + "user=" + user);

		String oldEncryptedPassword = encryptPassword(oldPassword);
		String newEnctryptedPassword = encryptPassword(newPassword);

		if (!StringUtils.equals(oldEncryptedPassword, user.getPassword())) {
			logger.log(Level.INFO, logStr + "Old password does not match. user=" + user);
			throw new BusinessException(BundleResolver.class, "UserBusiness.passwordDoesNotMatch");
		}

		if (validate) {
			validatePassword(user, newPassword, newEnctryptedPassword);
		}
		user.setPassword(newEnctryptedPassword);
	}

	/**
	 * Change the password of a User
	 * 
	 * @param user        The user of whom the password sould be changed
	 * @param oldPassword The old password. Needed for verification
	 * @param newPassword The new password
	 * @param validate    If true, validation will be started
	 * @param agent       Identifier of process, dialog or interface
	 */
	public void changePassword(User user, String newPassword, boolean validate) throws BusinessException {
		String logStr = "changePassword ";
		logger.log(Level.INFO, logStr + "user=" + user);

		String newEnctryptedPassword = encryptPassword(newPassword);
		if (validate) {
			validatePassword(user, newPassword, newEnctryptedPassword);
		}
		user.setPassword(newEnctryptedPassword);
	}

	/**
	 * Encrypt a password
	 * 
	 * @param password The password to encrypt
	 */
	public String encryptPassword(String password) throws BusinessException {
		String logStr = "encryptPassword ";

		if (password == null) {
			password = "";
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(password.getBytes("UTF-8"));
			StringBuilder encryptedPassword = new StringBuilder();
			Formatter formatter = new Formatter(encryptedPassword);
			for (byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			formatter.close();
			return encryptedPassword.toString();
//			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//			messageDigest.reset();
//			return Base64.getEncoder().encodeToString(messageDigest.digest());
		} catch (Throwable t) {
			logger.log(Level.WARNING, logStr + "Cannot create encryption module", t);
			throw new BusinessException(BundleResolver.class, "UserBusiness.invalidEncrption");
		}
	}

	/**
	 * Add the given Role to the given user
	 */
	public void addRole(User user, Role role) {
		String logStr = "addRole ";
		logger.log(Level.FINE, logStr + "user=" + user + ", role=" + role);

		if (user.getRoles() == null) {
			user.setRoles(new ArrayList<>());
		}
		if (!user.getRoles().contains(role)) {
			user.getRoles().add(role);
		}
	}

	/**
	 * Check whether the registering user is the one who is authenticated in the
	 * context.
	 * 
	 * @throws BusinessException
	 */
	public void checkLogin(User user) throws BusinessException {
		String logStr = "checkLogin ";
		if (user == null) {
			return;
		}
		String currentUserName = getCurrentUsersName();
		String registeringUserName = user.getName();
		if (!StringUtils.equals(currentUserName, registeringUserName)) {
			logger.log(Level.WARNING, logStr + "Username mismatch. registering user=" + registeringUserName
					+ ", authenticated user=" + currentUserName);
			throw new BusinessException(BundleResolver.class, "UserBusiness.userLoginMismatch", currentUserName);
		}
	}

	/**
	 * Registration of login.
	 * <p>
	 * UserLoginEvent is fired.
	 */
	public void registerLogin(User user, String agent) throws BusinessException {
		if (user == null) {
			user = getCurrentUser();
		}
		if (user != null) {
			fireUserLoginEvent(user, agent);
		}
	}

	/**
	 * Registration of logout.
	 * <p>
	 * UserLogoutEvent is fired.
	 */
	public void registerLogout(User user, String agent) throws BusinessException {
		if (user == null) {
			user = getCurrentUser();
		}
		if (user != null) {
			fireUserLogoutEvent(user, agent);
		}
	}

	private void fireUserLoginEvent(User user, String agent) throws BusinessException {
		logger.fine("Fire UserLoginEvent. user=" + user + ", agent=" + agent);
		try {
			loginEvent.fire(new UserLoginEvent(user, agent));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireUserLogoutEvent(User user, String agent) throws BusinessException {
		try {
			logger.fine("Fire UserLogoutEvent. user=" + user + ", agent=" + agent);
			logoutEvent.fire(new UserLogoutEvent(user, agent));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	public void validatePassword(User user, String password, String encryptedPassword) throws BusinessException {
		for (PasswordValidator validator : passwordValidators) {
			validator.validate(user, password, encryptedPassword);
		}
	}
}
