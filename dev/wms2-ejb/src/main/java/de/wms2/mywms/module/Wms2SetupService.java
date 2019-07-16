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
import org.mywms.service.RoleService;
import org.mywms.service.UniqueConstraintViolatedException;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.user.UserBusiness;
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
	private UserBusiness userBusiness;
	@Inject
	private RoleService roleService;
	@Inject
	private SequenceBusiness sequeceBusiness;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@Override
	public String getModulePackage() {
		return "de.wms2.mywms";
	}

	@PostConstruct
	public void checkSetup() {
		// This call ensures availability of the system client prior to all other
		// accesses
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

		String value = propertyBusiness.getString(getModulePropertyName(), null);
		if (StringUtils.equals(value, level.name())) {
			return;
		}

		switch (level) {
		case INITIALIZED:
			setupBasicData(locale);
			createProperty(null, getModulePropertyName(), level.name(), Wms2Properties.GROUP_SETUP,
					Wms2Properties.GROUP_SETUP, null, locale);
			break;
		default:
			logger.log(Level.INFO, "Skip setup level=" + level);
			break;
		}
	}

	public void setupBasicData(Locale locale) throws BusinessException {
		logger.log(Level.WARNING, "setupBasicData");

		logger.info("Create Users...");
		Client client = clientService.getSystemClient();

		logger.info("Create Users...");
		User admin = userBusiness.readUser("admin");
		if (admin == null) {
			admin = userBusiness.createUser(client, "admin", "admin");
			admin.setLocale("en");
		}
		User de = userBusiness.readUser("de");
		if (de == null) {
			de = userBusiness.createUser(client, "de", "de");
			de.setLocale("de");
		}
		User en = userBusiness.readUser("en");
		if (en == null) {
			en = userBusiness.createUser(client, "en", "en");
			en.setLocale("en");
		}

		try {
			Role adminRole = roleService.create("Admin");
			admin.getRoles().add(adminRole);
			de.getRoles().add(adminRole);
			en.getRoles().add(adminRole);
			Role serviceRole = roleService.create("Service");
			admin.getRoles().add(serviceRole);
		} catch (UniqueConstraintViolatedException e) {
		}

		logger.info("Create Sequences...");
		sequeceBusiness.createNotExisting("GoodsReceipt", "WE-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("Advice", "AVIS-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("UnitLoadAdvice", "UAV-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("UnitLoad", "%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("PickingOrder", "PICK-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("DeliveryOrder", "ORDER-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("Storage", "STORE-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("Shipment", "GOUT-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("ReplenishOrder", "REPL-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("Inventory", "IMAN-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("Stocktaking", "IV-%1$06d", 1L, 999999L);

		createProperty(null, Wms2Properties.KEY_PASSWORD_EXPRESSION, null, Wms2Properties.GROUP_UI, locale);

		logger.log(Level.INFO, "Completed Setup");
	}
}
