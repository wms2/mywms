/* 
Copyright 2019-2022 Matthias Krane
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
package de.wms2.mywms.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.transport.TransportOrderEntityService;
import de.wms2.mywms.util.ListUtils;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Constants;
import de.wms2.mywms.util.Wms2Properties;

/**
 * @author krane
 *
 */
@Stateless
public class LocationFinderBean implements LocationFinder {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private TransportOrderEntityService relocateOrderService;
	@Inject
	private StorageStrategyEntityService strategyService;
	@Inject
	private LocationReserver locationReserver;
	@Inject
	private FixAssignmentEntityService fixService;
	@Inject
	private SystemPropertyBusiness systemPropertyBusiness;
	@Inject
	private ZoneEntityService zoneService;
	@Inject
	private StorageAreaEntityService storageAreaEntityService;

	private static int QUERY_LIMIT = 30;
	private Map<String, BigDecimal> fieldWeightBuffer;
	private Map<String, BigDecimal> sectionWeightBuffer;

	/**
	 * Find a location where the stock can be added.<br>
	 * The itemData and lot is considered.<br>
	 * 1. Search fix assigned location (maybe empty)<br>
	 * 2. Search other picking location. (FIFO)<br>
	 * The vetoStocks are not used
	 */
	@Override
	public StorageLocation findAddToLocation(StockUnit sourceStock, Collection<StockUnit> vetoStocks) {
		String logStr = "findAddToLocation ";
		logger.log(Level.INFO, logStr + "sourceStock=" + sourceStock + ", vetoStocks=" + vetoStocks);

		String lot = sourceStock.getLotNumber();
		Date bestBefore = sourceStock.getBestBefore();
		ItemData itemData = sourceStock.getItemData();

		if (sourceStock.getState() != StockState.ON_STOCK) {
			// Adding to existing stock is only valid for stock in state
			// ON_STOCK
			logger.log(Level.INFO, logStr + "Stock has no ON_STOCK state. Cannot Add-To something. stock-state="
					+ sourceStock.getState());
			return null;
		}

		// Try to use fix location
		List<FixAssignment> fixList = fixService.readByItemData(itemData);
		for (FixAssignment fix : fixList) {
			StorageLocation location = fix.getStorageLocation();
			if (location.isLocked()) {
				logger.log(Level.FINE, logStr + "Fix location is locked. location=" + location);
				continue;
			}
			Area area = location.getArea();
			if (!area.isUseFor(AreaUsages.PICKING)) {
				logger.log(Level.FINE, logStr + "Fix location is not for picking. location=" + location);
				continue;
			}

			boolean hasWrongLot = false;
			boolean hasVeto = false;
			List<StockUnit> stockList = stockUnitService.readByItemDataLocation(itemData, fix.getStorageLocation());
			for (StockUnit stock : stockList) {
				if (vetoStocks != null && vetoStocks.contains(stock)) {
					hasVeto = true;
				}
				if (!StringUtils.equals(stock.getLotNumber(), lot)) {
					hasWrongLot = true;
				}
			}
			if (hasWrongLot) {
				logger.log(Level.FINE, logStr + "Fix location has stock with wrong lot. location=" + location);
				continue;
			}
			if (hasVeto) {
				logger.log(Level.FINE, logStr + "Do not add to veto stock. location=" + location);
				continue;
			}
			logger.log(Level.INFO, logStr + "Found fix location. location=" + location);
			return fix.getStorageLocation();
		}

		// Try to use pickable stock
		List<StockUnit> stockList = readStockList(itemData, lot, bestBefore);
		for (StockUnit stock : stockList) {
			if (vetoStocks != null && vetoStocks.contains(stock)) {
				logger.log(Level.FINE, logStr + "Do not add to veto stock.");
				continue;
			}
			logger.log(Level.INFO,
					logStr + "Found Pickable stock. location=" + stock.getUnitLoad().getStorageLocation());
			return stock.getUnitLoad().getStorageLocation();
		}

		logger.log(Level.INFO, logStr + "Found nothing");
		return null;
	}

	/**
	 * Find a StorageLocation for a UnitLoad<br>
	 * The location can be used for normal storage
	 */
	@Override
	public StorageLocation findStorageLocation(UnitLoad unitLoad, StorageStrategy strategy) throws BusinessException {
		return findLocation(unitLoad, strategy, false);
	}

	/**
	 * Find a StorageLocation for a UnitLoad<br>
	 * The location can be used for picking
	 */
	@Override
	public StorageLocation findPickingLocation(UnitLoad unitLoad, StorageStrategy strategy) throws BusinessException {
		return findLocation(unitLoad, strategy, true);
	}

	@Override
	public StorageStrategy readStorageStrategy(UnitLoad unitLoad) {
		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		StorageStrategy strategy = readUnitLoadStrategy(stocksOnUnitLoad);
		return strategy;
	}

