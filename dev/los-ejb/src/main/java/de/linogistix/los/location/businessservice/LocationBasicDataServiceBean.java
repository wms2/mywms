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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.entityservice.LOSAreaServiceBean;
import de.linogistix.los.location.entityservice.LOSLocationClusterService;
import de.linogistix.los.location.entityservice.LOSLocationClusterServiceBean;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSWorkingAreaService;
import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.location.service.ZoneService;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.property.SystemProperty;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.Zone;


/**
 * @author krane
 *
 */
@Stateless
public class LocationBasicDataServiceBean implements LocationBasicDataService {

	private static final Logger log = Logger.getLogger(LocationBasicDataServiceBean.class);

	@EJB
	private ClientService clientService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private ZoneService zoneService;
	@EJB
	private LOSAreaService areaService;
	@EJB
	private LOSStorageLocationTypeService locationTypeService;
	@EJB
	private QueryUnitLoadTypeService ultService;
	@EJB
	private QueryTypeCapacityConstraintService capaService;
	@EJB
	private QueryStorageLocationService locService;
	@EJB
	private LOSStorageLocationService slService;
	@EJB
	private LOSLocationClusterService lcService;
	@EJB
	private LOSWorkingAreaService waService;
	@EJB
	private LOSSystemPropertyService propertyService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	
	public void createBasicData(Locale locale) throws FacadeException {

		log.info("Create Location Basic Data...");
		
		Client sys = clientService.getSystemClient();

		
		log.info("Create Properties...");

		
		log.info("Create Zones...");
		createZone(sys, "A");
		createZone(sys, "B");
		createZone(sys, "C");

		log.info("Create Areas...");

		String name = resolve("BasicDataAreaDefault", locale);
		propertyService.createSystemProperty(sys, null, LOSAreaServiceBean.PROPERTY_KEY_AREA_DEFAULT, name, LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescPROPERTY_KEY_AREA_DEFAULT", locale));
		Area storeArea = areaService.getDefault();
		storeArea.setName(name);
		storeArea.setUseFor(AreaUsages.STORAGE, true);
		storeArea.setUseFor(AreaUsages.PICKING, true);
		storeArea.setUseFor(AreaUsages.REPLENISH, true);
		SystemProperty prop = propertyService.getByKey(LOSAreaServiceBean.PROPERTY_KEY_AREA_DEFAULT);
		prop.setPropertyValue(name);

		Area areaIn = createArea(sys, resolve("BasicDataAreaGoodsIn", locale));
		areaIn.setUseFor(AreaUsages.GOODS_IN, true);

		Area areaOut = createArea(sys, resolve("BasicDataAreaGoodsOut", locale));
		areaOut.setUseFor(AreaUsages.GOODS_OUT, true);
		
		Area areaClearing = createArea(sys, resolve("BasicDataAreaClearing", locale));

		
		log.info("Create Cluster...");

		name = resolve("BasicDataClusterDefault", locale);
		propertyService.createSystemProperty(sys, null, LOSLocationClusterServiceBean.PROPERTY_KEY_CLUSTER_DEFAULT, name, LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescPROPERTY_KEY_CLUSTER_DEFAULT", locale));
		LocationCluster clusterDefault = lcService.getDefault();
		clusterDefault.setName(name);
		prop = propertyService.getByKey(LOSLocationClusterServiceBean.PROPERTY_KEY_CLUSTER_DEFAULT);
		prop.setPropertyValue(name);

		
		log.info("Create working area...");
		createWorkingArea( resolve("BasicDataWorkingAreaDefault", locale), clusterDefault );

		log.info("Create LocationTypes...");
		
		LocationType pType = locationTypeService.getDefaultStorageLocationType();
		
		LocationType sysType = locationTypeService.getNoRestrictionType();
		
		locationTypeService.getAttachedUnitLoadType();
		
		
		
		
		log.info("Create UnitLoadTypes...");

		UnitLoadType defUlType = ultService.getDefaultUnitLoadType();

		
		
		
		log.info("Create CapacityConstraints...");

		createCapa( pType, defUlType );

		
		
		log.info("Create StorageLocations...");
		
		List<StorageLocation> list = locService.getListForGoodsIn();
		if( list == null || list.size() == 0 ) {
			createStorageLocation(sys, resolve("BasicDataLocationGoodsIn", locale), areaIn, sysType, clusterDefault);
		}
		list = locService.getListForGoodsOut();
		if( list == null || list.size() == 0 ) {
			createStorageLocation(sys, resolve("BasicDataLocationGoodsOut", locale), areaOut, sysType, clusterDefault);
		}
		StorageLocation loc = slService.getClearing();
		loc.setArea(areaClearing);
		
		loc = slService.getNirwana();
		loc.setArea(areaClearing);
		

		log.info("Create Location Basic Data. done.");
	}
	
	private Zone createZone(Client client, String name) {
		Zone zone = null;
		try {
			zone = zoneService.getByName(client, name);
		} catch (Exception e) {	}
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
			area = areaService.getByName(client, name);
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
		loc = slService.getByName(name);
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
