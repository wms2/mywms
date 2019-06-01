/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.test.TestUtilities;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import junit.framework.TestCase;

/**
 * 
 * @author trautm
 */
public class StorageFacadeBeanTest extends TestCase {

	private static final Logger log = Logger
			.getLogger(StorageFacadeBeanTest.class);
	StorageFacade bean;

	public StorageFacadeBeanTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.bean = TestUtilities.beanLocator.getStateless(StorageFacade.class);
		TopologyBeanTest t = new TopologyBeanTest();
		t.initServices();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test of getStorageRequest method, of class StorageFacadeBean.
	 */
	public void testGetStorageRequest() throws Exception {
		String label = null;
		LOSUnitLoadQueryRemote ulQuery = TestUtilities.beanLocator
				.getStateless(LOSUnitLoadQueryRemote.class);
		LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator
				.getStateless(LOSStorageLocationQueryRemote.class);
		StockUnitQueryRemote suQuery = TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class);
		
		QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE,"labelId", true);
		
		TemplateQueryWhereToken t = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "storageLocation.name",
				LocationTestTopologyRemote.SL_WE_TESTCLIENT_NAME);
//		TemplateQueryWhereToken tc = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_EQUAL, "client.number",
//				.CommonTestTopologyRemote.TESTCLIENT_NUMBER);
		TemplateQuery q = new TemplateQuery();
		q.addWhereToken(t);