	private StorageLocation findLocation(UnitLoad unitLoad, StorageStrategy strategy, boolean pickingOnly)
			throws BusinessException {
		String logStr = "findLocation ";

		Date dateStart = new Date();

		// check locks on stock and source
		if (unitLoad.isLocked()) {
			logger.log(Level.WARNING, logStr + "Unitload is locked. cannot relocate. unitLoad=" + unitLoad);
			throw new BusinessException(Wms2BundleResolver.class, "LocationFinder.invalidUnitLoadIsLocked");
		}
		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		for (StockUnit stock : stocksOnUnitLoad) {
			if (stock.isLocked()) {
				logger.log(Level.WARNING,
						logStr + "Stock is locked. cannot relocate. stock=" + stock + ", unitLoad=" + unitLoad);
				throw new BusinessException(Wms2BundleResolver.class, "LocationFinder.invalidStockIsLocked");
			}
		}
		if (unitLoad.getStorageLocation().isLocked()) {
			logger.log(Level.WARNING,
					logStr + "Source location is locked. cannot relocate. location=" + unitLoad.getStorageLocation());
			throw new BusinessException(Wms2BundleResolver.class, "LocationFinder.invalidLocationIsLocked");
		}

		if (strategy == null) {
			strategy = readUnitLoadStrategy(stocksOnUnitLoad);
		}
		if (strategy == null) {
			strategy = strategyService.getDefault();
		}

		if (strategy.isManualSearch()) {
			logger.log(Level.INFO, logStr + "Do not search location for manual search strategy. unitLoad=" + unitLoad
					+ ", strategy=" + strategy);
			return null;
		}

		// itemData information is needed by some strategies. Mix different
		// itemDatas, near picking location,
		// replenishment, ...
		ItemData uniqueItemData = null;
		List<ItemData> itemData = new ArrayList<>();
		Date strategyDate = null;
		for (StockUnit su : stocksOnUnitLoad) {
			if (!itemData.contains(su.getItemData())) {
				itemData.add(su.getItemData());
			}
			if (strategyDate == null) {
				strategyDate = su.getStrategyDate();
			}
			if (su.getStrategyDate().compareTo(strategyDate) < 0) {
				strategyDate = su.getStrategyDate();
			}
		}
		if (itemData.size() == 1) {
			uniqueItemData = itemData.get(0);
		}
		if (!strategy.isMixItem() && uniqueItemData == null) {
			logger.log(Level.WARNING, logStr
					+ "No unique itemData on unitLoad. Cannot find location without mixing itemDatas. Abort. unitLoad="
					+ unitLoad + ", strategy=" + strategy);
			return null;
		}

		// Initialize unit load data
		BigDecimal unitLoadWeight = readUnitLoadWeight(unitLoad);

		StorageLocation location = null;

		Client stockClient = unitLoad.getClient();
		List<Client> clients = new ArrayList<Client>();
		clients.add(stockClient);
		if (!strategy.isOnlyClientLocation() && !stockClient.isSystemClient()) {
			Client systemClient = clientBusiness.getSystemClient();
			clients.add(systemClient);
		}

		// use itemData zone and its overflow zones
		// an empty array will search in all zones
		List<Zone> zones = new ArrayList<Zone>();
		if (strategy.getZone() != null) {
			zones.add(strategy.getZone());
		} else {
			zones = readZoneFlow(stocksOnUnitLoad, stockClient);
		}

		List<StorageArea> hiddenAreas = new ArrayList<>();
		List<StorageArea> storageAreas = storageAreaEntityService.readByStorageStrategy(strategy);
		if (!itemData.isEmpty() && !storageAreas.isEmpty() && strategy.isUseAreaStrategyDate()) {
			// Do not use areas beyond the one that contains material with older
			// strategy-date
			List<StorageArea> olderStockAreas = readOlderStockAreas(itemData, strategyDate, storageAreas);
			hiddenAreas.addAll(olderStockAreas);
			if (hiddenAreas.size() == storageAreas.size()) {
				logger.log(Level.INFO,
						logStr + "Found nothing. All areas are invalid. unitLoad = " + unitLoad + ", unitLoadType="
								+ unitLoad.getUnitLoadType() + ", strategy=" + strategy + ", unitLoadWeight="
								+ unitLoadWeight + ", itemData=" + itemData + ", strategyDate=" + strategyDate
								+ ", hiddenAreas=" + hiddenAreas + ", time="
								+ (new Date().getTime() - dateStart.getTime()));
				return null;
			}
			if (olderStockAreas.size() > 0) {
				logger.log(Level.INFO,
						logStr + "Hide areas with older stock. unitLoad = " + unitLoad + ", strategy=" + strategy
								+ ", itemData=" + itemData + ", strategyDate=" + strategyDate + ", olderStockAreas="
								+ olderStockAreas);
			}
		}

		if (!itemData.isEmpty()) {
			// Do not use areas, that have enough material
			List<StorageArea> fullAreas = readFullAreas(itemData, strategyDate);
			for (StorageArea fullArea : fullAreas) {
				if (!hiddenAreas.contains(fullArea)) {
					hiddenAreas.add(fullArea);
				}
			}
			if (fullAreas.size() > 0) {
				logger.log(Level.INFO, logStr + "Hide full areas. unitLoad = " + unitLoad + ", strategy=" + strategy
						+ ", itemData=" + itemData + ", fullAreas=" + fullAreas);
			}
		}

		location = findLocationInternal(clients, unitLoad, strategy, zones, unitLoadWeight, stocksOnUnitLoad,
				uniqueItemData, pickingOnly, storageAreas, hiddenAreas);
		if (location != null) {
			logger.log(Level.INFO, logStr + "Found location=" + location + ", unitload=" + unitLoad + ", unitLoadType="
					+ unitLoad.getUnitLoadType() + ", strategy=" + strategy + ", unitLoadWeight=" + unitLoadWeight
					+ ", clients=" + clients + ", zones=" + zones + ", uniqueItemData=" + uniqueItemData
					+ ", pickingOnly=" + pickingOnly + ", storageAreas=" + storageAreas + ", hiddenAreas=" + hiddenAreas
					+ ", time=" + (new Date().getTime() - dateStart.getTime()));
			return location;
		}

		logger.log(Level.INFO,
				logStr + "Found nothing, unitLoad=" + unitLoad + ", unitLoadType=" + unitLoad.getUnitLoadType()
						+ ", strategy=" + strategy + ", unitLoadWeight=" + unitLoadWeight + ", clients=" + clients
						+ ", zones=" + zones + ", uniqueItemData=" + uniqueItemData + ", pickingOnly=" + pickingOnly
						+ ", storageAreas=" + storageAreas + ", hiddenAreas=" + hiddenAreas + ", time="
						+ (new Date().getTime() - dateStart.getTime()));
		return null;
	}

