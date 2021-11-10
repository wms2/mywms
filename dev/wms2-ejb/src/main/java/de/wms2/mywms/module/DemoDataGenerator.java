/* 
Copyright 2021 Matthias Krane

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemUnitType;

import de.wms2.mywms.address.Address;
import de.wms2.mywms.advice.Advice;
import de.wms2.mywms.advice.AdviceBusiness;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderEntityService;
import de.wms2.mywms.delivery.DeliveryOrderLineEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.inventory.UnitLoadTypeUsages;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaEntityService;
import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.LocationClusterEntityService;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.LocationTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderGenerator;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingOrderLineGenerator;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataEntityService;
import de.wms2.mywms.product.ItemDataNumber;
import de.wms2.mywms.product.ItemDataNumberEntityService;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.product.ItemUnitEntityService;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.product.PackagingUnitEntityService;
import de.wms2.mywms.sequence.CheckDigitService;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.OrderPrio;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.TypeCapacityEntityService;
import de.wms2.mywms.strategy.Zone;
import de.wms2.mywms.strategy.ZoneEntityService;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;

@Stateless
public class DemoDataGenerator {
	private static final Logger log = Logger.getLogger(DemoDataGenerator.class);

	@Resource
	private EJBContext context;
	@Inject
	private ClientBusiness clientService;
	@Inject
	private ZoneEntityService zoneService;
	@Inject
	private AreaEntityService areaService;
	@Inject
	private ItemDataEntityService itemDataService;
	@Inject
	private FixAssignmentEntityService fixedService;
	@Inject
	private ItemDataNumberEntityService eanService;
	@Inject
	private PersistenceManager manager;
	@Inject
	private LocationTypeEntityService locationTypeService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private LocationClusterEntityService locationClusterService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private TypeCapacityEntityService typeCapacityEntityService;
	@Inject
	private AdviceBusiness adviceBusiness;
	@Inject
	private DeliveryOrderEntityService deliveryOrderService;
	@Inject
	private DeliveryOrderLineEntityService deliveryOrderLineService;
	@Inject
	private OrderStrategyEntityService orderStrategyEntityService;
	@Inject
	private ItemUnitEntityService itemUnitService;
	@Inject
	private CheckDigitService checkDigitService;
	@Inject
	private PackagingUnitEntityService packagingService;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private PickingOrderLineGenerator pickingPosGenerator;
	@Inject
	private PickingOrderGenerator pickingOrderGenerator;

	public void generateDemoData(Locale locale) throws BusinessException {
		long start = System.currentTimeMillis();

		Client systemClient = clientService.getSystemClient();

		log.info("Create Units...");
		ItemUnit defaultUnit = itemUnitService.getDefault();
		defaultUnit.setName(translate("unitPcs", locale));
		ItemUnit gramUnit = createItemUnit(translate("unitGramm", locale), ItemUnitType.WEIGHT);
		ItemUnit packUnit = createItemUnit(translate("unitPack", locale), ItemUnitType.PIECE);

		log.info("Create Zones...");
		Zone aZone = createZone("A");
		Zone bZone = createZone("B");
		Zone cZone = createZone("C");

		log.info("Create LocationTypes...");
		LocationType defaultLocationType = locationTypeService.getDefault();
		defaultLocationType.setName(translate("locationTypePallet", locale));
		locationTypeService.setDefault(defaultLocationType);
		LocationType shelfLocationType = createLocationType(translate("locationTypeShelf", locale));
		LocationType blockLocationType = createLocationType(translate("locationTypeBlock", locale));
		LocationType systemLocationType = locationTypeService.getSystem();

		log.info("Create UnitLoadTypes...");
		UnitLoadType euroUnitLoadType = createUnitLoadType(translate("unitLoadTypeEuro", locale), 1.8, 0.8, 1.2, 900,
				25);
		euroUnitLoadType.setUseFor(UnitLoadTypeUsages.FORKLIFT, true);
		euroUnitLoadType.setUseFor(UnitLoadTypeUsages.COMPLETE, true);
		euroUnitLoadType.setUseFor(UnitLoadTypeUsages.PICKING, true);
		euroUnitLoadType.setUseFor(UnitLoadTypeUsages.SHIPPING, true);
		euroUnitLoadType.setUseFor(UnitLoadTypeUsages.STORAGE, true);
		euroUnitLoadType.setUseFor(UnitLoadTypeUsages.PACKING, true);
		unitLoadTypeService.setDefault(euroUnitLoadType);
		UnitLoadType box60UnitLoadType = createUnitLoadType(translate("unitLoadTypeBox6040", locale), 0.3, 0.6, 0.4, 20,
				0.5);
		box60UnitLoadType.setUseFor(UnitLoadTypeUsages.STORAGE, true);
		UnitLoadType box30UnitLoadType = createUnitLoadType(translate("unitLoadTypeBox3040", locale), 0.3, 0.3, 0.4, 15,
				0.3);
		box30UnitLoadType.setUseFor(UnitLoadTypeUsages.STORAGE, true);
		UnitLoadType cartonUnitLoadTypeA = createUnitLoadType(translate("unitLoadTypeCartonA", locale), 0.18, 0.25,
				0.35, 12, 0.2);
		cartonUnitLoadTypeA.setUseFor(UnitLoadTypeUsages.PACKING, true);
		cartonUnitLoadTypeA.setUseFor(UnitLoadTypeUsages.SHIPPING, true);
		cartonUnitLoadTypeA.setUseFor(UnitLoadTypeUsages.PICKING, true);
		UnitLoadType cartonUnitLoadTypeB = createUnitLoadType(translate("unitLoadTypeCartonB", locale), 0.35, 0.4, 0.6,
				30, 0.3);
		cartonUnitLoadTypeB.setUseFor(UnitLoadTypeUsages.PACKING, true);
		cartonUnitLoadTypeB.setUseFor(UnitLoadTypeUsages.SHIPPING, true);
		cartonUnitLoadTypeB.setUseFor(UnitLoadTypeUsages.PICKING, true);

		log.info("Create TypeCapacityConstraints...");
		createCapacityConstraint(defaultLocationType, euroUnitLoadType, 100, 0);
		createCapacityConstraint(shelfLocationType, box60UnitLoadType, 100, 0);
		createCapacityConstraint(shelfLocationType, box30UnitLoadType, 50, 0);
		createCapacityConstraint(blockLocationType, euroUnitLoadType, 5, 1);

		log.info("Create LocationsClusters...");
		LocationCluster palletCluster = createLocationCluster(translate("palletstore", locale));
		LocationCluster shelfCluster = createLocationCluster(translate("shelfstore", locale));
		LocationCluster highbayCluster = createLocationCluster(translate("highbay", locale));
		LocationCluster fixedCluster = createLocationCluster(translate("fixed", locale));
		LocationCluster blockCluster = createLocationCluster(translate("blockStore", locale));

		log.info("Create StorageLocations...");
		Area storageArea = areaService.getDefault();

		List<StorageLocation> palletLocations = new ArrayList<>();
		palletLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, palletCluster, aZone, "P1", 4, 3, 3));
		palletLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, palletCluster, aZone, "P2", 4, 3, 3));
		palletLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, palletCluster, aZone, "P3", 4, 3, 3));
		palletLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, palletCluster, aZone, "P4", 4, 3, 3));
		List<StorageLocation> shelfLocations = new ArrayList<>();
		shelfLocations.addAll(
				createLocations(systemClient, storageArea, shelfLocationType, shelfCluster, aZone, "S1", 4, 6, 4));
		shelfLocations.addAll(
				createLocations(systemClient, storageArea, shelfLocationType, shelfCluster, aZone, "S2", 4, 6, 4));
		shelfLocations.addAll(
				createLocations(systemClient, storageArea, shelfLocationType, shelfCluster, bZone, "S3", 4, 6, 4));
		shelfLocations.addAll(
				createLocations(systemClient, storageArea, shelfLocationType, shelfCluster, cZone, "S4", 4, 6, 4));
		List<StorageLocation> highbayLocations = new ArrayList<>();
		highbayLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, highbayCluster, aZone, "H1", 6, 3, 8));
		highbayLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, highbayCluster, aZone, "H2", 6, 3, 8));
		highbayLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, highbayCluster, aZone, "H3", 6, 3, 8));
		highbayLocations.addAll(
				createLocations(systemClient, storageArea, defaultLocationType, highbayCluster, aZone, "H4", 6, 3, 8));
		createLocations(systemClient, storageArea, systemLocationType, fixedCluster, aZone, "F1", 2, 3, 2);
		createLocations(systemClient, storageArea, blockLocationType, blockCluster, bZone, "B", 6, 1, 1);
		manager.flush();
		manager.clear();

		log.info("Create ItemData...");
		List<ItemData> papers = new ArrayList<>();
		List<ItemData> printers = new ArrayList<>();
		List<ItemData> paints = new ArrayList<>();
		List<ItemData> screws = new ArrayList<>();

		String printerTypeName = translate("productTypePrinter", locale);
		int printerNumber = 14008559;
		int paintNumber = 32408001;
		int eanCounter = 1925847;
		for (int manufacturer = 0; manufacturer < 8; manufacturer++) {
			String manufacturerName = translate("manufacturer" + manufacturer, locale);
			for (int model = 0; model < 7; model++) {
				printerNumber += 17;
				int type = model % 3;
				String printerName = manufacturerName + " " + manufacturerName.substring(0, 1) + "X-" + (model + 1);
				eanCounter += 31;
				String eanCode = "41246" + eanCounter;
				eanCode += checkDigitService.calculateCheckDigit(eanCode, "modulo10");
				ItemData itemData = createItemData(systemClient, "" + printerNumber,
						printerTypeName + " " + printerName, printerTypeName, defaultUnit, false, false, aZone,
						euroUnitLoadType, eanCode, 0.35, 0.4, 0.5, 4.3);
				printers.add(itemData);
				for (int paint = 0; paint < (type * 3 + 3); paint++) {
					paintNumber += 31;
					eanCounter += 31;
					String paintName = translate("paint" + paint, locale);
					String paintTypeName = translate("paintType" + type, locale);
					String paintEanCode = "41248" + eanCounter;
					paintEanCode += checkDigitService.calculateCheckDigit(paintEanCode, "modulo10");
					double depth = (paint < 6 ? 0.12 : 0.24);
					itemData = createItemData(systemClient, "" + paintNumber,
							paintTypeName + " " + printerName + " " + paintName, paintTypeName, defaultUnit, false,
							false, aZone, box60UnitLoadType, paintEanCode, 0.05, 0.1, depth, 0.1);
					paints.add(itemData);
				}
			}
		}

		String paperTypeName = translate("productTypePaper", locale);
		ItemData paper1 = createItemData(systemClient, "56942315", translate("paper0", locale), paperTypeName, packUnit,
				false, false, aZone, euroUnitLoadType, "4124467890123", 0.05, 0.22, 0.3, 2.6);
		papers.add(paper1);
		createPackaging(paper1, translate("unitCarton", locale), 5, "4124467890131");
		ItemData paper2 = createItemData(systemClient, "56944711", translate("paper1", locale), paperTypeName, packUnit,
				false, false, aZone, euroUnitLoadType, "4124467890711", 0.06, 0.22, 0.3, 3.8);
		papers.add(paper2);
		createPackaging(paper2, translate("unitCarton", locale), 5, "4124467890745");

		String screwTypeName = translate("productTypeScrew", locale);
		ItemData screw1 = createItemData(systemClient, "65540321", translate("screw0", locale), screwTypeName, gramUnit,
				false, false, aZone, box60UnitLoadType, null, 0.02, 0.01, 0.01, 0.001);
		screws.add(screw1);
		createPackaging(screw1, translate("unitCarton", locale), 100, "4124467890843");
		ItemData screw2 = createItemData(systemClient, "65544342", translate("screw1", locale), screwTypeName, gramUnit,
				false, false, aZone, box60UnitLoadType, null, 0.02, 0.01, 0.01, 0.001);
		screws.add(screw2);
		createPackaging(screw2, translate("unitCarton", locale), 150, "4124467890861");
		manager.flush();
		manager.clear();

		log.info("Create FixedLocations...");
		createFixLocation("F1-011-1", paper1, 201, 400);
		createFixLocation("F1-013-1", paper2, 11, 210);
		manager.flush();
		manager.clear();

		log.info("Create Stock...");
		int unitLoadNumber = 0;

		createStock(systemClient, "F1-011-1", paper1, 200, "F1-011-1", null, translate("unitCarton", locale));
		createStock(systemClient, "F1-013-1", paper2, 7, "F1-013-1", null, translate("unitCarton", locale));

		palletLocations.sort(new LocationComparator());
		Iterator<StorageLocation> locations = palletLocations.iterator();
		int numPalletStocks = 0;
		for (ItemData itemData : printers) {
			for (int i = 0; i < 4; i++) {
				createStock(systemClient, locations.next().getName(), itemData, 8,
						String.format("%06d", ++unitLoadNumber), null, null);
				numPalletStocks++;
			}
			if (numPalletStocks > 100) {
				break;
			}
		}

		for (ItemData itemData : papers) {
			for (int i = 0; i < 16; i++) {
				createStock(systemClient, locations.next().getName(), itemData, 200,
						String.format("%06d", ++unitLoadNumber), null, translate("unitCarton", locale));
			}
		}

		// Adjust sequence to match manual generated unit load numbers
		sequenceBusiness.readNextValue(UnitLoad.class, "labelId");

		highbayLocations.sort(new LocationComparator());
		locations = highbayLocations.iterator();
		for (ItemData itemData : papers) {
			for (int i = 0; i < 40; i++) {
				createStock(systemClient, locations.next().getName(), itemData, 200,
						String.format("%06d", ++unitLoadNumber), null, translate("unitCarton", locale));
			}
		}
		for (ItemData itemData : printers) {
			for (int i = 0; i < 4; i++) {
				createStock(systemClient, locations.next().getName(), itemData, 8,
						String.format("%06d", ++unitLoadNumber), null, null);
			}
		}

		// Adjust sequence to match manual generated unit load numbers
		sequenceBusiness.readNextValue(UnitLoad.class, "labelId");

		shelfLocations.sort(new LocationComparator());
		locations = shelfLocations.iterator();
		int numShelfStocks = 0;
		for (ItemData itemData : paints) {
			for (int i = 0; i < 4; i++) {
				createStock(systemClient, locations.next().getName(), itemData, 12,
						String.format("%06d", ++unitLoadNumber), null, null);
				numShelfStocks++;
			}
			if (numShelfStocks > 300) {
				break;
			}
		}

		for (ItemData itemData : screws) {
			for (int i = 0; i < 4; i++) {
				createStock(systemClient, locations.next().getName(), itemData, 15000,
						String.format("%06d", ++unitLoadNumber), null, translate("unitCarton", locale));
			}
		}

		// Adjust sequence to match manual generated unit load numbers
		sequenceBusiness.readNextValue(UnitLoad.class, "labelId");

		log.info("Create Goods In...");
		createAdvice(systemClient, new ItemDataAmount(printers.get(0), 16), new ItemDataAmount(printers.get(1), 16));
		createAdvice(systemClient, new ItemDataAmount(papers.get(0), 2000), new ItemDataAmount(papers.get(1), 2000));
		createAdvice(systemClient, new ItemDataAmount(paints.get(0), 120), new ItemDataAmount(paints.get(1), 120));
		createAdvice(systemClient, new ItemDataAmount(paints.get(2), 120), new ItemDataAmount(paints.get(3), 120),
				new ItemDataAmount(paints.get(4), 120), new ItemDataAmount(paints.get(5), 120));

		log.info("Create Goods Out...");
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(printers.get(0), 1));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(printers.get(0), 1),
				new ItemDataAmount(papers.get(0), 2));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(printers.get(1), 1),
				new ItemDataAmount(papers.get(0), 2), new ItemDataAmount(paints.get(0), 2));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(papers.get(0), 2),
				new ItemDataAmount(paints.get(0), 1));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(papers.get(0), 2),
				new ItemDataAmount(paints.get(1), 2));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(papers.get(0), 3),
				new ItemDataAmount(paints.get(9), 1), new ItemDataAmount(paints.get(12), 1),
				new ItemDataAmount(paints.get(13), 1), new ItemDataAmount(paints.get(14), 1));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(papers.get(0), 10));
		createDeliveryOrder(systemClient, locale, new ItemDataAmount(papers.get(1), 3));

		log.info("Done. duration=" + (System.currentTimeMillis() - start));
	}

	private ItemUnit createItemUnit(String name, ItemUnitType unitType) throws BusinessException {
		ItemUnit itemUnit = itemUnitService.readByName(name);
		if (itemUnit == null) {
			itemUnit = itemUnitService.create(name);
		}
		itemUnit.setUnitType(unitType);

		return itemUnit;
	}

	private Zone createZone(String name) throws BusinessException {
		Zone zone = zoneService.read(name);
		if (zone == null) {
			zone = zoneService.create(name);
		}

		return zone;
	}

	private LocationType createLocationType(String name) throws BusinessException {
		LocationType locationType = locationTypeService.readByName(name);
		if (locationType == null) {
			locationType = locationTypeService.create(name);
		}

		return locationType;
	}

	private UnitLoadType createUnitLoadType(String name, double height, double width, double depth,
			double liftingCapacity, double weight) throws BusinessException {
		UnitLoadType unitLoadType = unitLoadTypeService.readByName(name);
		if (unitLoadType == null) {
			unitLoadType = unitLoadTypeService.create(name);
		}

		unitLoadType.setDepth(BigDecimal.valueOf(depth));
		unitLoadType.setWidth(BigDecimal.valueOf(width));
		unitLoadType.setHeight(BigDecimal.valueOf(height));
		unitLoadType.setWeight(BigDecimal.valueOf(weight));
		unitLoadType.setLiftingCapacity(BigDecimal.valueOf(liftingCapacity));

		return unitLoadType;
	}

	private TypeCapacityConstraint createCapacityConstraint(LocationType locationType, UnitLoadType unitLoadType,
			double allocation, int orderIndex) throws BusinessException {
		TypeCapacityConstraint constraint = typeCapacityEntityService.read(locationType, unitLoadType);
		if (constraint == null) {
			constraint = typeCapacityEntityService.create(locationType, unitLoadType, BigDecimal.valueOf(allocation));
		}
		constraint.setOrderIndex(orderIndex);

		return constraint;
	}

	private LocationCluster createLocationCluster(String name) throws BusinessException {
		LocationCluster cluster = locationClusterService.readByName(name);
		if (cluster == null) {
			cluster = locationClusterService.create(name);
		}
		return cluster;
	}

	private List<StorageLocation> createLocations(Client client, Area area, LocationType type, LocationCluster cluster,
			Zone zone, String rackName, int numFields, int numPosInField, int numLevels) {
		List<StorageLocation> locations = new ArrayList<>();
		int xPos = 0;
		for (int field = 0; field < numFields; field++) {
			for (int posInField = 0; posInField < numPosInField; posInField++) {
				xPos++;
				for (int level = 0; level < numLevels; level++) {
					String fieldName = "";
					String sectionName = "";
					String locationName = rackName + "-" + String.format("%1$02d", field + 1);
					if (numPosInField > 1) {
						fieldName = locationName;
						sectionName = locationName;
						locationName += String.format("%1$01d", posInField + 1);
					}
					if (numLevels > 1) {
						fieldName += "-" + String.format("%1$01d", level + 1);
						locationName += "-" + String.format("%1$01d", level + 1);
					}

					StorageLocation location = locationService.readByName(locationName);
					if (location == null) {
						location = locationService.create(locationName, client, type, area, cluster);
					}
					location.setRack(rackName);
					if (!StringUtils.isBlank(fieldName)) {
						location.setField(fieldName);
					}
					if (!StringUtils.isBlank(sectionName)) {
						location.setSection(sectionName);
					}
					location.setZone(zone);
					location.setYPos(level + 1);
					location.setXPos(xPos);
					locations.add(location);
				}
			}
		}
		manager.flush();
		manager.clear();

		return locations;
	}

	private ItemData createItemData(Client client, String number, String name, String tradeGroup, ItemUnit unit,
			boolean lotMandatory, boolean serialMandatory, Zone zone, UnitLoadType unitLoadType, String ean,
			double height, double width, double depth, double weight) throws BusinessException {

		ItemData itemData = itemDataService.readByNumber(number);
		if (itemData == null) {
			itemData = itemDataService.create(client, number, name, unit);
		}

		itemData.setDescription(name);
		itemData.setLotMandatory(lotMandatory);
		itemData.setSerialNoRecordType(
				serialMandatory ? SerialNoRecordType.GOODS_OUT_RECORD : SerialNoRecordType.NO_RECORD);
		itemData.setZone(zone);
		itemData.setTradeGroup(tradeGroup);
		itemData.setDefaultUnitLoadType(unitLoadType);
		itemData.setHeight(BigDecimal.valueOf(height));
		itemData.setWidth(BigDecimal.valueOf(width));
		itemData.setDepth(BigDecimal.valueOf(depth));
		itemData.setWeight(BigDecimal.valueOf(weight));

		if (!StringUtils.isBlank(ean)) {
			ItemDataNumber existingEan = eanService.read(itemData, ean);
			if (existingEan == null) {
				eanService.create(itemData, ean);
			}
		}
		return itemData;
	}

	private PackagingUnit createPackaging(ItemData itemData, String name, double amount, String ean)
			throws BusinessException {
		PackagingUnit packagingUnit = packagingService.read(itemData, name);
		if (packagingUnit == null) {
			packagingUnit = packagingService.create(itemData, name, BigDecimal.valueOf(amount));
			if (!StringUtils.isBlank(ean)) {
				ItemDataNumber existingEan = eanService.read(itemData, ean);
				if (existingEan == null) {
					ItemDataNumber itemDataNumber = eanService.create(itemData, ean);
					itemDataNumber.setPackagingUnit(packagingUnit);
				}
			}
		}
		return packagingUnit;
	}

	private FixAssignment createFixLocation(String locationName, ItemData itemData, double minAmount, double maxAmount)
			throws BusinessException {
		StorageLocation location = locationService.readByName(locationName);
		if (location == null) {
			return null;
		}

		fixedService.readByItemData(itemData).forEach(entity -> manager.remove(entity));
		fixedService.readByLocation(location).forEach(entity -> manager.remove(entity));

		FixAssignment fixAssignment = fixedService.create(itemData, location);
		fixAssignment.setMinAmount(new BigDecimal(minAmount));
		fixAssignment.setMaxAmount(new BigDecimal(maxAmount));

		location.setLocationType(locationTypeService.getSystem());

		return fixAssignment;
	}

	private void createStock(Client client, String locationName, ItemData itemData, double amount,
			String unitLoadNumber, String lotNumber, String packagingUnitName) throws BusinessException {
		StorageLocation location = locationService.readByName(locationName);
		if (location == null) {
			log.error("Location not found. Do not create stock. locationName=" + locationName);
			return;
		}

		PackagingUnit packagingUnit = null;
		if (!StringUtils.isBlank(packagingUnitName)) {
			packagingUnit = packagingService.read(itemData, packagingUnitName);
		}

		UnitLoad unitLoad = unitLoadService.readByLabel(unitLoadNumber);
		if (unitLoad != null) {
			log.error("Do not create UnitLoad " + unitLoadNumber + " twice");
			return;
		}

		UnitLoadType unitLoadType = itemData.getDefaultUnitLoadType();
		if (unitLoadType == null) {
			unitLoadType = unitLoadTypeService.getDefault();
		}

		unitLoad = inventoryBusiness.createUnitLoad(client, unitLoadNumber, unitLoadType, location, StockState.ON_STOCK,
				null, null, null);
		inventoryBusiness.createStock(unitLoad, itemData, BigDecimal.valueOf(amount), lotNumber, null, null,
				packagingUnit, StockState.ON_STOCK, null, null, null, true);
		manager.flush();
		manager.clear();
	}

	private void createAdvice(Client client, ItemDataAmount... lines) throws BusinessException {
		Advice advice = adviceBusiness.createOrder(client, null);
		advice.setDeliveryDate(new Date());
		for (ItemDataAmount line : lines) {
			adviceBusiness.addOrderLine(advice, line.itemData, null, BigDecimal.valueOf(line.amount), 1);
		}
	}

	private void createDeliveryOrder(Client client, Locale locale, ItemDataAmount... lines) {
		try {
			StorageLocation location = null;
			List<StorageLocation> locationList = locationService.getForGoodsOut(null);
			if (!locationList.isEmpty()) {
				location = locationList.get(0);
			}

			OrderStrategy strategy = orderStrategyEntityService.getDefault(client);

			DeliveryOrder order = deliveryOrderService.create(client, strategy);
			order.setDeliveryDate(new Date());
			order.setDestination(location);
			order.setPrio(OrderPrio.NORMAL);

			Address address = new Address();
			address.setFirstName(translate("addressFirstName", locale));
			address.setLastName(translate("addressLastName", locale));
			address.setStreet(translate("addressStreet", locale));
			address.setZipCode(translate("addressZipCode", locale));
			address.setCity(translate("addressCity", locale));
			order.setAddress(address);

			for (ItemDataAmount line : lines) {
				deliveryOrderLineService.create(order, line.itemData, null, BigDecimal.valueOf(line.amount), 1);
			}

			List<PickingOrderLine> picks = pickingPosGenerator.generatePicks(order, true);
			List<PickingOrder> pickingOrders = pickingOrderGenerator.generatePickingOrders(picks);
			for (PickingOrder pickingOrder : pickingOrders) {
				pickingOrder.setState(OrderState.PROCESSABLE);
			}

		} catch (Exception e) {
			log.error("Error creating Order: " + e.getMessage(), e);
		}
	}

	private String translate(String key, Locale locale) {
		return Translator.getString(Wms2BundleResolver.class, "Demo", key, "caption", locale);
	}

	private class LocationComparator implements Comparator<StorageLocation> {

		@Override
		public int compare(StorageLocation o1, StorageLocation o2) {
			if (o1.getYPos() > o2.getYPos()) {
				return 1;
			}
			if (o1.getYPos() < o2.getYPos()) {
				return -1;
			}
			if (o1.getXPos() > o2.getXPos()) {
				return 1;
			}
			if (o1.getXPos() < o2.getXPos()) {
				return -1;
			}
			int i = StringUtils.compare(o1.getRack(), o2.getRack());
			if (i != 0) {
				return i;
			}
			return StringUtils.compare(o1.getName(), o2.getName());
		}

	}

	private class ItemDataAmount {
		public ItemData itemData;
		public double amount;

		public ItemDataAmount(ItemData itemData, double amount) {
			this.itemData = itemData;
			this.amount = amount;
		}
	}
}
