/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.test.TestUtilities;

public class InventoryProcessFacadeBeanTest extends TestCase {

	InventoryProcessFacade bean;
	
	private static final Logger logger = Logger.getLogger(InventoryProcessFacadeBeanTest.class);
	protected void setUp() throws Exception {
		super.setUp();
		bean = TestUtilities.beanLocator.getStateless(InventoryProcessFacade.class);
	}

	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDoInventoryOnFixed(){
			
		String sl = "T1-1-1-1";
		String ulName = "T1-1-1-1";
		String itemData = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
		String lot = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
		int amount = 1000;
		boolean fix = true;
		
		try {
			bean.doInventoyForStorageLocation(CommonTestTopologyRemote.TESTCLIENT_NUMBER,sl, ulName, null,null,itemData, lot, new BigDecimal(amount), fix, true, true);
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			fail(e.getMessage());
		}
		
		sl = "T1-1-1-1";
		ulName = "T1-1-1-1";
		itemData = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
		lot = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
		amount = 11111;
		fix = true;
		
		try {
			bean.doInventoyForStorageLocation(CommonTestTopologyRemote.TESTCLIENT_NUMBER,sl, ulName,null,ulName,itemData, lot, new BigDecimal(amount), fix, true, true);
		} catch (FacadeException e) {
			logger.error(e.getMessage(),e);
			fail(e.getMessage());
		}
	}
	
	
	
	public void testDoInventoryOnRackLocation(){
		String sl = "T1-1-4-1";
		String ulName = "T1-1-4-1";
		String itemData = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
		String lot = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
		int amount = 200;
		boolean fix = true;
		
		try {
			bean.doInventoyForStorageLocation(CommonTestTopologyRemote.TESTCLIENT_NUMBER,sl, ulName,null,ulName,itemData, lot, new BigDecimal(amount), fix, true, true);
		} catch (FacadeException e) {
			logger.error(e.getMessage(),e);
			fail(e.getMessage());
		}
		
		
		sl = "T1-1-4-1";
		ulName = "T1-1-4-1-Expectedfull";
		itemData = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
		lot = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
		amount = 200;
		fix = true;
		
		try {
			bean.doInventoyForStorageLocation(CommonTestTopologyRemote.TESTCLIENT_NUMBER,sl, ulName,null,ulName,itemData, lot, new BigDecimal(amount), fix, true, false);
		} catch (LOSLocationException ex){
			if (ex.getLocationExceptionKey().equals(LOSLocationExceptionKey.STORAGELOCATION_ALLREADY_FULL)){
				
				try{
					bean.doInventoyForStorageLocation(CommonTestTopologyRemote.TESTCLIENT_NUMBER,sl, ulName,null,ulName,itemData, lot, new BigDecimal(amount), fix, true, true);
				} catch (FacadeException ex2){
					logger.error(ex2.getMessage(),ex2);
					fail(ex2.getMessage());
				}
			} else{
				logger.error(ex.getMessage(),ex);
				fail(ex.getMessage());
			}
		} catch (FacadeException e) {
			logger.error(e.getMessage(),e);
			fail(e.getMessage());
		}
		
		sl = "T1-1-4-1";
		ulName = "T1-1-4-1-Expectedfull";
		itemData = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
		lot = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
		amount = 200;
		fix = true;
		
		try {
			bean.doInventoyForStorageLocationFromScratch(CommonTestTopologyRemote.TESTCLIENT_NUMBER,sl, ulName,null,ulName,itemData, lot, new BigDecimal(amount));
		} catch (FacadeException e) {
			logger.error(e.getMessage(),e);
			fail(e.getMessage());
		}
		
		LOSUnitLoadQueryRemote ulQuery = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
		
		try{
			LOSUnitLoad ul = ulQuery.queryByIdentity("T1-1-4-1-Expectedfull");
			assertEquals(1, ul.getStockUnitList().size());
		} catch (FacadeException e) {
			logger.error(e.getMessage(),e);
			fail(e.getMessage());
		}
		
	}

}
