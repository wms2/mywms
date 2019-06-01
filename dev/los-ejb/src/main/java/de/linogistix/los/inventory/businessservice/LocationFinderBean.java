/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.inventory.service.LOSStorageStrategyService;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationAlreadyFullException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.Zone;

/**
 * Strategy Service,
 * Find a storage location for a unit load.
 * 
 * @author krane
 *
 */
@Stateless
public class LocationFinderBean implements LocationFinder {
	private static final Logger log = Logger.getLogger(LocationFinderBean.class);

	
	@EJB
	private ClientService clientService;
	@EJB
	private LOSStorageLocationTypeService storageLocationTypeService;
	@EJB
	private LocationReserver locationReserver;
	@EJB
	private LOSStorageRequestService storageRequestService;
	@EJB
	private LOSStorageStrategyService strategyService;
	@EJB
	private LOSUnitLoadService unitLoadService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	private boolean isPreferOwnClient() {
		return true;
	}
	
	public StorageLocation findLocation( UnitLoad unitLoad ) throws FacadeException {
		
		
		return findLocation(unitLoad, null, null);
	}
	
	public StorageLocation findPickingLocation( UnitLoad unitLoad, Zone zone, StorageStrategy strategy ) throws FacadeException {
		return findLocation( unitLoad, zone, strategy, true );
	}

	public StorageLocation findLocation( UnitLoad unitLoad, Zone zone, StorageStrategy strategy ) throws FacadeException {
		return findLocation( unitLoad, zone, strategy, false );
	}
	
