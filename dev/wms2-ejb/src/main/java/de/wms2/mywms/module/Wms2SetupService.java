/* 
Copyright 2019-2020 Matthias Krane
info@krane.engineer

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

import de.wms2.mywms.advice.Advice;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.AreaEntityService;
import de.wms2.mywms.location.LocationClusterEntityService;
import de.wms2.mywms.location.LocationTypeEntityService;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.replenish.ReplenishOrder;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.shipping.ShippingOrder;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.transport.TransportOrder;
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
	@Inject
	private LocationClusterEntityService locationClusterService;
	@Inject
	private AreaEntityService areaService;
	@Inject
	private LocationTypeEntityService locationTypeService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private StorageLocationEntityService storageLocationEntityService;
	@Inject
	private OrderStrategyEntityService orderStrategyService;

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
					Wms2Properties.GROUP_SETUP, locale);
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
		sequeceBusiness.createNotExisting(GoodsReceipt.class.getSimpleName(), "WE-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(Advice.class.getSimpleName(), "AVIS-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(UnitLoad.class.getSimpleName(), "%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(PickingOrder.class.getSimpleName(), "PICK-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(DeliveryOrder.class.getSimpleName(), "ORDER-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(TransportOrder.class.getSimpleName(), "TR-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(ShippingOrder.class.getSimpleName(), "GOUT-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting(ReplenishOrder.class.getSimpleName(), "REPL-%1$06d", 1L, 999999L);
		sequeceBusiness.createNotExisting("StocktakingOrder", "IV-%1$06d", 1L, 999999L);

		logger.info("Create defaults...");
		locationClusterService.getSystem();
		locationClusterService.getDefault();
		areaService.getSystem();
		areaService.getDefault();
		locationTypeService.getSystem();
		locationTypeService.getDefault();
		unitLoadTypeService.getSystem();
		unitLoadTypeService.getDefault();
		unitLoadTypeService.getVirtual();
		storageLocationEntityService.getClearing();
		storageLocationEntityService.getTrash();
		orderStrategyService.getDefault(client);

		createProperty(null, Wms2Properties.KEY_PASSWORD_EXPRESSION, null, Wms2Properties.GROUP_UI, locale);
		createProperty(null, Wms2Properties.KEY_REPORT_LOCALE,
				locale == null ? Locale.getDefault().toString() : locale.toString(), Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_REPLENISH_FROM_PICKING, "true", Wms2Properties.GROUP_WMS, locale);

		createProperty(null, Wms2Properties.KEY_GOODSRECEIPT_LIMIT_AMOUNT_TO_NOTIFIED, "false",
				Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_SHIPPING_RENAME_UNITLOAD, "true", Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_SHIPPING_LOCATION, null, Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_STRATEGY_ZONE_FLOW, null, Wms2Properties.GROUP_WMS, locale);

		logger.log(Level.INFO, "Completed Setup");
	}
}
