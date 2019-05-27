/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.report.ReportException;
import de.linogistix.los.report.ReportExceptionKey;
import de.linogistix.los.test.TestUtilities;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * 
 * @author trautm
 */
public class LOSGoodsReceiptFacadeBeanTest extends TestCase {

	private static final Logger logger = Logger
			.getLogger(LOSGoodsReceiptFacadeBeanTest.class);
	public LOSGoodsReceiptFacade bean;
	protected TopologyBeanTest topology;

	public LOSGoodsReceipt GR;

	BODTO<Client> cdto;
	BODTO<StorageLocation> goodsInLocation;

	public LOSGoodsReceiptFacadeBeanTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.bean = TestUtilities.beanLocator
				.getStateless(LOSGoodsReceiptFacade.class);
		topology = new TopologyBeanTest();
		topology.initServices();

		Client c = TopologyBeanTest.getTESTCLIENT();
		this.cdto = new BODTO<Client>(c.getId(), c.getVersion(), c.getNumber());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAllowed() {
		List<BODTO<Lot>> lots;
		
		try {
			LotQueryRemote lotQuery = TestUtilities.beanLocator
					.getStateless(LotQueryRemote.class);
			Client c = TopologyBeanTest.getTESTCLIENT();
			this.cdto = new BODTO<Client>(c.getId(), c.getVersion(), c
					.getNumber());
			
//			lots = lotQuery.autoCompletion("");
//			assertTrue(lots.size() > 0);
//			
//			lots = lotQuery.autoCompletion("", c);
//			assertTrue(lots.size() > 0);
//			
			List<BODTO<ItemData>> idats = bean.getAllowedItemData(
					ManageInventoryFacadeBeanTest.TEST_ITEM, cdto, null);
			assertTrue(idats.size() == 1);

			System.out.println("--- class of itdats : "+idats.get(0).getClass().toString());
			
			BODTO<ItemData> idat = idats.get(0);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "itemData.id", idat.getId());
			lots = lotQuery.autoCompletion("", c, new TemplateQueryWhereToken[]{t});
			assertNotNull(lots);
			assertTrue(lots.size() > 0);
	
//			lots = bean.getAllowedLots("", cdto, idat);
//			assertTrue(lots.size() == 1);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			fail(ex.getMessage());
		}

	}
	
	/**
	 * Test of createGoodsReceipt method, of class LOSGoodsReceiptFacadeBean.
	 */
	public void testGoodsReceipt() {
		try {
			System.out.println("createGoodsReceipt");
			
			UnitLoadTypeQueryRemote ulTypeQuery = TestUtilities.beanLocator
			.getStateless(UnitLoadTypeQueryRemote.class);
			
			LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator
			.getStateless(LOSStorageLocationQueryRemote.class);
			UnitLoadType defType = ulTypeQuery.getDefaultUnitLoadType();
			BODTO<UnitLoadType> defTypeTO = new BODTO<UnitLoadType>(defType.getId(), defType.getVersion(), defType.getName());

			LOSAdviceQueryRemote advQuery;
			advQuery = TestUtilities.beanLocator
					.getStateless(LOSAdviceQueryRemote.class);

			Client c = TopologyBeanTest.getTESTCLIENT();
			StorageLocation sl = slQuery.queryByIdentity(c, LocationTestTopologyRemote.SL_WE_TESTCLIENT_NAME).get(0);

			cdto = new BODTO<Client>(c.getId(), c.getVersion(), c.getNumber());
			goodsInLocation = new BODTO<StorageLocation>(sl.getId(), sl
					.getVersion(), sl.getName());

			String licencePlate = "";
			String driverName = "";
			String forwarder = "";
			String deliveryNoteNumber = "";
			Date receiptDate = new Date();

			LOSGoodsReceipt result = bean.createGoodsReceipt(cdto,
					licencePlate, driverName, forwarder, deliveryNoteNumber,
					receiptDate, goodsInLocation, "");
			assertNotNull(result);
			this.GR = result;
			BODTO<ItemData> idat;
			BODTO<Lot> lot;
			LOSAdvice adv;
			BODTO<LOSAdvice> advice ;
			
			// position 1
			try{
				idat = bean.getAllowedItemData(
						ManageInventoryFacadeBeanTest.TEST_ITEM, cdto, null).get(0);
				lot = bean.getAllowedLots("", cdto, idat).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv
						.getVersion(), adv.toUniqueString());
				bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, adv.getNotifiedAmount(), advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.FINISHED);
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			// position 2
			try{
				lot = bean.getAllowedLots(InventoryTestTopologyRemote.LOT_N1_A1_NAME, cdto, null)
						.get(0);
				idat = bean.getAllowedItemData("", cdto, lot).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv.getVersion(), adv
						.toUniqueString());
				bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, adv.getNotifiedAmount().subtract(new BigDecimal(200)), advice, LOSGoodsReceiptType.INTAKE, 0, "");
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.GOODS_TO_COME);
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			try{
				// position 3
				lot = bean.getAllowedLots(ManageInventoryFacadeBeanTest.TEST_LOT_X,
						cdto, null).get(0);
				idat = bean.getAllowedItemData("", cdto, lot).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv.getVersion(), adv
						.toUniqueString());
				LOSGoodsReceiptPosition pos3 = bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, adv.getNotifiedAmount().subtract(new BigDecimal(1000)) , advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.GOODS_TO_COME);
				assertTrue(pos3.getStockUnit().getItemData().getId().equals(
					idat.getId()));
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			try{
				// position 4
				lot = bean.getAllowedLots(ManageInventoryFacadeBeanTest.TEST_LOT_X,
						cdto, null).get(0);
				idat = bean.getAllowedItemData("", cdto, lot).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv.getVersion(), adv
						.toUniqueString());
				LOSGoodsReceiptPosition pos4 = bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, new BigDecimal(2000),advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.OVERLOAD);
				assertTrue(pos4.getStockUnit().getItemData().getId().equals(
						idat.getId()));
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			bean.acceptGoodsReceipt(GR);

		} catch (FacadeException ex) {
			logger.error(ex, ex);
			fail(ex.getMessage());
		}

	}

	/**
	 * Test of createGoodsReceipt method, of class LOSGoodsReceiptFacadeBean.
	 */
	public void testGoodsReceiptMulitClient() {
		try {
			System.out.println("createGoodsReceipt");
			
			UnitLoadTypeQueryRemote ulTypeQuery = TestUtilities.beanLocator
			.getStateless(UnitLoadTypeQueryRemote.class);
			
			LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator
			.getStateless(LOSStorageLocationQueryRemote.class);
			
			UnitLoadType defType = ulTypeQuery.getDefaultUnitLoadType();
			BODTO<UnitLoadType> defTypeTO = new BODTO<UnitLoadType>(defType.getId(), defType.getVersion(), defType.getName());

			LOSAdviceQueryRemote advQuery;
			advQuery = TestUtilities.beanLocator
					.getStateless(LOSAdviceQueryRemote.class);

			Client c = TopologyBeanTest.getTESTMANDANT();
			StorageLocation sl = slQuery.queryByIdentity(c, LocationTestTopologyRemote.SL_WE_TESTMANDANT_NAME).get(0);

			cdto = new BODTO<Client>(c.getId(), c.getVersion(), c.getNumber());
			goodsInLocation = new BODTO<StorageLocation>(sl.getId(), sl
					.getVersion(), sl.getName());

			String licencePlate = "";
			String driverName = "";
			String forwarder = "";
			String deliveryNoteNumber = "";
			Date receiptDate = new Date();

			LOSGoodsReceipt result = bean.createGoodsReceipt(cdto,
					licencePlate, driverName, forwarder, deliveryNoteNumber,
					receiptDate, goodsInLocation, "");
			assertNotNull(result);
			this.GR = result;
			BODTO<ItemData> idat;
			BODTO<Lot> lot;
			LOSAdvice adv;
			BODTO<LOSAdvice> advice;
			// position 1
			try{
				idat = bean.getAllowedItemData(
						ManageInventoryFacadeBeanTest.TEST_ITEM, cdto, null).get(0);
				lot = bean.getAllowedLots("", cdto, idat).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv
						.getVersion(), adv.toUniqueString());
				bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, adv.getNotifiedAmount(), advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.FINISHED);
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			// position 2
			try{
				lot = bean.getAllowedLots(InventoryTestTopologyRemote.LOT_N1_A1_NAME, cdto, null)
						.get(0);
				idat = bean.getAllowedItemData("", cdto, lot).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv.getVersion(), adv
						.toUniqueString());
				bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, adv.getNotifiedAmount().subtract(new BigDecimal(200)), advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.GOODS_TO_COME);
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			// position 3
			try{
				lot = bean.getAllowedLots(ManageInventoryFacadeBeanTest.TEST_LOT_X,
						cdto, null).get(0);
				idat = bean.getAllowedItemData("", cdto, lot).get(0);
				adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
				advice = new BODTO<LOSAdvice>(adv.getId(), adv.getVersion(), adv
						.toUniqueString());
				LOSGoodsReceiptPosition pos3 = bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, adv.getNotifiedAmount().subtract(new BigDecimal(1000)) , 
						advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.GOODS_TO_COME);
				assertTrue(pos3.getStockUnit().getItemData().getId().equals(
						idat.getId()));
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			// position 4
			lot = bean.getAllowedLots(ManageInventoryFacadeBeanTest.TEST_LOT_X,
					cdto, null).get(0);
			idat = bean.getAllowedItemData("", cdto, lot).get(0);
			adv = bean.getAllowedAdvices("", cdto, lot, idat).get(0);
			advice = new BODTO<LOSAdvice>(adv.getId(), adv.getVersion(), adv
					.toUniqueString());
			try{
				LOSGoodsReceiptPosition pos4 = bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, new BigDecimal(2000), advice);
				adv = advQuery.queryById(adv.getId());
				assertEquals(adv.getAdviceState(), LOSAdviceState.OVERLOAD);
				assertTrue(pos4.getStockUnit().getItemData().getId().equals(
						idat.getId()));
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			//quality fault
			try{
				bean.createGoodsReceiptPosition(
						cdto, GR, lot, idat, null, defTypeTO, new BigDecimal(77), advice,LOSGoodsReceiptType.INTAKE,  StockUnitLockState.QUALITY_FAULT.getLock(), "QM Fehler Allgemein zum Testen");
				
			} catch (ReportException ex){
				if (ex.getReportExceptionKey().equals(ReportExceptionKey.PRINT_FAILED)){
					logger.warn("" + ex.getMessage());
				}
			}
			
			bean.acceptGoodsReceipt(GR);

		} catch (FacadeException ex) {
			logger.error(ex, ex);
			fail(ex.getMessage());
		}

	}

	

}
