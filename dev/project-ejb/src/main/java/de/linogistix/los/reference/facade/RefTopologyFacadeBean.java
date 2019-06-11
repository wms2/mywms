/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.facade;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemUnitType;

import de.linogistix.los.common.businessservice.CommonBasicDataService;
import de.linogistix.los.common.businessservice.LOSJasperReportGenerator;
import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.businessservice.InventoryBasicDataService;
import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.inventory.service.ItemUnitService;
import de.linogistix.los.location.businessservice.LocationBasicDataService;
import de.linogistix.los.location.entityservice.LOSAreaService;
import de.linogistix.los.location.entityservice.LOSLocationClusterService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.reference.model.ProjectPropertyKey;
import de.linogistix.los.stocktaking.component.LOSStockTakingProcessComp;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadPackageType;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.strategy.Zone;
import de.wms2.mywms.strategy.ZoneEntityService;
import de.wms2.mywms.util.Wms2Properties;

@Stateless
public class RefTopologyFacadeBean implements RefTopologyFacade {

	@EJB
	private ItemUnitService unitService;
	
	@Inject
	private ClientBusiness clientService;

	@EJB
	private ItemUnitService itemUnitService;

	@Inject
	private ZoneEntityService zoneService;
	
	@EJB
	private LOSAreaService areaService;

	@EJB
	private LOSStorageLocationTypeService locationTypeService;

	@EJB
	private QueryTypeCapacityConstraintService capaService;
	
	@EJB
	private LOSStorageLocationService slService;
	
	@EJB
	private ItemDataService itemDataService;
	
	@EJB
	private QueryFixedAssignmentService fixedService;
	
	@EJB
	private LOSLocationClusterService lcService;
	
	@EJB
	private QueryUnitLoadTypeService ultService;
	
	@EJB
	private LOSUnitLoadService ulService;
	
	@EJB
	private QueryUnitLoadService ulQueryService;
	
	@EJB
	private ContextService contextService;

	@EJB
	private ManageInventoryFacade inventoryFacade;

	@EJB
	private LOSOrderFacade orderFacade;
	
	@EJB
	private ItemDataNumberService eanService;

	@EJB
	private LOSStockTakingProcessComp stService;
	@EJB
	private EntityGenerator entityGenerator;

	
	@EJB
	private CommonBasicDataService commonBasicDataService;
	@EJB
	private LocationBasicDataService locationBasicDataService;
	@EJB
	private InventoryBasicDataService inventoryBasicDataService;
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private LOSJasperReportGenerator reportService;
	@EJB
	private QueryStorageLocationService locService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	private static final Logger log = Logger.getLogger(RefTopologyFacadeBean.class);
	
