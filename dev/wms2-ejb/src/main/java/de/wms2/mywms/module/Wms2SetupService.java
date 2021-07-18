/* 
Copyright 2019-2021 Matthias Krane
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;
import org.mywms.model.Role;
import org.mywms.model.User;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaEntityService;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.LocationClusterEntityService;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.LocationTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.product.ItemUnitEntityService;
import de.wms2.mywms.property.SystemProperty;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.strategy.StorageStrategyEntityService;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.TypeCapacityEntityService;
import de.wms2.mywms.strategy.Zone;
import de.wms2.mywms.strategy.ZoneEntityService;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
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
	private OrderStrategyEntityService orderStrategyService;
	@Inject
	private StorageStrategyEntityService storageStrategyService;
	@Inject
	private ItemUnitEntityService itemUnitService;
	@Inject
	private ZoneEntityService zoneService;
	@Inject
	private TypeCapacityEntityService typeCapacityEntityService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private DemoDataGenerator demoDataGenerator;

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
	public int getOrderIndex() {
		return 10;
	}

	@Override
	public void setup(SetupLevel level, Locale locale) throws BusinessException {

		String value = propertyBusiness.getString(getModulePropertyName(), null);
		if (StringUtils.equals(value, level.name())) {
			return;
		}

		switch (level) {
		case UNINITIALIZED:
			createProperty(null, getModulePropertyName(), level.name(), Wms2Properties.GROUP_SETUP,
					Wms2Properties.GROUP_SETUP, locale);
			break;
		case INITIALIZED:
			setupBasicData(locale);
			createProperty(null, getModulePropertyName(), level.name(), Wms2Properties.GROUP_SETUP,
					Wms2Properties.GROUP_SETUP, locale);
			break;
		case DEMO_SMALL:
		case DEMO_MEDIUM:
		case DEMO_LARGE:
			demoDataGenerator.generateDemoData(locale);
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

		Client client = clientService.getSystemClient();

		logger.info("Create Properties...");
		createProperty(null, Wms2Properties.KEY_PASSWORD_EXPRESSION, null, Wms2Properties.GROUP_UI, locale);
		createProperty(null, Wms2Properties.KEY_REPORT_LOCALE,
				locale == null ? Locale.getDefault().toString() : locale.toString(), Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_REPLENISH_FROM_PICKING, "true", Wms2Properties.GROUP_WMS, locale);

		createProperty(null, Wms2Properties.KEY_GOODSRECEIPT_LIMIT_AMOUNT_TO_NOTIFIED, "false",
				Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_SHIPPING_RENAME_UNITLOAD, "true", Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_SHIPPING_LOCATION, null, Wms2Properties.GROUP_WMS, locale);
		createProperty(null, Wms2Properties.KEY_STRATEGY_ZONE_FLOW, "A,B,C;B,C,A;C,B,A", Wms2Properties.GROUP_WMS,
				locale);

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
		User service = userBusiness.readUser("service");
		if (service == null) {
			service = userBusiness.createUser(client, "service", "service");
			service.setLocale("en");
		}

		Role adminRole = userBusiness.readRole("Admin");
		if (adminRole == null) {
			adminRole = userBusiness.createRole("Admin", "");
		}
		userBusiness.addRole(admin, adminRole);
		userBusiness.addRole(de, adminRole);
		userBusiness.addRole(en, adminRole);
		Role serviceRole = userBusiness.readRole("Service");
		if (serviceRole == null) {
			serviceRole = userBusiness.createRole("Service", "");
		}
		userBusiness.addRole(admin, serviceRole);
		userBusiness.addRole(service, serviceRole);
		Role operatorRole = userBusiness.readRole("Operator");
		if (operatorRole == null) {
			operatorRole = userBusiness.createRole("Operator", "");
		}

		logger.info("Create Sequences...");
		sequeceBusiness.createNotExisting("GoodsReceipt", translate("sequenceGoodsReceipt", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("Advice", translate("sequenceAdvice", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("UnitLoad", translate("sequenceUnitLoad", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("PickingOrder", translate("sequencePickingOrder", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("DeliveryOrder", translate("sequenceDeliveryOrder", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("TransportOrder", translate("sequenceTransportOrder", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("ShippingOrder", translate("sequenceShippingOrder", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("ReplenishOrder", translate("sequenceReplenishOrder", locale), 1L, 999999L);
		sequeceBusiness.createNotExisting("StocktakingOrder", translate("sequenceStocktakingOrder", locale), 1L,
				999999L);

		logger.info("Create defaults...");
		locationClusterService.getSystem();
		LocationCluster defaultCluster = locationClusterService.getDefault();
		areaService.getSystem();
		areaService.getDefault();
		LocationType systemLocationType = locationTypeService.getSystem();
		LocationType defaultLocationType = locationTypeService.getDefault();
		unitLoadTypeService.getSystem();
		UnitLoadType defaultUnitLoadType = unitLoadTypeService.getDefault();
		unitLoadTypeService.getVirtual();
		unitLoadTypeService.getPicking();
		locationService.getClearing();
		locationService.getTrash();
		OrderStrategy defaultOrderStrategy = orderStrategyService.getDefault(client);
		orderStrategyService.getExtinguish(client);
		storageStrategyService.getDefault();
		ItemUnit itemUnit = itemUnitService.getDefault();
		itemUnit.setName(translate("itemUnitPcs", locale));

		createZone("A");
		createZone("B");
		createZone("C");

		Area areaIn = createArea(translate("areaGoodsIn", locale));
		areaIn.setUseFor(AreaUsages.GOODS_IN, true);

		Area areaOut = createArea(translate("areaGoodsOut", locale));
		areaOut.setUseFor(AreaUsages.GOODS_OUT, true);

		Area areaClearing = createArea(translate("areaClearing", locale));

		createCapacityConstraint(defaultLocationType, defaultUnitLoadType);

		List<StorageLocation> list = locationService.getForGoodsIn(null);
		if (list.isEmpty()) {
			createStorageLocation(client, translate("locationGoodsIn", locale), areaIn, systemLocationType,
					defaultCluster);
		}
		list = locationService.getForGoodsOut(null);
		if (list.isEmpty()) {
			StorageLocation goodsOutLocation = createStorageLocation(client, translate("locationGoodsOut", locale),
					areaOut, systemLocationType, defaultCluster);
			defaultOrderStrategy.setDefaultDestination(goodsOutLocation);

			SystemProperty shippingLocationProperty = propertyBusiness.read(Wms2Properties.KEY_SHIPPING_LOCATION);
			if (shippingLocationProperty != null && StringUtils.isBlank(shippingLocationProperty.getPropertyValue())) {
				shippingLocationProperty.setPropertyValue(goodsOutLocation.getName());
			}
		}
		StorageLocation loc = locationService.getClearing();
		loc.setArea(areaClearing);

		loc = locationService.getTrash();
		loc.setArea(areaClearing);

		logger.log(Level.INFO, "Completed Setup");
	}

	private void createZone(String name) throws BusinessException {
		Zone zone = zoneService.read(name);
		if (zone == null) {
			zone = zoneService.create(name);
		}

	}

	private Area createArea(String name) throws BusinessException {
		Area area = areaService.readByName(name);
		if (area == null) {
			area = areaService.create(name);
		}
		return area;
	}

	private void createCapacityConstraint(LocationType locationType, UnitLoadType unitLoadType)
			throws BusinessException {
		TypeCapacityConstraint constraint = typeCapacityEntityService.read(locationType, unitLoadType);
		if (constraint == null) {
			constraint = typeCapacityEntityService.create(locationType, unitLoadType, BigDecimal.valueOf(100));
		}
	}

	private StorageLocation createStorageLocation(Client client, String name, Area area, LocationType locationType,
			LocationCluster cluster) {
		StorageLocation location = locationService.readByName(name);
		if (location == null) {
			location = locationService.create(name, client, locationType, area, cluster);
		}
		return location;
	}

	private String translate(String key, Locale locale) {
		return Translator.getString(Wms2BundleResolver.class, "BasicData", key, "name", locale);
	}
}