	private List<Zone> readZoneFlow(List<StockUnit> stocksOnUnitLoad, Client client) {
		List<Zone> zones = new ArrayList<Zone>();

		String abcFlow = systemPropertyBusiness.getString(Wms2Properties.KEY_STRATEGY_ZONE_FLOW, client, null, null);
		List<String> flows = ListUtils.stringToList(abcFlow, ";");

		String startZoneName = Wms2Constants.UNDEFINED_ZONE_NAME;
		Zone unitLoadZone = readUnitLoadZone(stocksOnUnitLoad);
		if (unitLoadZone != null) {
			zones.add(unitLoadZone);
			startZoneName = unitLoadZone.getName();
		}

		for (String flow : flows) {
			List<String> zoneNames = ListUtils.stringToList(flow, ",");
			if (zoneNames == null || zoneNames.isEmpty()) {
				continue;
			}
			String firstZoneName = zoneNames.get(0);
			if (!StringUtils.equals(firstZoneName, startZoneName)) {
				continue;
			}
			for (String zoneName : zoneNames) {
				if (StringUtils.equals(zoneName, "NONE")) {
					continue;
				}
				Zone zone = zoneService.read(zoneName);
				if (zone == null) {
					continue;
				}
				if (!zones.contains(zone)) {
					zones.add(zone);
				}
			}
		}

		return zones;
	}

	private StorageLocation findLocationInternal(Collection<Client> clients, UnitLoad unitLoad,
			StorageStrategy strategy, Collection<Zone> zones, BigDecimal unitLoadWeight,
			Collection<StockUnit> stocksOnUnitLoad, ItemData itemData, boolean pickingOnly,
			List<StorageArea> storageAreas, List<StorageArea> hiddenAreas) throws BusinessException {
		String logStr = "findLocationInternal ";

		logger.log(Level.FINE,
				logStr + "clients=" + clients + ", unitLoad=" + unitLoad + ", strategy=" + strategy + ", zones=" + zones
						+ ", unitLoadWeight=" + unitLoadWeight + ", itemData=" + itemData + ", pickingOnly="
						+ pickingOnly + ", storageAreas=" + storageAreas + ", hiddenAreas=" + hiddenAreas
						+ ", unitLoadType=" + unitLoad.getUnitLoadType());

		fieldWeightBuffer = new HashMap<>();
		sectionWeightBuffer = new HashMap<>();

		StorageLocation location = null;

		if (strategy.isNearPickingLocation()) {
			Integer searchNearPositionX = null;
			String searchInRack = null;
			StorageLocation fixLocation = null;
			if (itemData != null) {
				FixAssignment fixAssignment = fixService.readFirstByItemData(itemData, true);
				if (fixAssignment != null) {
					fixLocation = fixAssignment.getStorageLocation();
					if (fixLocation != null) {
						searchInRack = fixLocation.getRack();
						if (searchInRack != null) {
							searchNearPositionX = fixLocation.getXPos();
						} else {
							logger.log(Level.WARNING, logStr
									+ "Fix assigned location has no rack attribute. Cannot search in near of this location. location="
									+ fixLocation);
						}
					}
				}
			}
			if (searchNearPositionX != null && !StringUtils.isBlank(searchInRack)) {
				logger.log(Level.INFO, logStr + "Search near of picking location. itemData=" + itemData + ", location="
						+ location + ", searchInRack=" + searchInRack + ", searchNearPositionX=" + searchNearPositionX);
				location = findLocationInternal(clients, unitLoad, strategy, storageAreas, hiddenAreas, zones,
						searchInRack, unitLoadWeight, stocksOnUnitLoad, itemData, pickingOnly, searchNearPositionX);
				if (location != null) {
					return location;
				}
			} else {
				logger.log(Level.INFO, logStr
						+ "No suitable fix assigned picking location or rack found to query nearPickingLocation. Ignore. itemData="
						+ itemData);
			}
		}

		location = findLocationInternal(clients, unitLoad, strategy, storageAreas, hiddenAreas, zones, null,
				unitLoadWeight, stocksOnUnitLoad, itemData, pickingOnly, null);
		if (location != null) {
			return location;
		}

		return null;
	}

