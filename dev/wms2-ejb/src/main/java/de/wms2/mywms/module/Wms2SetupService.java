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
package de.wms2.mywms.module;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;
import org.mywms.model.Role;
import org.mywms.model.User;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.RoleService;
import org.mywms.service.UniqueConstraintViolatedException;
import org.mywms.service.UserService;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.util.Wms2Properties;

/**
 * Setup of the wms2 base module
 * 
 * @author krane
 *
 */
@Singleton
@Startup
public class Wms2SetupService extends ModuleSetup {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private ClientBusiness clientService;
	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@Override
	public String getModulePackage() {
		return "de.wms2.mywms";
	}

	@PostConstruct
	public void checkSetup() {
		// This call ensures availability of the system client prior to all other accesses
		clientService.getSystemClient();

		String value = propertyBusiness.getString(getModulePropertyName(), null);
		if (!StringUtils.isBlank(value)) {
			return;
		}

		try {
			setup(SetupLevel.INITIALIZED, null);
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Setup failed", t);
			return;
		}
	}

	@Override
	public void setup(SetupLevel level, Locale locale) throws BusinessException {
		logger.log(Level.WARNING, "Start Setup");

		createProperty(null, getModulePropertyName(), level.name(), Wms2Properties.GROUP_SETUP,
				Wms2Properties.GROUP_SETUP, null, locale);

		Client client = clientService.getSystemClient();
		User admin;
		try {
			admin = userService.getByUsername("admin");
		} catch (EntityNotFoundException e1) {
			admin = userService.create(client, "admin", "", "", "admin");
			admin.setLocale("en");
		} 
		User de;
		try {
			de = userService.getByUsername("de");
		} catch (EntityNotFoundException e1) {
			de = userService.create(client, "de", "", "", "de");
			de.setLocale("de");
		} 
		User en;
		try {
			en = userService.getByUsername("en");
		} catch (EntityNotFoundException e1) {
			en = userService.create(client, "en", "", "", "en");
			en.setLocale("en");
		} 

		try {
			Role role = roleService.create("Admin");
			admin.getRoles().add(role);
			de.getRoles().add(role);
			en.getRoles().add(role);
		} catch (UniqueConstraintViolatedException e) {
		}

		logger.log(Level.INFO, "Completed Setup");
	}

}
