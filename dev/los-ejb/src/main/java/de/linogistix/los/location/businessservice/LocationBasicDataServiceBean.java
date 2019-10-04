/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSWorkingAreaService;
import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.client.ClientBusiness;
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
import de.wms2.mywms.property.SystemProperty;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.Zone;
import de.wms2.mywms.strategy.ZoneEntityService;
import de.wms2.mywms.util.Wms2Properties;


/**
 * @author krane
 *
 */
@Stateless
public class LocationBasicDataServiceBean implements LocationBasicDataService {

	private static final Logger log = Logger.getLogger(LocationBasicDataServiceBean.class);

	@Inject
	private ClientBusiness clientService;
	@EJB
	private EntityGenerator entityGenerator;
	@Inject
	private ZoneEntityService zoneService;
	@EJB
	private AreaEntityService areaService;
	@EJB
	private LOSStorageLocationTypeService losLocationTypeService;
	@EJB
	private QueryTypeCapacityConstraintService capaService;
	@EJB
	private LOSWorkingAreaService waService;
	@EJB
	private LOSSystemPropertyService propertyService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	@Inject
	private LocationTypeEntityService locationTypeService;
	@Inject
	private LocationClusterEntityService locationClusterService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private OrderStrategyEntityService orderStrategyService;

	public void createBasicData(Locale locale) throws FacadeException {

		log.info("Create Location Basic Data...");
		
		Client sys = clientService.getSystemClient();

		
		log.info("Create Properties...");

		
		log.info("Create Zones...");
		createZone("A");
		createZone("B");
		createZone("C");

		log.info("Create Areas...");

		String name = resolve("BasicDataAreaDefault", locale);
		Area storeArea = areaService.getDefault();
		storeArea.setName(name);
		storeArea.setUsages(null);
		storeArea.setUseFor(AreaUsages.STORAGE, true);
		storeArea.setUseFor(AreaUsages.PICKING, true);
		storeArea.setUseFor(AreaUsages.REPLENISH, true);
		SystemProperty prop = propertyService.getByKey(Wms2Properties.KEY_AREA_DEFAULT);
		prop.setPropertyValue(name);

		Area areaIn = createArea(sys, resolve("BasicDataAreaGoodsIn", locale));
		areaIn.setUseFor(AreaUsages.GOODS_IN, true);

		Area areaOut = createArea(sys, resolve("BasicDataAreaGoodsOut", locale));
		areaOut.setUseFor(AreaUsages.GOODS_OUT, true);
		
		Area areaClearing = createArea(sys, resolve("BasicDataAreaClearing", locale));

		
		log.info("Create Cluster...");

		LocationCluster clusterDefault = locationClusterService.getDefault();

		
		log.info("Create working area...");
		createWorkingArea( resolve("BasicDataWorkingAreaDefault", locale), clusterDefault );

		log.info("Create LocationTypes...");
		
		LocationType pType = locationTypeService.getDefault();
		
		LocationType sysType = locationTypeService.getSystem();
		
		
		log.info("Create UnitLoadTypes...");

		UnitLoadType defUlType = unitLoadTypeService.getDefault();

		
		
		
		log.info("Create CapacityConstraints...");

		createCapa( pType, defUlType );

		
		
		log.info("Create StorageLocations...");
		
		List<StorageLocation> list = locationService.getForGoodsIn(null);
		if( list == null || list.size() == 0 ) {
			createStorageLocation(sys, resolve("BasicDataLocationGoodsIn", locale), areaIn, sysType, clusterDefault);
		}
		list = locationService.getForGoodsOut(null);
		if (list == null || list.size() == 0) {
			StorageLocation goodsOutLocation = createStorageLocation(sys, resolve("BasicDataLocationGoodsOut", locale),
					areaOut, sysType, clusterDefault);
			OrderStrategy defaultOrderStrategy = orderStrategyService.getDefault(sys);
			if (defaultOrderStrategy.getDefaultDestination() == null) {
				defaultOrderStrategy.setDefaultDestination(goodsOutLocation);
			}
			SystemProperty shippingLocationProperty = propertyService.getByKey(Wms2Properties.KEY_SHIPPING_LOCATION);
			if (shippingLocationProperty != null && StringUtils.isBlank(shippingLocationProperty.getPropertyValue())) {
				shippingLocationProperty.setPropertyValue(goodsOutLocation.getName());
			}
		}
		StorageLocation loc = locationService.getClearing();
		loc.setArea(areaClearing);
		
		loc = locationService.getTrash();
		loc.setArea(areaClearing);
		

		log.info("Create Location Basic Data. done.");
	}
	
	private Zone createZone(String name) {
		Zone zone = zoneService.read(name);
		if( zone == null ) {
			zone = entityGenerator.generateEntity( Zone.class );
			zone.setName(name);
			manager.persist(zone);
		}
		return zone;
	}
	

	private Area createArea(Client client, String name) {
		Area area = null; 
		try {
			area = areaService.readByName(name);
		} catch (Exception e) {}
		if( area == null ) {
			area = entityGenerator.generateEntity( Area.class );
			area.setName(name);
			manager.persist(area);
		}
		return area;
	}
	
	private TypeCapacityConstraint createCapa(LocationType slType, UnitLoadType ulType) {
		TypeCapacityConstraint constraint = null;
		constraint = capaService.getByTypes(slType, ulType);
		if( constraint == null ) {
			constraint = entityGenerator.generateEntity( TypeCapacityConstraint.class );
			constraint.setStorageLocationType(slType);
			constraint.setUnitLoadType(ulType);
			manager.persist(constraint);
		}

		return constraint;
	}
	
	private StorageLocation createStorageLocation(Client client, String name, Area area, LocationType slType, LocationCluster cluster) {
		StorageLocation loc = null;
		loc = locationService.readByName(name);
		if( loc == null ) {
			loc = entityGenerator.generateEntity( StorageLocation.class );
			loc.setClient(client);
			loc.setName(name);
			loc.setType(slType);
			loc.setArea(area);
			loc.setCluster(cluster);
			manager.persist(loc);
		}
		
		return loc;
	}
	
	private LOSWorkingArea createWorkingArea( String name, LocationCluster cluster ) {
		LOSWorkingArea wa = null;
		try {
			wa = waService.getByName(name);
		} catch (Exception e) {	}
		if( wa == null ) {
			wa = entityGenerator.generateEntity( LOSWorkingArea.class );
			wa.setName(name);
			manager.persist(wa);
			LOSWorkingAreaPosition wap = null;
			wap = entityGenerator.generateEntity( LOSWorkingAreaPosition.class );
			wap.setCluster(cluster);
			wap.setWorkingArea(wa);
			manager.persist(wap);
		}
		return wa;
	}
	
	private final String resolve( String key, Locale locale ) {
        if (key == null) {
            return "";
        }
        
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("de.linogistix.los.location.res.Bundle", locale, BundleResolver.class.getClassLoader());
            String s = bundle.getString(key);
            return s;
        }
        catch (MissingResourceException ex) {
        	log.error("Exception: "+ex.getMessage());
            return key;
        }
        catch (IllegalFormatException ife){
        	log.error("Exception: "+ife.getMessage());
        	return key;
        }
    }
}