	private StorageLocation findLocationInternal(Collection<Client> clients, UnitLoad unitLoad,
			StorageStrategy strategy, List<StorageArea> storageAreas, List<StorageArea> hiddenAreas,
			Collection<Zone> zones, String rack, BigDecimal unitLoadWeight, Collection<StockUnit> stocksOnUnitLoad,
			ItemData itemData, boolean pickingOnly, Integer nearPositionX) throws BusinessException {
		String logStr = "findLocationInternal ";
		int queryOffset = 0;
		int queryLimit = QUERY_LIMIT;

		// Do not read all candidates in one query. Usually one of the first
		// locations should be suitable
		while (true) {

			List<StorageLocation> locationList = readCandidates(clients, unitLoad, strategy, storageAreas, hiddenAreas,
					zones, rack, unitLoadWeight, pickingOnly, nearPositionX, queryOffset);

			for (StorageLocation location : locationList) {
				logger.log(Level.FINE, logStr + "check location=" + location);

				// Check weight
				if (!checkWeight(unitLoad, location)) {
					continue;
				}

				if (!locationReserver.checkAllocateLocation(location, unitLoad, true, false)) {
					logger.log(Level.FINE, logStr + "Location not allocatable. location=" + location);
					continue;
				}

				// Check storage orders for strategies with unique itemData on a
				// location
				if (!strategy.isMixItem() && itemData != null) {
					if (existsOtherItemData(location, itemData)) {
						logger.log(Level.INFO, logStr + "Not allowed to mix itemDatas. location=" + location
								+ ", itemData=" + itemData);
						continue;
					}

					boolean storageOk = true;
					List<TransportOrder> relocateOrderList = relocateOrderService.readOpen(location);
					for (TransportOrder relocateOrder : relocateOrderList) {
						UnitLoad relocateUnitLoad = relocateOrder.getUnitLoad();
						List<StockUnit> stocksOnRelocateUnitLoad = stockUnitService.readByUnitLoad(relocateUnitLoad);
						for (StockUnit stock : stocksOnRelocateUnitLoad) {
							if (!stock.getItemData().equals(itemData)) {
								logger.log(Level.FINE,
										logStr + "Location not usable, Wrong itemData in storage order. location="
												+ location + ", itemData (in transport)=" + stock.getItemData()
												+ ", itemData (requested)=" + itemData);
								storageOk = false;
								break;
							}
						}
					}
					if (!storageOk) {
						continue;
					}
				}

				logger.log(Level.FINE, logStr + "check done location=" + location);
				return location;
			}

			if (locationList.size() < queryLimit) {
				break;
			}
			queryOffset += queryLimit;
			queryLimit += QUERY_LIMIT;
		}

		return null;
	}

	private boolean checkWeight(UnitLoad unitLoad, StorageLocation location) {
		String logStr = "checkWeight ";

		if (location.getLocationType().getLiftingCapacity() == null
				&& location.getLocationType().getFieldLiftingCapacity() == null
				&& location.getLocationType().getSectionLiftingCapacity() == null) {
			// No weight restriction set
			return true;
		}

		BigDecimal unitLoadWeight = readUnitLoadWeight(unitLoad);
		if (unitLoadWeight == null) {
			logger.log(Level.FINE, logStr + "Weight check passed. UnitLoad has no weight. unitLoad=" + unitLoad
					+ ", location=" + location);
			return true;
		}

		BigDecimal locationWeight = null;
		BigDecimal fieldWeight = null;
		BigDecimal sectionWeight = null;

		if (location.getLocationType().getLiftingCapacity() != null) {
			if (unitLoadWeight.compareTo(location.getLocationType().getLiftingCapacity()) > 0) {
				logger.log(Level.FINE,
						"Weight of location exceeded. unitLoad=" + unitLoad + ", location=" + location
								+ ", weight of unitLoad=" + unitLoadWeight + ", lifting capacity="
								+ location.getLocationType().getLiftingCapacity());
				return false;
			}

			locationWeight = summarizeWeight(unitLoad, unitLoadService.readByLocation(location),
					relocateOrderService.readOpen(location));
			locationWeight = locationWeight.add(unitLoadWeight);
			if (locationWeight.compareTo(location.getLocationType().getLiftingCapacity()) > 0) {
				logger.log(Level.FINE,
						"Weight of location exceeded. unitLoad=" + unitLoad + ", location=" + location
								+ ", weight on location=" + locationWeight + ", lifting capacity="
								+ location.getLocationType().getLiftingCapacity());
				return false;
			}
		}

		if (location.getLocationType().getFieldLiftingCapacity() != null && !StringUtils.isBlank(location.getField())) {
			fieldWeight = fieldWeightBuffer.get(location.getField());
			if (fieldWeight == null) {
				fieldWeight = summarizeWeight(unitLoad, unitLoadService.readByField(location.getField()),
						relocateOrderService.readOpenByField(location.getField()));
				fieldWeightBuffer.put(location.getField(), fieldWeight);
			}
			fieldWeight = fieldWeight.add(unitLoadWeight);
			if (fieldWeight.compareTo(location.getLocationType().getFieldLiftingCapacity()) > 0) {
				logger.log(Level.FINE,
						"Weight of field exceeded. unitLoad=" + unitLoad + ", location=" + location + ", field="
								+ location.getField() + ", weight on field=" + fieldWeight + ", lifting capacity="
								+ location.getLocationType().getFieldLiftingCapacity());
				return false;
			}
		}

		if (location.getLocationType().getSectionLiftingCapacity() != null
				&& !StringUtils.isBlank(location.getSection())) {
			sectionWeight = sectionWeightBuffer.get(location.getSection());
			if (sectionWeight == null) {
				sectionWeight = summarizeWeight(unitLoad, unitLoadService.readBySection(location.getSection()),
						relocateOrderService.readOpenBySection(location.getSection()));
				sectionWeightBuffer.put(location.getSection(), sectionWeight);
			}
			sectionWeight = sectionWeight.add(unitLoadWeight);
			if (sectionWeight.compareTo(location.getLocationType().getSectionLiftingCapacity()) > 0) {
				logger.log(Level.FINE,
						"Weight of section exceeded. unitLoad=" + unitLoad + ", location=" + location + ", section="
								+ location.getSection() + ", weight on section=" + sectionWeight + ", lifting capacity="
								+ location.getLocationType().getSectionLiftingCapacity());
				return false;
			}
		}

		logger.log(Level.FINE,
				logStr + "Weight check passed. unitLoad=" + unitLoad + ", location=" + location
						+ ", weight on location=" + locationWeight + "/"
						+ location.getLocationType().getLiftingCapacity() + ", weight on field=" + fieldWeight + "/"
						+ location.getLocationType().getFieldLiftingCapacity() + ", weight on section=" + sectionWeight
						+ "/" + location.getLocationType().getSectionLiftingCapacity());

		return true;
	}