//		q.addWhereToken(tc);
		q.setBoClass(UnitLoad.class);

		List<UnitLoad> l = ulQuery.queryByTemplate(d, q);
		
		Vector<String> labels = new Vector<String>();
		for (UnitLoad u : l) {
			UnitLoad ul = ulQuery.queryById(u.getId());
			if (ul.getStockUnitList().size() > 0) {
				for (StockUnit su : ul.getStockUnitList()){
					su = suQuery.queryById(ul.getStockUnitList().get(0).getId());
					if (su.getClient().equals(
							TopologyBeanTest.getTESTCLIENT()) 
							|| su.getClient().equals(TopologyBeanTest.getTESTMANDANT())) {
//						if (ul.getStorageLocation().getArea().getAreaType().equals(LOSAreaType.GOODS_IN)) {
						if (ul.getStorageLocation().getArea().isUseFor(AreaUsages.GOODS_IN)) {
							label = ul.getLabelId();
							labels.add(label);
						}
					} else{
						log.error("Wrong client - skip " + ul.toDescriptiveString());
					}
				}
			}
		}

		if (labels.size() == 0) {
			fail("No UnitLoad on Goods in location for TestClientd");
		}

		String lastLotXLabel = null;
		String lastLotXLabelTestMandant = null;
		String testUnitLoadWrong = null;
		String testFullStorageLocation = null;

		for (String ulLabel : labels) {
			// Test normal process
			label = ulLabel;
			LOSStorageRequest r = bean.getStorageRequest(label, false);
			assertNotNull(r);

			UnitLoad ul = ulQuery.queryById(r.getUnitLoad()
					.getId());
			StockUnit su = ul.getStockUnitList().get(0);

			if (su.getLot().getName().equals(InventoryTestTopologyRemote.LOT_N1_A1_NAME)) {
				log.info("Request: " + r.toDescriptiveString());
				assertNotNull(r.getUnitLoad());
				assertNotNull(r.getRequestState());
				assertEquals(r.getRequestState(), LOSStorageRequestState.RAW);
				assertNotNull(r.getDestination());
				
				StorageLocation dest = r.getDestination();
				dest = slQuery.queryById(dest.getId());
				Client c = dest.getClient();		
				assertTrue(c.equals(TopologyBeanTest.getTESTCLIENT())
						|| c.equals(TopologyBeanTest.getTESTMANDANT()));

				bean.finishStorageRequest(label, r.getDestination().getName(),
						false, false);
				StorageLocation sl = slQuery.queryByIdentity(r
						.getDestination().getName());
				assertTrue(sl.getUnitLoads().size() == 1);
				assertEquals(sl.getUnitLoads().get(0), r.getUnitLoad());

			} else if (su.getLot().getName().equals(
					ManageInventoryFacadeBeanTest.TEST_LOT)) {
				// Test Wrong
				try {
					bean.finishStorageRequest(label, "T1-1-1", false, false);
				} catch (InventoryException ex) {
					if (ex
							.getInventoryExceptionKey()
							.equals(
									InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED)) {
						
						bean.finishStorageRequest(label, r.getDestination()
								.getName(), true, false);
						
						testUnitLoadWrong = label;
						testFullStorageLocation = r.getDestination().getName();
					} else {
						log.error(ex, ex);
						fail(ex.getMessage());
					}
				} catch (LOSLocationException ex) {
					log.error(ex, ex);
					bean.finishStorageRequest(label, r.getDestination()
							.getName(), true, false);
				}
			} else if (su.getLot().getName().equals(
					ManageInventoryFacadeBeanTest.TEST_LOT_X)) {
				// Test zuschuetten
				if (lastLotXLabel != null || lastLotXLabelTestMandant != null) {
					log.info("Request: " + r.toDescriptiveString());
					try {
						// try to place on full StorageLocation
						bean.finishStorageRequest(label,
								testFullStorageLocation, false, false);
						fail("Could place on full storage location");
					} catch (LOSLocationException ex) {
						if (ex
								.getLocationExceptionKey()
								.equals(
										LOSLocationExceptionKey.STORAGELOCATION_ALLREADY_FULL)) {
							// should not work as well
							try {
								// try to place on full StorageLocaiton
								bean.finishStorageRequest(label,
										testFullStorageLocation, false, true);
								fail("Could place on full storage location");
							} catch (LOSLocationException lex) {
								if (lex.getLocationExceptionKey().equals(
												LOSLocationExceptionKey.STORAGELOCATION_ALLREADY_FULL)) {
									try{
										slQuery.queryByIdentity("T1-1-1-1");
										// try to place on wrong fixed assigned storloc
										bean.finishStorageRequest(label,
												"T1-1-1-1", false, true);
									} catch (FacadeException ex3){
										//ok
									}
								} else{
									fail(ex.getMessage());
								}
							}
						} else {
							fail(ex.getMessage());
						}
					}
					try {
						// UnitLoad with other lot : should not work
						bean.finishStorageRequest(label, testUnitLoadWrong,
								true, false);
					} catch (InventoryException ex) {
						if (ex.getInventoryExceptionKey()
								.equals(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED) || true) {// TODO
							// now it should
							String addLabel = "";
							try {
								
								if (su.getClient().getNumber().equals(CommonTestTopologyRemote.TESTCLIENT_NUMBER)){
									addLabel = lastLotXLabel; 
								} else if (su.getClient().getNumber().equals(CommonTestTopologyRemote.TESTMANDANT_NUMBER)){
									addLabel = lastLotXLabelTestMandant; 
								}
								bean.finishStorageRequest(label, addLabel,
										false, false);
							} catch (InventoryException iex) {
								if (iex
										.getInventoryExceptionKey()
										.equals(
												InventoryExceptionKey.STORAGE_ADD_TO_EXISTING)) {
									UnitLoad u = r.getUnitLoad();
									u = ulQuery.queryById(u
											.getId());
									StockUnit sUnit = u.getStockUnitList().get(
											0);
									log.info("test zuschuetten/add to existing  from "
													+ label
													+ " to "
													+ addLabel);
									bean.finishStorageRequest(label,
											addLabel, true, false);
									u = ulQuery.queryById(u
											.getId());
									assertEquals(slQuery.getNirwanaName(),
											u.getStorageLocation().getName());
									assertTrue(u.getStockUnitList() == null
											|| u.getStockUnitList().size() == 0);
									sUnit = TopologyBeanTest.getSuQuery()
											.queryById(sUnit.getId());
									assertFalse(sUnit.getUnitLoad().equals(u));
									u = ulQuery.queryByIdentity(addLabel);
									// now there are two StockUnits on a pallet
//									//assertTrue(u.getStockUnitList().size() == 2);
									// but should be consolidated??
									assertTrue(u.getStockUnitList().size() == 1);
									lastLotXLabel = null; // reset
									lastLotXLabelTestMandant = null; // ... for next loop
								} else {
									log.error(iex, iex);
									fail(iex.getMessage());
								}
							}
						}
					}
				} else {
					log.info("Request: " + r.toDescriptiveString());
					assertNotNull(r.getUnitLoad());
					assertNotNull(r.getRequestState());
					assertEquals(r.getRequestState(),
							LOSStorageRequestState.RAW);
					assertNotNull(r.getDestination());
					
					StorageLocation dest = r.getDestination();
					dest = slQuery.queryById(dest.getId());
					Client c = dest.getClient();		
					assertTrue(c.equals(TopologyBeanTest
							.getTESTCLIENT()) || 
							c.equals(TopologyBeanTest.getTESTMANDANT()));
					bean.finishStorageRequest(label, r.getDestination()
							.getName(), false, false);
					if (su.getClient().getNumber().equals(CommonTestTopologyRemote.TESTCLIENT_NUMBER)){
						lastLotXLabel = label; //remember for later on
					} else if (su.getClient().getNumber().equals(CommonTestTopologyRemote.TESTMANDANT_NUMBER)){
						lastLotXLabelTestMandant = label; 
					}
					StorageLocation sl = slQuery.queryByIdentity(r
							.getDestination().getName());
					assertTrue(sl.getUnitLoads().size() == 1);
					assertEquals(sl.getUnitLoads().get(0), r.getUnitLoad());
					
				}
			}
		}

	}

}