	public void createBasicTopology() throws FacadeException {
		log.info("Create Basic Data.");

		commonBasicDataService.createBasicData(getLocale());
		locationBasicDataService.createBasicData(getLocale());
		inventoryBasicDataService.createBasicData(getLocale());
		
		log.info("Create Properties...");
		Client sys = clientService.getSystemClient();
		propertyService.createSystemProperty(sys, null, ProjectPropertyKey.CREATE_DEMO_TOPOLOGY, "true", Wms2Properties.GROUP_SETUP, resolve("PropertyDescCREATE_DEMO_TOPOLOGY"), true);
	
		log.info("Create Basic Data. done.");
	}
	
	
	public boolean checkDemoData() throws FacadeException {
		String prop = propertyService.getStringDefault(ProjectPropertyKey.CREATE_DEMO_TOPOLOGY, null);
		if( prop == null ) {
			createBasicTopology();
			return false;
		}
		
		return true;
	}
	
	
	public void createDemoTopology() throws FacadeException {
		createBasicTopology();
		
		Client sys = clientService.getSystemClient();

		
		log.info("Create ItemUnits...");
		ItemUnit pce = unitService.getDefault();
		
		ItemUnit gramm = createItemUnit(resolve("UnitGrammName"), ItemUnitType.WEIGHT, 1, null);
		
		createItemUnit( resolve("UnitKiloGrammName"), ItemUnitType.WEIGHT, 1000, gramm);
		
		ItemUnit pack = createItemUnit( resolve("UnitPackName"), ItemUnitType.PIECE, 1, null);
		

		
		log.info("Create Zones...");
		
		Zone zoneSysA = createZone("A");

		
		log.info("Create LocationTypes...");
		
		LocationType pType = locationTypeService.getDefaultStorageLocationType();
		pType.setName( resolve("LocationTypePallet") );
		
		LocationType fixType = locationTypeService.getAttachedUnitLoadType();
		fixType.setName( resolve("LocationTypePicking") );

		LocationType fType = createLocationType(sys, resolve("LocationTypeShelf") );
		

		log.info("Create UnitLoadTypes...");

		UnitLoadType euro140 = ultService.getDefaultUnitLoadType();
		euro140.setName( resolve("UnitLoadTypeEuro") );
		euro140.setDepth(BigDecimal.valueOf(1.20));
		euro140.setWidth(BigDecimal.valueOf(0.80));
		euro140.setHeight(BigDecimal.valueOf(1.40));
		euro140.setWeight(BigDecimal.valueOf(25));
		euro140.setLiftingCapacity(BigDecimal.valueOf(500));

		UnitLoadType kltType60 = createUnitLoadType(sys, resolve("UnitLoadTypeBox6040"), 0.40, 0.60, 0.25, 20, 0.7);
		
		UnitLoadType kltType30 = createUnitLoadType(sys, resolve("UnitLoadTypeBox3040"), 0.40, 0.30, 0.25, 20, 0.5);
		
		UnitLoadType picking = ultService.getPickLocationUnitLoadType();
		picking.setName( resolve("UnitLoadTypePicking") );
		
		
		log.info("Create CapacityConstraints...");

		createCapa( pType, euro140, BigDecimal.valueOf(100) );
		createCapa( fType, kltType60, BigDecimal.valueOf(100));
		createCapa( fType, kltType30, BigDecimal.valueOf(50));
		createCapa( fixType, picking, BigDecimal.valueOf(100));
		
	

		log.info("Create StorageLocations...");
		
		LocationCluster cluster = lcService.getDefault();
		

		List<Area> areaList = null;
		Area rackArea = null;
		areaList = areaService.getForStorage();
		if( areaList.size()>0 ) {
			rackArea = areaList.get(0);
		}
		Area pickArea = null;
		areaList = areaService.getForPicking();
		if( areaList.size()>0 ) {
			pickArea = areaList.get(0);
		}

		createRack(sys, resolve("RackNamePallet")+" A1", "A1", 5, 3, 2, rackArea, pType, cluster, zoneSysA);
		manager.flush();
		createRack(sys, resolve("RackNameShelf")+" A2", "A2", 3, 6, 2, rackArea, fType, cluster, zoneSysA);
		manager.flush();

		
		
		log.info("Create Picklocations...");
		
		createRack(sys, resolve("RackNamePickFixed"), "P1", 2, 3, 2, pickArea, fixType, cluster, zoneSysA);
		manager.flush();

		

		log.info("Create ItemData...");
		
		ItemData printer1 = createItemData(sys, resolve("CustomerItemData1Number"), resolve("CustomerItemData1Name"), resolve("CustomerItemData1Desc"), pce, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		ItemData printer2 = createItemData(sys, resolve("CustomerItemData2Number"), resolve("CustomerItemData2Name"), resolve("CustomerItemData2Desc"), pce, false, SerialNoRecordType.GOODS_OUT_RECORD, zoneSysA, euro140);
		ItemData paper1 = createItemData(sys, resolve("CustomerItemData3Number"), resolve("CustomerItemData3Name"), resolve("CustomerItemData3Desc"), pack, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		createEAN(paper1, "12345678");
		ItemData paper2 = createItemData(sys, resolve("CustomerItemData4Number"), resolve("CustomerItemData4Name"), resolve("CustomerItemData4Desc"), pack, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		ItemData toner1 = createItemData(sys, resolve("CustomerItemData5Number"), resolve("CustomerItemData5Name"), resolve("CustomerItemData5Desc"), pce, true, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		createEAN(toner1, "12312312");
		ItemData toner2 = createItemData(sys, resolve("CustomerItemData6Number"), resolve("CustomerItemData6Name"), resolve("CustomerItemData6Desc"), pce, false, SerialNoRecordType.NO_RECORD, zoneSysA, euro140);
		ItemData screw1 = createItemData(sys, resolve("CustomerItemData7Number"), resolve("CustomerItemData7Name"), resolve("CustomerItemData7Desc"), gramm, false, SerialNoRecordType.NO_RECORD, zoneSysA, kltType60);
		createItemData(sys, resolve("CustomerItemData8Number"), resolve("CustomerItemData8Name"), resolve("CustomerItemData8Desc"), gramm, false, SerialNoRecordType.NO_RECORD, zoneSysA, kltType60);
		
		
		
		log.info("Create FixedLocations...");
		
		createFixLocation( sys, "P1-011-1", paper1, fixType);
		createFixLocation( sys, "P1-012-1", paper2, fixType);
		

		
		
		log.info("Create Stock...");
		
		createStock(sys, "P1-011-1", paper1, new BigDecimal(200),"P1-011-1", null);
		
		createStock(sys, "A1-011-1", paper1, new BigDecimal(200),"000001", null);
		createStock(sys, "A1-012-1", paper1, new BigDecimal(200),"000002", null);
		createStock(sys, "A1-021-1", paper2, new BigDecimal(200),"000003", null);
		createStock(sys, "A1-022-1", paper2, new BigDecimal(200),"000004", null);

		createStock(sys, "A1-011-2", printer1, new BigDecimal(8), "000005", null);
		createStock(sys, "A1-012-2", printer1, new BigDecimal(8), "000006", null);
		createStock(sys, "A1-013-2", printer2, new BigDecimal(8), "000007", null);
		createStock(sys, "A1-021-2", toner1, new BigDecimal(144), "000008", "1582");
		createStock(sys, "A1-022-2", toner1, new BigDecimal(144),"000009", "1582");
		createStock(sys, "A1-023-2", toner1, new BigDecimal(144),"000010", "1582");
		createStock(sys, "A1-031-2", toner2, new BigDecimal(72),"000011", null);
		
		createStock(sys, "A2-011-1", screw1, new BigDecimal(15000),"000012", null);

		log.info("Create Goods In...");
		createAdvice( sys, printer1, new BigDecimal(16) );
		createAdvice( sys, paper1, new BigDecimal(2000) );
		createAdvice( sys, toner1, new BigDecimal(572) );
			
		log.info("Create Goods Out...");
		createOrder( sys, printer1, new BigDecimal(1), null, null );
		createOrder( sys, printer1, new BigDecimal(1), paper1, new BigDecimal(1) );
		createOrder( sys, printer2, new BigDecimal(1), paper1, new BigDecimal(1) );
		createOrder( sys, paper1, new BigDecimal(10), toner1, new BigDecimal(1) );
		createOrder( sys, paper1, new BigDecimal(10), toner1, new BigDecimal(1) );
		
		log.info("Create Stocktaking...");
		createStocktaking( sys, "A1-01%1");
		
		
		log.info("Create Reports...");
		reportService.getJasportReport(sys, InventoryBundleResolver.class, "OrderReceipt");
		reportService.getJasportReport(sys, InventoryBundleResolver.class, "StockUnitLabel");
		reportService.getJasportReport(sys, InventoryBundleResolver.class, "UnitLoadLabel");
		
		log.info("Done.");

	}

	private Locale getLocale() {
		Locale locale = null;
		String localeName = contextService.getCallersUser().getLocale();
        if( !StringTools.isEmpty(localeName) ) {
        	locale = new Locale(localeName);
        }
        if( locale == null ) {
        	locale = Locale.getDefault();
        }
        return locale;
	}
	private ItemUnit createItemUnit(String name, ItemUnitType unitType, int baseFactor, ItemUnit baseUnit) {
		ItemUnit itemUnit = null;
		itemUnit = itemUnitService.getByName(name);
		if( itemUnit == null ) {
			itemUnit = entityGenerator.generateEntity( ItemUnit.class );
			itemUnit.setName(name);
			manager.persist(itemUnit);
		}
		itemUnit.setUnitType(unitType);
		itemUnit.setBaseFactor(baseFactor);
		itemUnit.setBaseUnit(baseUnit);
			
		return itemUnit;
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
	

	private LocationType createLocationType(Client client, String name) {
		LocationType type = null;
		try {
			type = locationTypeService.getByName(name);
		} catch (Exception e) {
			// ignore
		}
		if( type == null ) {
			type = entityGenerator.generateEntity( LocationType.class );
			type.setName(name);
			manager.persist(type);
		}

		
		return type;
	}

	private UnitLoadType createUnitLoadType(Client client, String name, double depth, double width, double height, double liftingCapacity, double weight) {
		UnitLoadType type = null;
		type = ultService.getByName(name);
		if( type == null ) {
			type = entityGenerator.generateEntity( UnitLoadType.class );
			type.setName(name);
			manager.persist(type);
		}

		type.setDepth(BigDecimal.valueOf(depth));
		type.setWidth(BigDecimal.valueOf(width));
		type.setHeight(BigDecimal.valueOf(height));
		type.setWeight(BigDecimal.valueOf(weight));
		type.setLiftingCapacity(BigDecimal.valueOf(liftingCapacity));

		return type;
	}

	private TypeCapacityConstraint createCapa( LocationType slType, UnitLoadType ulType, BigDecimal capa ) {
		TypeCapacityConstraint constraint = null;
		constraint = capaService.getByTypes(slType, ulType);
		if( constraint == null ) {
			constraint = entityGenerator.generateEntity( TypeCapacityConstraint.class );
			constraint.setStorageLocationType(slType);
			constraint.setUnitLoadType(ulType);
			constraint.setAllocation(capa);
			manager.persist(constraint);
		}
		
		return constraint;
	}

	private void createRack(Client client, String name, String locationName, int noColumns, int noLoc, int noRows, Area area, LocationType type, LocationCluster cluster, Zone zone) {

		for(int x1=0; x1<noColumns; x1++){
			for(int x2=0; x2<noLoc; x2++){
				for(int y=0; y<noRows; y++){
	//				String slName = locationName + "-" + String.format("%1$02d",y+1) + "-" + String.format("%1$03d",x+1) ;
					String slName = locationName + "-" + String.format("%1$02d",x1+1) + String.format("%1$01d",x2+1) + "-" + String.format("%1$01d",y+1) ;
					StorageLocation rl = null;
					rl = slService.getByName(slName);
					if( rl == null ) {
						rl = entityGenerator.generateEntity( StorageLocation.class );
						rl.setClient(client);
						rl.setName(slName);
						rl.setLocationType(type);
						rl.setRack(name);
						rl.setArea(area);
						manager.persist(rl);
					}				
					rl.setClient(client);
					rl.setArea(area);
					rl.setLocationType(type);
					rl.setRack(name);
					rl.setLocationCluster(cluster);
					rl.setZone(zone);
				}
			}
		}
	}

	private ItemData createItemData(Client client, String number, String name, String descr, ItemUnit unit, boolean lotMandatory, SerialNoRecordType serialType, Zone zone, UnitLoadType type) {
		ItemData itemData = null;
		itemData = itemDataService.getByItemNumber(client, number);
		if( itemData == null ) {
			itemData = entityGenerator.generateEntity( ItemData.class );
			itemData.setClient(client);
			itemData.setNumber(number);
			itemData.setItemUnit(unit);
			manager.persist(itemData);
		}

		itemData.setName(name);
		itemData.setDescription(descr);
		itemData.setItemUnit(unit);
		itemData.setLotMandatory(lotMandatory);
		itemData.setSerialNoRecordType(serialType);
		itemData.setZone(zone);
		itemData.setTradeGroup( resolve("ItemDataTradeGroup"));
		itemData.setDefaultUnitLoadType(type);
		return itemData;
	}
	
	private ItemDataNumber createEAN(ItemData itemData, String code) {
		ItemDataNumber ean = null;
		List<ItemDataNumber> eanList = eanService.getListByNumber(null, code);
		if( eanList == null || eanList.size() == 0 ) {
			try {
				ean = eanService.create(itemData, code);
			} catch (FacadeException e) {
				e.printStackTrace();
			}
		}
		return ean;
	}
	private LOSFixedLocationAssignment createFixLocation( Client client, String locationName, ItemData itemData, LocationType locType ) throws FacadeException {
		StorageLocation sl = slService.getByName(locationName);
		if(sl == null) {
			return null;
		}
		
		sl.setType(locType);
		LOSFixedLocationAssignment fl = null;
		List<LOSFixedLocationAssignment> flList = fixedService.getByItemData(itemData);
		for( LOSFixedLocationAssignment x : flList ) {
			manager.remove(x);
		}
		LOSFixedLocationAssignment x = fixedService.getByLocation(sl);
		if( x != null ) {
			manager.remove(x);
		}
		manager.flush();
		
		fl = entityGenerator.generateEntity( LOSFixedLocationAssignment.class );
		fl.setItemData(itemData);
		fl.setAssignedLocation(sl);
		fl.setDesiredAmount(new BigDecimal(400));

		manager.persist(fl);

		
		sl.setType(locationTypeService.getNoRestrictionType());
		
		UnitLoadType pickUlType = ultService.getPickLocationUnitLoadType();
		UnitLoad ul = null;
		try {
			ul = ulQueryService.getByLabelId(locationName);
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( ul == null ) {
			ul = ulService.createLOSUnitLoad(client, locationName, pickUlType, sl);
		}
		else {
			ul.setClient(client);
			ul.setUnitLoadType(pickUlType);
			ul.setStorageLocation(sl);
		}
		
		ul.setPackageType(UnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE);
		
		return fl;
	}
	
	private void createStock( Client client, String locationName, ItemData idat, BigDecimal amount, String unitLoadNumber, String lotNumber) {
		try {
			UnitLoad ul = ulQueryService.getByLabelId(unitLoadNumber);
			if( ul != null ) {
				StorageLocation sl = slService.getByName(locationName);
				LOSFixedLocationAssignment x = fixedService.getByLocation(sl);
				if( x == null ) {
					log.error("Do not create UnitLoad " + unitLoadNumber+" twice");
					return;
				}
			}
			inventoryFacade.createStockUnitOnStorageLocation(client.getNumber(), locationName, idat.getNumber(), lotNumber, amount, unitLoadNumber);
		} catch (Exception e) {
			log.error("Error creating Stock: "+e.getMessage(), e);
		}
		
	}
	
	private void createAdvice( Client client, ItemData idat, BigDecimal amount ) {
		try {
			inventoryFacade.createAvis(client.getNumber(), idat.getNumber(), null, amount, new Date(), null, null, false);
		} catch (Exception e) {
			log.error("Error creating Advice: "+e.getMessage(), e);
		}
		
	}

	private void createOrder( Client client, ItemData idat1, BigDecimal amount1, ItemData idat2, BigDecimal amount2 ) {
		try {
			List<OrderPositionTO> posList = new ArrayList<OrderPositionTO>();
			if( idat1 != null ) {
				OrderPositionTO pos = new OrderPositionTO(client.getNumber(), null, idat1.getNumber(), amount1);
				posList.add(pos);
			}
			if( idat2 != null ) {
				OrderPositionTO pos = new OrderPositionTO(client.getNumber(), null, idat2.getNumber(), amount2);
				posList.add(pos);
			}
			List<StorageLocation>locList = locService.getListForGoodsOut();
			String locName = null;
			if( locList != null && locList.size() > 0 ) {
				StorageLocation loc = locList.get(0);
				locName = loc.getName();
			}
			orderFacade.order(client.getNumber(), null, posList.toArray(new OrderPositionTO[posList.size()]), null, null, locName, null, new Date(), LOSCustomerOrder.PRIO_DEFAULT, true, false, null );

		} catch (Exception e) {
			log.error("Error creating Order: "+e.getMessage(), e);
		}
	}

	private void createStocktaking( Client client, String locationName ) {
		try {
		stService.generateOrders(true, client.getId(), null, null, null, locationName, null, null, null, true, true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private final String resolve( String key ) {
		Locale locale = null;
		if (key == null) {
            return "";
        }
        
        if( !StringTools.isEmpty(contextService.getCallersUser().getLocale()) ) {
        	locale = new Locale(contextService.getCallersUser().getLocale());
        }
        if( locale == null ) {
        	locale = Locale.getDefault();
        }
        
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("translation.TopologyBundle", locale, RefTopologyFacadeBean.class.getClassLoader());
            // resolving key
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