	// summarize all weights excluded the given unitLoad
	private BigDecimal summarizeWeight(UnitLoad excludedUnitLoad, Collection<UnitLoad> unitLoadsOnSection,
			Collection<TransportOrder> transportsToSection) {
		BigDecimal sumWeight = BigDecimal.ZERO;

		for (UnitLoad unitLoadOnSection : unitLoadsOnSection) {
			if (unitLoadOnSection.equals(excludedUnitLoad)) {
				continue;
			}
			if (unitLoadOnSection.getWeight() != null) {
				sumWeight = sumWeight.add(unitLoadOnSection.getWeight());
			}
		}

		for (TransportOrder transportToSection : transportsToSection) {
			UnitLoad transportUnitLoad = transportToSection.getUnitLoad();
			if (transportUnitLoad == null || transportUnitLoad.equals(excludedUnitLoad)) {
				continue;
			}
			BigDecimal relocateWeight = readUnitLoadWeight(transportUnitLoad);
			if (relocateWeight != null) {
				sumWeight = sumWeight.add(relocateWeight);
			}
		}

		return sumWeight;
	}

	@SuppressWarnings("unchecked")
	private List<StorageLocation> readCandidates(Collection<Client> clients, UnitLoad unitLoad,
			StorageStrategy strategy, Collection<StorageArea> storageAreas, List<StorageArea> hiddenAreas,
			Collection<Zone> zones, String rack, BigDecimal weight, boolean pickingOnly, Integer nearPositionX,
			int offset) throws BusinessException {
		String logStr = "readCandidates ";
		String paramLog = "";

		boolean searchForStrategyArea = hasClusters(storageAreas);

		String jpql = " SELECT location, allocationRule FROM ";
		jpql += StorageLocation.class.getName() + " location ";
		jpql += "left outer join location.zone as zone ";
		jpql += "left join location.locationType as locationType ";
		jpql += "left join location.area as area, ";
		jpql += TypeCapacityConstraint.class.getName() + " allocationRule";
		if (searchForStrategyArea) {
			jpql += "," + StorageStrategyArea.class.getSimpleName() + " strategyArea";
		}

		// Search only the given areas/clusters. If no areas are given, search in
		// all clusters
		if (searchForStrategyArea) {
			jpql += " where strategyArea.storageStrategy = :strategy";
			jpql += " and strategyArea.storageArea in (:storageAreas)";
			jpql += " and location.locationCluster member of strategyArea.storageArea.locationClusters";
		} else {
			jpql += " WHERE location.locationCluster is not null";
		}

		// Search in the given rack
		if (!StringUtils.isBlank(rack)) {
			jpql += " AND location.rack=:rack ";
		}

		// Join all valid allocationRule
		jpql += " AND allocationRule.unitLoadType=:unitLoadType AND allocationRule.locationType=locationType";

		// Do not query fixed locations
		jpql += " AND not exists(";
		jpql += "   select 1 from " + FixAssignment.class.getName() + " fix";
		jpql += "   where fix.storageLocation=location";
		jpql += " ) ";

		// Locations must not be complete allocated
		jpql += " AND location.allocation<100 ";

		if (pickingOnly) {
			// Search locations for picking
			jpql += " AND area.usages like '%" + AreaUsages.PICKING + "%' ";
		} else {
			// Search locations for storage
			jpql += " AND area.usages like '%" + AreaUsages.STORAGE + "%' ";
		}

		// Do not search locked locations
		jpql += " AND location.lock=0";

		// Search only the given clients
		jpql += " AND (location.client in (:clients)) ";

		// Do not search full marked locations
		jpql += " AND location.allocationState=0";

		// The lifting capacity of the location must be smaller than the given
		// additional weight.
		// Details will be checked later on.
		if (weight != null) {
			jpql += " AND (locationType.liftingCapacity is null or locationType.liftingCapacity >= :weight) ";
			jpql += " AND (locationType.fieldLiftingCapacity is null or locationType.fieldLiftingCapacity >= :weight) ";
			jpql += " AND (locationType.sectionLiftingCapacity is null or locationType.sectionLiftingCapacity >= :weight) ";
		}

		// Search only the given zones
		if (zones != null && zones.size() > 0) {
			jpql += " AND location.zone in(:zones) ";
		}

		// check new allocationRule
		// Some more details are checked later on
		jpql += " AND ( ";
		jpql += "      (allocationRule.allocation>0 and allocationRule.allocation <= 100";
		jpql += "       and location.allocation<=(100-allocationRule.allocation) ) ";
		jpql += "   or (allocationRule.allocation>100";
		jpql += "       and location.allocation=0 ";
		jpql += "       and location.field is not null and location.field!='' ";
		jpql += "       and exists(";
		jpql += "           select 1 from " + StorageLocation.class.getName() + " neighbor ";
		jpql += "           where neighbor.id!=location.id";
		jpql += "             and neighbor.field=location.field";
		jpql += "             and neighbor.locationCluster=location.locationCluster";
		jpql += "             and neighbor.YPos=location.YPos";
		jpql += "             and neighbor.ZPos=location.ZPos";
		jpql += "             and neighbor.allocation<100)";
		jpql += "      ) ";
		jpql += " ) ";

		if (!strategy.isMixClient()) {
			// Do not mix clients of the stock units on a location.
			jpql += " AND NOT EXISTS (";
			jpql += "   select 1 from " + UnitLoad.class.getName() + " otherUnitLoad";
			jpql += "   where otherUnitLoad.storageLocation=location and otherUnitLoad.client!=:stockClient";
			jpql += " ) ";
			// Check clients of already existing transports too
			jpql += " AND NOT EXISTS (";
			jpql += "   select 1 from " + TransportOrder.class.getName() + " otherRelocateOrder ";
			jpql += "   where otherRelocateOrder.destinationLocation=location and otherRelocateOrder.state<:finished ";
			jpql += "     and otherRelocateOrder.unitLoad.client!=:stockClient";
			jpql += " ) ";
		}

		if (!hiddenAreas.isEmpty()) {
			jpql += " and not exists(select 1 from " + StorageArea.class.getSimpleName() + " hiddenArea";
			jpql += " where hiddenArea in (:hiddenAreas)";
			jpql += " and location.locationCluster member of hiddenArea.locationClusters)";
		}

		jpql += " ORDER BY ";
		if (nearPositionX != null) {
			jpql += "abs(location.XPos-:XPos), ";
		}
		if (strategy.isUseAreaStrategyDate() && searchForStrategyArea) {
			jpql += "strategyArea.orderIndex, ";
		}

		boolean orderByName = false;
		for (String orderby : ListUtils.stringToArray(strategy.getSorts())) {
			if (StringUtils.equals(orderby, StorageStrategySortType.ALLOCATION.name())) {
				jpql += "location.allocation desc, ";
			} else if (StringUtils.equals(orderby, StorageStrategySortType.POSITION_X.name())) {
				jpql += "location.XPos, ";
			} else if (StringUtils.equals(orderby, StorageStrategySortType.POSITION_Y.name())) {
				jpql += "location.YPos, ";
			} else if (StringUtils.equals(orderby, StorageStrategySortType.ORDERINDEX.name())) {
				jpql += "location.orderIndex, ";
			} else if (StringUtils.equals(orderby, StorageStrategySortType.NAME.name())) {
				jpql += "location.name, ";
				orderByName = true;
			} else if (StringUtils.equals(orderby, StorageStrategySortType.CAPACITY.name())) {
				jpql += "allocationRule.orderIndex, ";
			} else if (StringUtils.equals(orderby, StorageStrategySortType.ZONE.name())) {
				if (zones != null && zones.size() > 0) {
					jpql += "case";
					int i = 0;
					for (Zone zone : zones) {
						if (zone == null) {
							jpql += " when zone is null then " + i;
						} else {
							jpql += " when zone.id=" + zone.getId() + " then " + i;
						}
						i++;
					}
					jpql += " else " + i + " end, ";
				}
			} else if (StringUtils.equals(orderby, StorageStrategySortType.CLIENT.name())) {
				jpql += "case";
				int i = 0;
				for (Client client : clients) {
					jpql += " when location.client.id=" + client.getId() + " then " + i;
					i++;
				}
				jpql += " else " + i + " end, ";
			} else if (StringUtils.equals(orderby, StorageStrategySortType.STORAGEAREA.name())) {
				if (!strategy.isUseAreaStrategyDate() && searchForStrategyArea) {
					// do not add twice
					jpql += "strategyArea.orderIndex, ";
				}
			} else {
				logger.log(Level.WARNING, logStr + "Unknown order switch. sortType=" + orderby);
			}
		}

		if (!orderByName) {
			jpql += "location.name, ";
		}
		jpql += "location.id ";

		Query query = manager.createQuery(jpql);

		if (searchForStrategyArea) {
			query.setParameter("strategy", strategy);
			paramLog += ", strategy=" + strategy;
			query.setParameter("storageAreas", storageAreas);
			paramLog += ", storageAreas=" + storageAreas;
		}

		query.setParameter("unitLoadType", unitLoad.getUnitLoadType());
		paramLog += ", unitLoadType=" + unitLoad.getUnitLoadType();

		query.setParameter("clients", clients);
		paramLog += ", clients=" + clients;

		if (!strategy.isMixClient()) {
			query.setParameter("stockClient", unitLoad.getClient());
			paramLog += ", stockClient=" + unitLoad.getClient();
			query.setParameter("finished", OrderState.FINISHED);
		}

		if (weight != null) {
			query.setParameter("weight", weight);
			paramLog += ", weight=" + weight;

		}
		if (zones != null && zones.size() > 0) {
			query.setParameter("zones", zones);
			paramLog += ", zones=" + zones;

		}
		if (nearPositionX != null) {
			query.setParameter("XPos", nearPositionX);
		}
		if (!StringUtils.isBlank(rack)) {
			query.setParameter("rack", rack);
			paramLog += ", rack=" + rack;
		}
		if (!hiddenAreas.isEmpty()) {
			query.setParameter("hiddenAreas", hiddenAreas);
			paramLog += ", hiddenAreas=" + hiddenAreas;
		}
		query.setFirstResult(offset);
		paramLog += ", offset=" + offset;
		query.setMaxResults(QUERY_LIMIT);

		List<Object[]> results = query.getResultList();
		if (results.size() == 0) {
			logger.log(Level.FINE, logStr + "Unsuccessful search location Query=" + jpql + " -- Paramater=" + paramLog);
		} else {
			logger.log(Level.FINER, logStr + "Search location Query=" + jpql + " -- Paramater=" + paramLog);
		}

		List<StorageLocation> locations = new ArrayList<>(results.size());
		for (Object[] result : results) {
			StorageLocation location = (StorageLocation) result[0];
			locations.add(location);
		}

		return locations;

	}

