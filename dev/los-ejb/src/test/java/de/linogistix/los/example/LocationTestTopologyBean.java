/*
 *
 * Created on 12. September 2006, 09:57
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.example;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;
import org.mywms.service.ClientService;
import org.mywms.service.ItemUnitService;
import org.mywms.service.RoleService;
import org.mywms.service.UserService;

import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.location.crud.LOSAreaCRUDRemote;
import de.linogistix.los.location.crud.LOSRackCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote;
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.model.LOSUnitLoadRecord;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSFixedLocationAssignmentQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadRecordQueryRemote;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * Creates an example topology
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless()
public class LocationTestTopologyBean implements LocationTestTopologyRemote {

	private static final Logger log = Logger.getLogger(LocationTestTopologyBean.class);
	
	// --------------------------------------------------------------------------
	Client SYSTEMCLIENT;
	Client TESTCLIENT;
	Client TESTMANDANT;
		
	protected UnitLoadType KLT;
		
	protected LOSStorageLocationType PALETTENPLATZ_TYP_2;
		
	protected LOSStorageLocationType KOMMPLATZ_TYP;
	
	protected LOSTypeCapacityConstraint VIELE_PALETTEN;
	
	protected LOSTypeCapacityConstraint EINE_PALETTE;
	
	protected LOSTypeCapacityConstraint KOMM_FACH_DUMMY_LHM_CONSTR;
	
	protected LOSArea STORE_AREA;
	
	protected LOSArea KOMM_AREA;
	
	protected LOSArea WE_BEREICH;
	
	protected LOSArea WA_BEREICH;
	
	protected LOSArea CLEARING_BEREICH;
	
	protected LOSArea PRODUCTION_BEREICH;
	
	protected LOSStorageLocation SL_WE;
	
	protected LOSStorageLocation SL_PRODUCTION;
	
	protected LOSRack TEST_RACK_1;
	
	protected LOSRack TEST_RACK_2;
	
	protected LOSStorageLocation SL_WA;
	
	protected LOSStorageLocation SL_NIRWANA;
	
	protected LOSStorageLocation SL_CLEARING;
		
	
	@EJB
    RoleService roleService;
    @EJB
    UserService userService;
    @EJB
    ClientService clientService;
	@EJB
	ClientQueryRemote clientQuery;
	@EJB
	ClientCRUDRemote clientCrud;
	
	@EJB
	UnitLoadTypeQueryRemote ulTypeQuery;
	@EJB
	UnitLoadTypeCRUDRemote ulTypeCrud;
	@EJB
	LOSStorageLocationTypeQueryRemote slTypeQuery;
	@EJB
	LOSStorageLocationTypeService slTypeService;
	@EJB
	LOSStorageLocationTypeCRUDRemote slTypeCrud;
	@EJB
	LOSTypeCapacityConstraintQueryRemote typeCapacityConstaintQuery;
	@EJB
	LOSTypeCapacityConstraintCRUDRemote typeCapacityConstaintCrud;
	@EJB
	QueryTypeCapacityConstraintService typeCapacityConstaintService;
	@EJB
	LOSAreaQueryRemote areaQuery;
	@EJB
	LOSAreaCRUDRemote areaCrud;
	@EJB
	LOSStorageLocationQueryRemote slQuery;
	@EJB
	LOSStorageLocationCRUDRemote slCrud;
	@EJB
	RackQueryRemote rackQuery;
	@EJB
	LOSRackCRUDRemote rackCrud;
	@EJB
	UnitLoadQueryRemote ulQuery;
	@EJB
	UnitLoadCRUDRemote ulCrud;
	
	@EJB
	LOSFixedLocationAssignmentQueryRemote assQuery;
	
	@EJB
	LOSUnitLoadRecordQueryRemote ulRecordQuery;
	
	@EJB
	ItemUnitService itemUnitService;
    @EJB
    LOSStorageLocationService locationService;
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager em;

	/** Creates a new instance of TopologyBean */
	public LocationTestTopologyBean() {
	}
	
	//---------------------------------------------------
	
	//-----------------------------------------------------------------

	public void create() throws LocationTopologyException {
		try {
			createClients();
			
			createUnitLoadTypes();
			createStorageLocationsTyp();
			createCapacityConstraints();
			createAreas();
			
			createStorageLocations();
			createRacks();
			em.flush();
		} catch (FacadeException ex) {
			log.error(ex, ex);
			throw new LocationTopologyException();
		}

	}

	public void createClients() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {

		SYSTEMCLIENT = clientQuery.getSystemClient();

		if (SYSTEMCLIENT == null) {
			log.error("No System CLient found");
			throw new LocationTopologyException();
		}

		try {
			TESTCLIENT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			TESTCLIENT = new Client();
			TESTCLIENT.setName(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
			TESTCLIENT.setNumber(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
			TESTCLIENT.setCode(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
			em.persist(TESTCLIENT);
		}
		try {
			TESTMANDANT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			TESTMANDANT = new Client();
			TESTMANDANT.setName(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
			TESTMANDANT.setNumber(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
			TESTMANDANT.setCode(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
			em.persist(TESTMANDANT);
		}
		
	}

	public void createUnitLoadTypes() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {		
		
		try {
			KLT = ulTypeQuery.queryByIdentity(KLT_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			KLT = new UnitLoadType();
			KLT.setName(KLT_NAME);
			KLT.setDepth(BigDecimal.valueOf(0.6));
			KLT.setWidth(BigDecimal.valueOf(0.4));
			KLT.setWeight(BigDecimal.valueOf(30));
			KLT.setHeight(BigDecimal.valueOf(0.45));
			KLT.setAdditionalContent("KLT Behaelter 400 mm x 600 mm, bis 450 mm hoch, 30 ITEM_KG");
			em.persist(KLT);
		}

	}

	public void createStorageLocationsTyp() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {
		
		
		try {
			PALETTENPLATZ_TYP_2 = slTypeQuery
					.queryByIdentity(PALETTENPLATZ_TYP_2_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			// Typ Palettenplatz
			PALETTENPLATZ_TYP_2 = new LOSStorageLocationType();
			PALETTENPLATZ_TYP_2.setName(PALETTENPLATZ_TYP_2_NAME);
			em.persist(PALETTENPLATZ_TYP_2);
		}

		try {
			KOMMPLATZ_TYP = slTypeQuery.queryByIdentity(KOMMPLATZ_TYP_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			// Typ Kommplatz
			KOMMPLATZ_TYP = new LOSStorageLocationType();
			KOMMPLATZ_TYP.setName(KOMMPLATZ_TYP_NAME);
			em.persist(KOMMPLATZ_TYP);
		}
		
	}

	public void createCapacityConstraints() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {

		LOSStorageLocationType slTypeDefault = slTypeService.getDefaultStorageLocationType();
		UnitLoadType PALETTE = ulTypeQuery.getDefaultUnitLoadType();
		UnitLoadType DUMMY_KOMM_ULTYPE = ulTypeQuery.getPickLocationUnitLoadType();
		
		EINE_PALETTE = null;
		try {
			EINE_PALETTE = typeCapacityConstaintQuery.queryByIdentity(EINE_PALETTE_NAME);
		} catch (BusinessObjectNotFoundException ex) {}
		
		if( EINE_PALETTE == null ) {
			EINE_PALETTE = typeCapacityConstaintService.getByTypes(slTypeDefault, PALETTE);
		}
		if( EINE_PALETTE == null ) {
			// Kapazitaet "unbegrenzt"
			EINE_PALETTE = new LOSTypeCapacityConstraint();
//			EINE_PALETTE.setName(EINE_PALETTE_NAME);
			EINE_PALETTE.setUnitLoadType(PALETTE);
			EINE_PALETTE.setStorageLocationType(slTypeDefault);
			EINE_PALETTE.setAllocation( new BigDecimal(100) );
			em.persist(EINE_PALETTE);
		}

		KOMM_FACH_DUMMY_LHM_CONSTR = null;
		try {
			KOMM_FACH_DUMMY_LHM_CONSTR = typeCapacityConstaintQuery
					.queryByIdentity(KOMM_FACH_DUMMY_LHM_CONSTR_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			KOMM_FACH_DUMMY_LHM_CONSTR = typeCapacityConstaintService.getByTypes(KOMMPLATZ_TYP, DUMMY_KOMM_ULTYPE);
		}
		if( KOMM_FACH_DUMMY_LHM_CONSTR == null ) {
			// Kapazitaet "unbegrenzt"
			KOMM_FACH_DUMMY_LHM_CONSTR = new LOSTypeCapacityConstraint();
//			KOMM_FACH_DUMMY_LHM_CONSTR.setName(KOMM_FACH_DUMMY_LHM_CONSTR_NAME);
			KOMM_FACH_DUMMY_LHM_CONSTR.setUnitLoadType(DUMMY_KOMM_ULTYPE);
			KOMM_FACH_DUMMY_LHM_CONSTR.setStorageLocationType(KOMMPLATZ_TYP);
			KOMM_FACH_DUMMY_LHM_CONSTR.setAllocation( new BigDecimal(100) );
			em.persist(KOMM_FACH_DUMMY_LHM_CONSTR);
		}
	}

	public void createAreas() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {
		try {
			STORE_AREA = areaQuery.queryByIdentity(STORE_AREA_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			STORE_AREA = new LOSArea();
			STORE_AREA.setClient(SYSTEMCLIENT);
			STORE_AREA.setName(STORE_AREA_NAME);
			STORE_AREA.setUseForStorage(true);
			em.persist(STORE_AREA);
		}

		try {
			KOMM_AREA = areaQuery.queryByIdentity(KOMM_AREA_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			KOMM_AREA = new LOSArea();
			KOMM_AREA.setClient(SYSTEMCLIENT);
			KOMM_AREA.setName(KOMM_AREA_NAME);
			KOMM_AREA.setUseForPicking(true);
			em.persist(KOMM_AREA);
		}

		try {
			WE_BEREICH = areaQuery.queryByIdentity(WE_BEREICH_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			WE_BEREICH = new LOSArea();
			WE_BEREICH.setClient(SYSTEMCLIENT);
			WE_BEREICH.setName(WE_BEREICH_NAME);
			WE_BEREICH.setUseForGoodsIn(true);
			em.persist(WE_BEREICH);
		}

		try {
			WA_BEREICH = areaQuery.queryByIdentity(WA_BEREICH_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			WA_BEREICH = new LOSArea();
			WA_BEREICH.setClient(SYSTEMCLIENT);
			WA_BEREICH.setName(WA_BEREICH_NAME);
			WA_BEREICH.setUseForGoodsOut(true);
			em.persist(WA_BEREICH);
		}
		
		try {
			CLEARING_BEREICH = areaQuery.queryByIdentity(CLEARING_BEREICH_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			CLEARING_BEREICH = new LOSArea();
			CLEARING_BEREICH.setClient(SYSTEMCLIENT);
			CLEARING_BEREICH.setName(CLEARING_BEREICH_NAME);
			em.persist(CLEARING_BEREICH);
		}
		
		try {
			PRODUCTION_BEREICH = areaQuery.queryByIdentity(PRODUCTION_BEREICH_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			PRODUCTION_BEREICH = new LOSArea();
			PRODUCTION_BEREICH.setClient(SYSTEMCLIENT);
			PRODUCTION_BEREICH.setName(PRODUCTION_BEREICH_NAME);
			em.persist(PRODUCTION_BEREICH);
		}
	}

	public void createStorageLocations() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {

		LOSStorageLocationType slTypeNorestriction = slTypeService.getNoRestrictionType();

		try {
			SL_WE = slQuery.queryByIdentity(TESTCLIENT,SL_WE_TESTCLIENT_NAME).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			SL_WE = new LOSStorageLocation();
			SL_WE.setName(SL_WE_TESTCLIENT_NAME);
			SL_WE.setClient(TESTCLIENT);
			SL_WE.setType(slTypeNorestriction);
			SL_WE.setArea(WE_BEREICH);
			em.persist(SL_WE);
		}

		try {
			SL_WA = slQuery.queryByIdentity(TESTCLIENT, SL_WA_TESTCLIENT_NAME).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			SL_WA = new LOSStorageLocation();
			SL_WA.setName(SL_WA_TESTCLIENT_NAME);
			SL_WA.setClient(TESTCLIENT);
			SL_WA.setType(slTypeNorestriction);
			SL_WA.setArea(WA_BEREICH);
			em.persist(SL_WA);
		}
		
		try {
			SL_PRODUCTION = slQuery.queryByIdentity(TESTCLIENT, SL_PRODUCTION_TESTCLIENT_NAME).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			SL_PRODUCTION = new LOSStorageLocation();
			SL_PRODUCTION.setName(SL_PRODUCTION_TESTCLIENT_NAME);
			SL_PRODUCTION.setClient(TESTCLIENT);
			SL_PRODUCTION.setType(slTypeNorestriction);
			SL_PRODUCTION.setArea(PRODUCTION_BEREICH);
			em.persist(SL_PRODUCTION);
		}
		
		try {
			SL_WE = slQuery.queryByIdentity(TESTMANDANT,SL_WE_TESTMANDANT_NAME).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			SL_WE = new LOSStorageLocation();
			SL_WE.setName(SL_WE_TESTMANDANT_NAME);
			SL_WE.setClient(TESTMANDANT);
			SL_WE.setType(slTypeNorestriction);
			SL_WE.setArea(WE_BEREICH);
			em.persist(SL_WE);
		}

		try {
			SL_WA = slQuery.queryByIdentity(TESTMANDANT, SL_WA_TESTMANDANT_NAME).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			SL_WA = new LOSStorageLocation();
			SL_WA.setName(SL_WA_TESTMANDANT_NAME);
			SL_WA.setClient(TESTMANDANT);
			SL_WA.setType(slTypeNorestriction);
			SL_WA.setArea(WA_BEREICH);
			em.persist(SL_WA);
		}
		
		try {
			SL_PRODUCTION = slQuery.queryByIdentity(TESTMANDANT, SL_PRODUCTION_TESTMANDANT_NAME).get(0);
		} catch (BusinessObjectNotFoundException ex) {
			SL_PRODUCTION = new LOSStorageLocation();
			SL_PRODUCTION.setName(SL_PRODUCTION_TESTMANDANT_NAME);
			SL_PRODUCTION.setClient(TESTMANDANT);
			SL_PRODUCTION.setType(slTypeNorestriction);
			SL_PRODUCTION.setArea(PRODUCTION_BEREICH);
			em.persist(SL_PRODUCTION);
		}
		
	}

	public void createRacks() throws LocationTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException, BusinessObjectNotFoundException,
			BusinessObjectModifiedException, BusinessObjectMergeException {
		
		LOSStorageLocationType slTypeDefault = slTypeService.getDefaultStorageLocationType();
		UnitLoadType DUMMY_KOMM_ULTYPE = ulTypeQuery.getPickLocationUnitLoadType();
		
		try {
			TEST_RACK_1 = rackQuery.queryByIdentity(TEST_RACK_1_NAME);
		} catch (BusinessObjectNotFoundException ex) {

			TEST_RACK_1 = new LOSRack();
			TEST_RACK_1.setName(TEST_RACK_1_NAME);
			TEST_RACK_1.setLabelOffset(new Integer(3));
			TEST_RACK_1.setClient(TESTCLIENT);
			em.persist(TEST_RACK_1);
		}
		
		try {
			TEST_RACK_2 = rackQuery.queryByIdentity(TEST_RACK_2_NAME);
		} catch (BusinessObjectNotFoundException ex) {

			TEST_RACK_2 = new LOSRack();
			TEST_RACK_2.setName(TEST_RACK_2_NAME);
			TEST_RACK_2.setLabelOffset(new Integer(3));
			TEST_RACK_2.setClient(TESTMANDANT);
			em.persist(TEST_RACK_2);
		}

		for (int x = 1; x < 5; x++) {
			for (int y = 1; y < 4; y++) {
				LOSStorageLocation rl;
				String locName = TEST_RACK_1.getName() + "-1-" + y + "-" + x;
				try {
					rl = slQuery.queryByIdentity(locName);
				} catch (BusinessObjectNotFoundException ex) {
					rl = new LOSStorageLocation();
					rl.setClient(TESTCLIENT);
					rl.setArea(KOMM_AREA);
					rl.setName(locName);
					rl.setRack(TEST_RACK_1);
					rl.setType(KOMMPLATZ_TYP);
					rl
							.setCurrentTypeCapacityConstraint(KOMM_FACH_DUMMY_LHM_CONSTR);
					rl.setXPos(x);
					rl.setYPos(y);
					em.persist(rl);


					LOSUnitLoad ul = new LOSUnitLoad();
					ul.setClient(TESTCLIENT);
					ul.setLabelId(locName);
					ul.setType(DUMMY_KOMM_ULTYPE);
					ul.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE);
					ul.setStorageLocation(rl);
					em.persist(ul);
				}
			}
		}

		for (int x = 1; x < 5; x++) {
			for (int y = 4; y < 6; y++) {
				LOSStorageLocation rl;
				String locName = TEST_RACK_1.getName() + "-1-" + y + "-" + x;
				try {
					rl = slQuery.queryByIdentity(locName);
				} catch (BusinessObjectNotFoundException ex) {
					rl = new LOSStorageLocation();
					rl.setClient(TESTCLIENT);
					rl.setArea(STORE_AREA);
					rl.setName(locName);
					rl.setRack(TEST_RACK_1);
					rl.setType(slTypeDefault);
					rl.setXPos(x);
					rl.setYPos(y);
					em.persist(rl);

				}
			}
		}
		
		for (int x = 1; x < 3; x++) {
			for (int y = 1; y < 4; y++) {
				LOSStorageLocation rl;
				String locName = TEST_RACK_2.getName() + "-1-" + y + "-" + x;
				try {
					rl = slQuery.queryByIdentity(locName);
				} catch (BusinessObjectNotFoundException ex) {
					rl = new LOSStorageLocation();
					rl.setClient(TESTMANDANT);
					rl.setArea(KOMM_AREA);
					rl.setName(locName);
					rl.setRack(TEST_RACK_2);
					rl.setType(KOMMPLATZ_TYP);
					rl
							.setCurrentTypeCapacityConstraint(KOMM_FACH_DUMMY_LHM_CONSTR);
					rl.setXPos(x);
					rl.setYPos(y);
					em.persist(rl);


					LOSUnitLoad ul = new LOSUnitLoad();
					ul.setClient(TESTMANDANT);
					ul.setLabelId(locName);
					ul.setType(DUMMY_KOMM_ULTYPE);
					ul.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE);
					ul.setStorageLocation(rl);
					em.persist(ul);
				}
			}
		}

		for (int x = 1; x < 3; x++) {
			for (int y = 4; y < 6; y++) {
				LOSStorageLocation rl;
				String locName = TEST_RACK_2.getName() + "-1-" + y + "-" + x;
				try {
					rl = slQuery.queryByIdentity(locName);
				} catch (BusinessObjectNotFoundException ex) {
					rl = new LOSStorageLocation();
					rl.setClient(TESTMANDANT);
					rl.setArea(STORE_AREA);
					rl.setName(locName);
					rl.setRack(TEST_RACK_2);
					rl.setType(slTypeDefault);
					rl.setXPos(x);
					rl.setYPos(y);
					em.persist(rl);

				}
			}
		}
		

	}

	// ------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void remove(Class<BasicEntity> clazz) throws LocationTopologyException {

		try {
			List<BasicEntity> l;
			l = em.createQuery("SELECT o FROM " + clazz.getName() + " o")
					.getResultList();
			for (Iterator<BasicEntity> iter = l.iterator(); iter.hasNext();) {
				BasicEntity element = iter.next();
				element = (BasicEntity) em.find(clazz, element.getId());
				em.remove(element);
			}
			em.flush();

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new LocationTopologyException();
		}
	}

	private void initClient() throws LocationTopologyException{
		try {
			TESTCLIENT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
			TESTCLIENT = em.find(Client.class, TESTCLIENT.getId());
			
			TESTMANDANT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
			TESTMANDANT = em.find(Client.class, TESTMANDANT.getId());
			
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new LocationTopologyException();
		}		
	}

	public void clear() throws LocationTopologyException {
		try {
			initClient();

			clearUnitLoads();
			clearStorageLocations();
			clearRacks();
			clearLOSUnitLoadRecords();
			
		} catch (LocationTopologyException ex) {
			throw ex;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new LocationTopologyException();
		}
	}

	
	private void clearLOSUnitLoadRecords() throws LocationTopologyException {
		// Delete LogItems
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSUnitLoadRecord.class);

			List<LOSUnitLoadRecord> l = ulRecordQuery.queryByTemplate(d, q);
			for (LOSUnitLoadRecord u : l) {
				u = em.find(LOSUnitLoadRecord.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new LocationTopologyException();
		}

	}

	public void clearRacks() throws LocationTopologyException {

		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSRack.class);
			List<LOSRack> racks = rackQuery.queryByTemplate(d, q);
			for (LOSRack r : racks){
				r = em.find(LOSRack.class, r.getId());
				for (LOSStorageLocation rl : locationService.getListByRack(r)) {
					try {
						rl = slQuery.queryById(rl.getId());
						rl = em.find(LOSStorageLocation.class, rl.getId());
						for (LOSUnitLoad u : rl.getUnitLoads()) {
							u = (LOSUnitLoad) ulQuery.queryById(u.getId());
							u = em.find(LOSUnitLoad.class, u.getId());
							for (StockUnit su : u.getStockUnitList()) {
								su = em.find(StockUnit.class, su.getId());
								em.remove(su);
							}
							em.remove(u);
						}
						
					} catch (Throwable ex) {
						log.error(ex.getMessage(), ex);
					}
					log.info("Remove: " + rl.getName());
					em.remove(rl);
				}
				em.remove(r);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new LocationTopologyException();
		}
	}

	public void clearStorageLocations() throws LocationTopologyException {
		// Delete StorageLocations

		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "itemData.client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "itemData.client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSFixedLocationAssignment.class);

			List<LOSFixedLocationAssignment> l = assQuery.queryByTemplate(d, q);
			for (LOSFixedLocationAssignment u : l) {
				u = em.find(LOSFixedLocationAssignment.class, u.getId());
				em.remove(u);
			}
		} catch (Throwable e) {
			log.error(e, e);
			throw new LocationTopologyException();
		}
		
		try {

			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
						
			TemplateQuery q = new TemplateQuery();
			q.setDistinct(true);
			q.addWhereToken(t);
			q.addWhereToken(t2);
			
			q.setBoClass(LOSStorageLocation.class);

			List<LOSStorageLocation> l = slQuery.queryByTemplate(d, q);
			
			//l.add(slQuery.queryByIdentity(SL_WA_NAME));
			
			for (LOSStorageLocation rl : l) {
				try {
					
					rl = em.find(LOSStorageLocation.class, rl.getId());
					for (LOSUnitLoad u : rl.getUnitLoads()) {
						u = (LOSUnitLoad) ulQuery.queryById(u.getId());
						u = em.find(LOSUnitLoad.class, u.getId());
						for (StockUnit su : u.getStockUnitList()) {
							su = em.find(StockUnit.class, su.getId());
							em.remove(su);
						}
						em.remove(u);
					}
	
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
				}
				log.info("Remove: " + rl.getName());
				em.remove(rl);
			}
			
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new LocationTopologyException();
		}
	}

	public void clearUnitLoads() throws LocationTopologyException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSUnitLoad.class);

			List<UnitLoad> l = ulQuery.queryByTemplate(d, q);
			for (UnitLoad u : l) {
				u = em.find(LOSUnitLoad.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new LocationTopologyException();
		}
	}
}