	public StorageLocation findLocation( UnitLoad unitLoad, Zone zone, StorageStrategy strategy, boolean pickingOnly ) throws FacadeException {
		String logStr = "findLocation ";
		Date dateStart = new Date();

		// Initialize unit load data
		BigDecimal weightUnitLoad = readUnitLoadWeight(unitLoad);

		if( strategy == null ) {
			strategy = readUnitLoadStrategy(unitLoad);
		}
		if( strategy == null ) {
			strategy = strategyService.getDefault();
		}
		if( strategy == null ) {
			log.error(logStr+"No strategy defined. Cannot find location");
			throw new InventoryException(InventoryExceptionKey.STORAGE_STRATEGY_UNDEFINED, "");
		}
		
		
		// Initialize item data
		// This is only needed for strategies, which try not to mix different items
		ItemData itemData = null;
		if( ! strategy.isMixItem() ) {
			// Take the first item. If the unit load is mixed, it will not find a really good location with this strategy
			for( StockUnit su : unitLoad.getStockUnitList() ) {
				itemData = su.getItemData();
				break;
			}
		}
		
		List<Zone> zones = new ArrayList<>();
		if(zone!=null) {
			log.info(logStr+"Use only requested zone="+zone);
			zones.add(zone);
		}
		if (zones.isEmpty() && strategy.isUseItemZone()) {
			zone = readUnitLoadZone(unitLoad);
			while (true) {
				if (zone == null) {
					break;
				}
				if (zones.contains(zone)) {
					break;
				}
				zones.add(zone);
				zone = zone.getNextZone();
			}
		}
		if( zones.isEmpty() && strategy.getZone()!=null) {
			zones.add(strategy.getZone());
		}

		// Initialize clients
		Client clientSys = clientService.getSystemClient();
		Client clientStock = unitLoad.getClient();
		List<Client> clientListAll = new ArrayList<Client>();
		clientListAll.add(clientStock);
		List<Client> clientListPreferred = new ArrayList<Client>();
		if( !clientStock.equals(clientSys) ) {
			if( isPreferOwnClient() ) {
				if( existsClientLocations( clientStock ) ) {
					clientListPreferred.add( clientStock );
				}
			}
			clientListAll.add( clientSys );
		}

		

		StorageLocation location = null;
		

		if( clientListPreferred.size()>0 ) {
			location = searchLocationOfClient( clientListPreferred, unitLoad, strategy, itemData, zones, pickingOnly, weightUnitLoad );
			if( location != null ) {
				log.debug(logStr+"Found client location "+location.getName()+" in "+(new Date().getTime()-dateStart.getTime())+" ms");
				return location;
			}
		}

		location = searchLocationOfClient( clientListAll, unitLoad, strategy, itemData, zones, pickingOnly, weightUnitLoad );
		if( location != null ) {
			log.debug(logStr+"Found location "+location.getName()+" in "+(new Date().getTime()-dateStart.getTime())+" ms");
			return location;
		}


		log.debug(logStr+"found nothing in "+(new Date().getTime()-dateStart.getTime())+" ms");
		return null;
	}

	
	private StorageLocation searchLocationOfClient( Collection<Client> clients, UnitLoad unitLoad, StorageStrategy strategy, ItemData itemData, List<Zone> zones, boolean pickingOnly, BigDecimal weightUnitLoad ) throws FacadeException{
		StorageLocation location = null;
		
		location = searchLocation( clients, unitLoad, strategy, itemData, zones, pickingOnly, weightUnitLoad );
		if( location != null ) {
			return location;
		}
		
		return null;
	}
	

	
	private StorageLocation searchLocation( Collection<Client> clients, UnitLoad unitLoad, StorageStrategy strategy, ItemData itemData, List<Zone> zones, boolean pickingOnly, BigDecimal weight ) throws FacadeException{
		String logStr = "searchLocation ";
		int startSearchIndex = 0;

		log.info(logStr + "clients=" + clients + ", unitLoad=" + unitLoad + ", strategy=" + strategy + ", itemData="
				+ itemData + ", zones=" + zones + ", pickingOnly=" + pickingOnly + ", weight=" + weight);		
		
		List<Integer> locks = new ArrayList<Integer>();
		locks.add(LOSStorageLocationLockState.NOT_LOCKED.getLock());
		locks.add(LOSStorageLocationLockState.RETRIEVAL.getLock());
		
		// Do not read all. Usually one of the first location is suitable
		while (true) {

			List<Object[]> locationList = null;

			locationList = getLocationList(clients, locks, unitLoad, strategy, zones, pickingOnly, weight,
					startSearchIndex);

			for (Object[] o : locationList) {

				StorageLocation location = manager.find(StorageLocation.class, o[0]);
				if (location == null) {
					continue;
				}

				// Check storage requests for strategies which require unique item data on a
				// location
				if (!strategy.isMixItem() && itemData != null) {
					if (existsDiffItem(location, itemData)) {
						log.warn(logStr + "Not allowed to mix items");
						continue;
					}
				}

				// Check weight
				BigDecimal liftingCapacity = location.getType().getLiftingCapacity();
				if (liftingCapacity != null && liftingCapacity.compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal weightLoc = (weight == null ? BigDecimal.ZERO : weight);
					for (UnitLoad ulLoc : location.getUnitLoads()) {
						if (ulLoc.getWeight() != null) {
							weightLoc = weightLoc.add(ulLoc.getWeight());
						}
					}

					if (BigDecimal.ZERO.compareTo(location.getAllocation()) < 0) {
						List<LOSStorageRequest> reqList = storageRequestService.getActiveListByDestination(location);
						for (LOSStorageRequest req : reqList) {
							UnitLoad ulStorage = req.getUnitLoad();
							if (!unitLoad.equals(ulStorage)) {
								BigDecimal weightUnitLoad = readUnitLoadWeight(ulStorage);
								if (weightUnitLoad != null) {
									weightLoc = weightLoc.add(weightUnitLoad);
								}
							}
						}
					}
					if (weightLoc.compareTo(liftingCapacity) > 0) {
						log.debug(logStr + "Too much wieght for location. name=" + location.getName());
						continue;
					}
				}

				try {
					locationReserver.checkAllocateLocation(location, unitLoad, false);
				} catch (LOSLocationAlreadyFullException e) {
					log.debug(logStr + "Location not usable. LOSLocationAlreadyFullException=" + e.getMessage());
					continue;
				} catch (LOSLocationNotSuitableException e) {
					log.debug(logStr + "Location not usable. LOSLocationNotSuitableException=" + e.getMessage());
					continue;
				} catch (LOSLocationReservedException e) {
					log.debug(logStr + "Location not usable, LOSLocationReservedException=" + e.getMessage());
					continue;
				} catch (LOSLocationWrongClientException e) {
					log.debug(logStr + "Location not usable, LOSLocationWrongClientException=" + e.getMessage());
					continue;
				}

				return location;
			}

			if (locationList.size() < 10) {
				break;
			}
			startSearchIndex += 10;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object[]> getLocationList( Collection<Client> clients, Collection<Integer> locks, UnitLoad unitLoad, StorageStrategy strategy, List<Zone> zones, boolean pickingOnly, BigDecimal weight, int idxStart ) throws FacadeException{
		String logStr = "getLocationList ";
		String paramLog = "";

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT loc.id, loc.YPos, loc.XPos, loc.name  FROM ");
		sb.append(StorageLocation.class.getSimpleName()+" loc ");
		sb.append("left outer join loc.zone as zone, ");
		sb.append(LocationType.class.getSimpleName()+" locType, ");
		sb.append(Area.class.getSimpleName()+" area, ");
		sb.append(TypeCapacityConstraint.class.getSimpleName()+" constraint ");
		sb.append(" WHERE loc.locationType = locType and loc.area=area");
		sb.append(" AND constraint.unitLoadType=:unitLoadType and constraint.locationType=locType");
		sb.append(" AND locType!=:fixedType ");
		sb.append(" AND loc.allocation<100 ");
		sb.append(" AND loc.lock in (:lockList) ");
		sb.append(" AND loc.client in (:clientList) ");

		if( pickingOnly ) {
			sb.append(" and area.usages like '%" + AreaUsages.PICKING + "%'");
		}
		else {
			if( strategy.getUsePicking() == StorageStrategy.TRUE ) {
				sb.append(" and area.usages like '%" + AreaUsages.PICKING + "%'");
			}
			else if( strategy.getUsePicking() == StorageStrategy.FALSE ) {
				sb.append(" and not area.usages like '%" + AreaUsages.PICKING + "%'");
			}
			if( strategy.getUseStorage() != StorageStrategy.TRUE ) {
				sb.append(" and area.usages like '%" + AreaUsages.STORAGE + "%'");
			}
			else if( strategy.getUseStorage() != StorageStrategy.FALSE ) {
				sb.append(" and not area.usages like '%" + AreaUsages.STORAGE + "%'");
			}
		}
		if( weight != null ) {
			sb.append(" AND (locType.liftingCapacity is null or locType.liftingCapacity >= :weight) ");
		}
		
		// Search only the given zones
		if (zones != null && zones.size() > 0) {
			sb.append(" AND loc.zone in(:zones) ");
		}

		if( !strategy.isMixClient() ) {
			sb.append(" AND NOT EXISTS (select 1 from "+UnitLoad.class.getSimpleName()+" otherUnitLoad ");
			sb.append("   WHERE otherUnitLoad.client!=:stockClient and otherUnitLoad.storageLocation=loc ");
			sb.append(" ) ");
			sb.append(" AND NOT EXISTS (select 1 from "+LOSStorageRequest.class.getSimpleName()+" otherStorageRequest ");
			sb.append("   WHERE otherStorageRequest.client!=:stockClient and otherStorageRequest.destination=loc and otherStorageRequest.requestState in (:srStateRaw, :srStateProcessing) ");
			sb.append(" ) ");
		}
		
		sb.append(" AND (");
		sb.append("    (loc.currentTypeCapacityConstraint is null) ");
		sb.append(" or exists( select 1 from "+TypeCapacityConstraint.class.getSimpleName()+" cc1");
		sb.append("            where cc1=loc.currentTypeCapacityConstraint and cc1.allocationType=:typePercentage ) "); 
		sb.append(" or exists( select 1 from "+TypeCapacityConstraint.class.getSimpleName()+" cc1");
		sb.append("            where cc1=loc.currentTypeCapacityConstraint and cc1.allocationType=:typeUnitLoadType and cc1.unitLoadType=:unitLoadType ) "); 
		sb.append(" ) ");
		
		sb.append(" AND ( ");
		sb.append("     (constraint.allocationType=:typeUnitLoadType and constraint.allocation>0 and loc.allocation<=(100-constraint.allocation) ) ");
		sb.append("  or (constraint.allocationType=:typePercentage and constraint.allocation>0 and loc.allocation<=(100-constraint.allocation) ) ");
		sb.append(" ) ");
		
		sb.append(" ORDER BY ");

		if (zones != null && zones.size() > 0) {
			sb.append("case");
			int i = 0;
			for (Zone zone : zones) {
				if (zone == null) {
					sb.append(" when zone is null then " + i);
				} else {
					sb.append(" when zone.id=" + zone.getId() + " then " + i);
				}
				i++;
			}
			sb.append(" else " + i + " end, ");
		}

		if( strategy.getOrderByMode() == StorageStrategy.ORDER_BY_XPOS ) {
			sb.append("loc.XPos, loc.YPos, loc.name, loc.id ");
		}
		else {
			sb.append("loc.YPos, loc.XPos, loc.name, loc.id ");
		}

		LocationType fixedType = storageLocationTypeService.getAttachedUnitLoadType();

		log.debug(logStr+"Search location Query="+sb.toString());
		Query query = manager.createQuery(sb.toString());
		
		query.setParameter("unitLoadType", unitLoad.getUnitLoadType());
		paramLog += ", unitLoadType="+unitLoad.getUnitLoadType();
		query.setParameter("fixedType", fixedType);
		paramLog += ", fixedType="+fixedType;
		query.setParameter("clientList", clients);
		paramLog += ", clientList="+clients;
		query.setParameter("lockList", locks);
		paramLog += ", lockList="+locks;

		if( !strategy.isMixClient() ) {
			query.setParameter("stockClient", unitLoad.getClient());
			paramLog += ", stockClient="+unitLoad.getClient();
			query.setParameter("srStateRaw", LOSStorageRequestState.RAW);
			query.setParameter("srStateProcessing", LOSStorageRequestState.PROCESSING);
		}
		
		if( weight != null ) {
			query.setParameter("weight", weight );
			paramLog += ", weight="+weight;
		}

		if (zones != null && zones.size() > 0) {
			query.setParameter("zones", zones);
			paramLog += ", zones=" + zones;
		}

		query.setParameter("typeUnitLoadType", TypeCapacityConstraint.ALLOCATE_UNIT_LOAD_TYPE);
		query.setParameter("typePercentage", TypeCapacityConstraint.ALLOCATE_PERCENTAGE);
		
		query.setFirstResult(idxStart);
		query.setMaxResults(10);
		
		List<Object[]> result = query.getResultList();
		
		if( result==null || result.size()==0 ) {
			log.debug(logStr+"Search location Query="+sb.toString());
			log.debug(logStr+"Search location Paramter="+paramLog);
		}
		
		return result;
	}
	

	
	@SuppressWarnings("rawtypes")
	private boolean existsClientLocations( Client client ) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT name FROM ");
		sb.append(StorageLocation.class.getSimpleName()+" loc ");
		sb.append(" WHERE client = :client ");
		Query query = manager.createQuery(sb.toString());
		query.setParameter("client", client);
		query.setMaxResults(1);
		List res = query.getResultList();
		return res.size()>0;
		
	}
	
	private BigDecimal readUnitLoadWeight(UnitLoad unitLoad) {
		BigDecimal weight = unitLoad.getWeight();
		List<UnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( UnitLoad child : childs ) {
			BigDecimal weightChild = readUnitLoadWeight(child);
			if( weightChild != null ) {
				weight = weight == null ? weightChild : weight.add( weightChild );
			}
		}
		return weight;
	}

	private StorageStrategy readUnitLoadStrategy(UnitLoad unitLoad) {
		StorageStrategy strategy = null;

		for (StockUnit su : unitLoad.getStockUnitList()) {
			StorageStrategy itemStrategy = su.getItemData().getDefaultStorageStrategy();
			if (itemStrategy != null) {
				return itemStrategy;
			}
		}

		for (UnitLoad child : unitLoadService.getChilds(unitLoad)) {
			strategy = readUnitLoadStrategy(child);
			if (strategy != null) {
				return strategy;
			}
		}

		return null;
	}

	private Zone readUnitLoadZone(UnitLoad unitLoad) {
		Zone zone = null;
		
		for( StockUnit su : unitLoad.getStockUnitList() ) {
			Zone itemZone = su.getItemData().getZone();
			if( itemZone != null ) {
				return itemZone;
			}
		}
		
		List<UnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( UnitLoad child : childs ) {
			zone = readUnitLoadZone(child);
			if( zone != null ) {
				return zone;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean existsDiffItem( StorageLocation location, ItemData item ) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT su.id FROM ");
		sb.append(StockUnit.class.getSimpleName()+" su, "+UnitLoad.class.getSimpleName()+" ul ");
		sb.append(" WHERE ul.storageLocation=:location and su.unitLoad=ul and su.itemData != :item ");
		Query query = manager.createQuery(sb.toString());
		query.setParameter("location", location);
		query.setParameter("item", item);
		query.setMaxResults(1);
		List res = query.getResultList();
		return res.size()>0;
	}
	}