	private boolean hasClusters(Collection<StorageArea> areas) {
		for (StorageArea area : areas) {
			if (!area.getLocationClusters().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<StockUnit> readStockList(ItemData itemData, String lotNumber, Date bestBefore) {
		String logStr = "readStockList ";

		String jpql = " SELECT stock FROM " + StockUnit.class.getName() + " stock, ";
		jpql += StorageLocation.class.getName() + " location, ";
		jpql += Area.class.getName() + " area ";
		jpql += " WHERE location=stock.unitLoad.storageLocation ";
		jpql += " AND area = location.area";
		jpql += " AND stock.itemData=:itemData";
		if (!StringUtils.isBlank(lotNumber)) {
			jpql += " AND stock.lotNumber=:lotNumber";
		}
		if (bestBefore != null) {
			jpql += " AND stock.bestBefore=:bestBefore";
		}
		jpql += " AND stock.state=" + StockState.ON_STOCK;
		jpql += " AND area.usages like '%" + AreaUsages.PICKING + "%' ";
		jpql += " AND location.lock=0";
		jpql += " AND stock.lock=0";
		jpql += " ORDER BY stock.strategyDate, stock.amount, stock.created";

		Query query = manager.createQuery(jpql);

		query.setParameter("itemData", itemData);
		if (!StringUtils.isBlank(lotNumber)) {
			query.setParameter("lotNumber", lotNumber);
		}
		if (bestBefore != null) {
			query.setParameter("bestBefore", bestBefore);
		}

		List<StockUnit> result = query.getResultList();

		if (result == null || result.size() == 0) {
			logger.log(Level.FINE, logStr + "Search stock Query=" + jpql);
		}

		return result;
	}

	private BigDecimal readUnitLoadWeight(UnitLoad unitLoad) {
		BigDecimal weight = unitLoad.getWeight();

		List<UnitLoad> childUnitLoads = unitLoadService.readChilds(unitLoad);
		for (UnitLoad childUnitLoad : childUnitLoads) {
			BigDecimal childWeight = readUnitLoadWeight(childUnitLoad);
			if (weight == null) {
				weight = childWeight;
			} else if (childWeight != null) {
				weight = weight.add(childWeight);
			}
		}
		return weight;
	}

	/**
	 * Try to find the storageStrategy of the itemDatas. If there are different
	 * strategies, null is returned.
	 */
	private StorageStrategy readUnitLoadStrategy(List<StockUnit> stocksOnUnitLoad) {
		StorageStrategy strategy = null;

		for (StockUnit stock : stocksOnUnitLoad) {
			StorageStrategy itemDataStrategy = stock.getItemData().getDefaultStorageStrategy();
			if (itemDataStrategy == null) {
				continue;
			}
			if (strategy == null) {
				strategy = itemDataStrategy;
			}
			if (!Objects.equals(strategy, itemDataStrategy)) {
				return null;
			}
		}

		return strategy;
	}

	private boolean existsOtherItemData(StorageLocation location, ItemData itemData) {
		String jpql = " SELECT stock.id FROM ";
		jpql += StockUnit.class.getName() + " stock ";
		jpql += " WHERE stock.unitLoad.storageLocation=:location and stock.itemData != :itemData ";

		Query query = manager.createQuery(jpql);
		query.setParameter("location", location);
		query.setParameter("itemData", itemData);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
		} catch (NoResultException x) {
			return false;
		}
		return true;
	}

	/**
	 * Try to find the zone of the itemDatas. If there are different zones, null is
	 * returned.
	 */
	private Zone readUnitLoadZone(List<StockUnit> stocksOnUnitLoad) {
		Zone zone = null;

		for (StockUnit stock : stocksOnUnitLoad) {
			Zone itemDataZone = stock.getItemData().getZone();
			if (itemDataZone == null) {
				// ignore itemDatas without zone
				continue;
			}
			if (zone == null) {
				zone = itemDataZone;
			}
			if (!Objects.equals(zone, itemDataZone)) {
				return null;
			}
		}

		return zone;
	}

	/**
	 * Read all StorageAreas which have older stock than the given strategyDate of
	 * the given itemData.
	 * <p>
	 * The StockUnit or UnitLoad may be locked.
	 */
	private List<StorageArea> readOlderStockAreas(Collection<ItemData> itemData, Date strategyDate,
			List<StorageArea> candidates) {
		String jpql = " SELECT distinct storageArea FROM ";
		jpql += StockUnit.class.getName() + " stock,";
		jpql += LocationCluster.class.getName() + " cluster,";
		jpql += StorageArea.class.getName() + " storageArea";
		jpql += " WHERE cluster = stock.unitLoad.storageLocation.locationCluster ";
		jpql += " and cluster in elements(storageArea.locationClusters) ";
		jpql += " and stock.itemData in (:itemData) ";
		jpql += " and stock.strategyDate < :strategyDate";
		jpql += " and storageArea in (:candidates)";
		jpql += " and stock.unitLoad.storageLocation.lock=0";
		jpql += " and stock.state>=:incomming and stock.state<:picked";
		jpql += " and stock.unitLoad.state>=:incomming and stock.unitLoad.state<:picked";

		TypedQuery<StorageArea> query = manager.createQuery(jpql, StorageArea.class);
		query.setParameter("itemData", itemData);
		query.setParameter("strategyDate", strategyDate);
		query.setParameter("candidates", candidates);
		query.setParameter("incomming", StockState.INCOMING);
		query.setParameter("picked", StockState.PICKED);

		List<StorageArea> olderStockAreas = query.getResultList();

		ArrayList<StorageArea> invalidAreas = new ArrayList<>();
		boolean foundOlder = false;
		for (int i = candidates.size(); i-- > 0;) {
			StorageArea candidate = candidates.get(i);
			if (foundOlder) {
				invalidAreas.add(candidate);
			} else if (olderStockAreas.contains(candidate)) {
				foundOlder = true;
			}
		}

		return invalidAreas;
	}

	/**
	 * Read all StorageAreas which are full in meaning of ItemDataArea. Only
	 * consider stocks with an older or same strategyDate as the given one.
	 * <p>
	 * The StockUnit or UnitLoad may be locked.
	 */
	private List<StorageArea> readFullAreas(Collection<ItemData> itemData, Date strategyDate) {
		String jpql = " SELECT storageArea FROM ";
		jpql += StorageArea.class.getSimpleName() + " storageArea,";
		jpql += ItemDataArea.class.getName() + " itemDataStorage";
		jpql += " WHERE itemDataStorage.itemData in (:itemData)";
		jpql += " and itemDataStorage.storageArea = storageArea";
		jpql += " and (";
		jpql += " (itemDataStorage.plannedStocks=0";
		jpql += "  or itemDataStorage.plannedStocks <= ";
		jpql += "  (select count(distinct stock.unitLoad) from " + StockUnit.class.getSimpleName() + " stock";
		jpql += "  where stock.itemData=itemDataStorage.itemData";
		jpql += "  and (";
		jpql += " stock.unitLoad.storageLocation.locationCluster in elements(storageArea.locationClusters)";
		jpql += " or exists(select 1 from " + TransportOrder.class.getName() + " transportOrder";
		jpql += "   where transportOrder.unitLoad=stock.unitLoad";
		jpql += "   and transportOrder.state<:finished";
		jpql += "   and transportOrder.destinationLocation.locationCluster in elements(storageArea.locationClusters)";
		jpql += "  )";
		jpql += "  and stock.strategyDate<=:strategyDate";
		jpql += "  and stock.unitLoad.storageLocation.lock=0";
		jpql += "  and stock.state>=:incomming and stock.state<:picked";
		jpql += "  and stock.unitLoad.state>=:incomming and stock.unitLoad.state<:picked)";
		jpql += "  ))";
		jpql += " or (itemDataStorage.plannedAmount=0";
		jpql += "  or itemDataStorage.plannedAmount <= ";
		jpql += "  (select sum(stock.amount) from " + StockUnit.class.getSimpleName() + " stock";
		jpql += "  where stock.itemData=itemDataStorage.itemData";
		jpql += "  and (";
		jpql += " stock.unitLoad.storageLocation.locationCluster in elements(storageArea.locationClusters)";
		jpql += " or exists(select 1 from " + TransportOrder.class.getName() + " transportOrder";
		jpql += "   where transportOrder.unitLoad=stock.unitLoad";
		jpql += "   and transportOrder.state<:finished";
		jpql += "   and transportOrder.destinationLocation.locationCluster in elements(storageArea.locationClusters)";
		jpql += "  )";
		jpql += "  and stock.strategyDate<=:strategyDate";
		jpql += "  and stock.unitLoad.storageLocation.lock=0";
		jpql += "  and stock.state>=:incomming and stock.state<:picked";
		jpql += "  and stock.unitLoad.state>=:incomming and stock.unitLoad.state<:picked)";
		jpql += " )))";

		TypedQuery<StorageArea> query = manager.createQuery(jpql, StorageArea.class);
		query.setParameter("itemData", itemData);
		query.setParameter("strategyDate", strategyDate);
		query.setParameter("incomming", StockState.INCOMING);
		query.setParameter("picked", StockState.PICKED);
		query.setParameter("finished", OrderState.FINISHED);
		return query.getResultList();
	}
}
