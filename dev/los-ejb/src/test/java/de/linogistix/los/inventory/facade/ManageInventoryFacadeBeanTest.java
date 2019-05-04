/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.test.TestUtilities;

/**
 *
 * @author trautm
 */
public class ManageInventoryFacadeBeanTest extends TestCase {

    public ItemData iDat;
    protected ManageInventoryFacade bean;
    protected TopologyBeanTest topology;
    public static final String TEST_ITEM = "Test ItemData TEST";
    public static final String TEST_LOT = "Test Lot";
    public static final String TEST_LOT_X = "Test Lot X";

    private Logger log = Logger.getLogger(ManageInventoryFacadeBeanTest.class);
    
    public ManageInventoryFacadeBeanTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bean = TestUtilities.beanLocator.getStateless(ManageInventoryFacade.class);
        topology = new TopologyBeanTest();
        topology.initServices();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of createItemData method, of class ManageInventoryFacadeBean.
     */
    public void testManageInventory() throws Exception {
        
    	GregorianCalendar nextMonth = (GregorianCalendar)GregorianCalendar.getInstance();
    	nextMonth.add(GregorianCalendar.MONTH, 1);
    	
    	GregorianCalendar today = (GregorianCalendar)GregorianCalendar.getInstance();
    	
    	GregorianCalendar nextNextMonth = (GregorianCalendar)GregorianCalendar.getInstance();
    	nextNextMonth.add(GregorianCalendar.MONTH,2);
    	
    	try{
	        bean.createItemData(CommonTestTopologyRemote.TESTCLIENT_NUMBER, TEST_ITEM);
	        try{
	        	bean.createItemData(CommonTestTopologyRemote.TESTCLIENT_NUMBER, InventoryTestTopologyRemote.ITEM_A1_NUMBER);
	        } catch (InventoryException ex){
	        	//OK exists
	        }
	        
	        bean.createAvis(CommonTestTopologyRemote.TESTCLIENT_NUMBER,
	                TEST_ITEM, TEST_LOT, new BigDecimal(1000), today.getTime(), nextNextMonth.getTime() , today.getTime(), false);
	        
	        bean.createAvis(CommonTestTopologyRemote.TESTCLIENT_NUMBER,
	                InventoryTestTopologyRemote.ITEM_A1_NUMBER, InventoryTestTopologyRemote.LOT_N1_A1_NAME, new BigDecimal(100000), today.getTime(), nextNextMonth.getTime() , today.getTime(), false);
	        
	        bean.createAvis(CommonTestTopologyRemote.TESTCLIENT_NUMBER,
	                InventoryTestTopologyRemote.ITEM_A1_NUMBER, TEST_LOT_X, new BigDecimal(7000),today.getTime(), nextNextMonth.getTime() , today.getTime(), true);
	            
        } catch (InventoryException ex){
            InventoryExceptionKey k = ex.getInventoryExceptionKey();
            if (k.equals(InventoryExceptionKey.ITEMDATA_EXISTS)){
                //
            } else{
                throw ex;
            }
        }
    }
    
    /**
     * Test of createItemData method, of class ManageInventoryFacadeBean.
     */
    public void testManageInventoryMultiClient() throws Exception {
        
    	GregorianCalendar nextMonth = (GregorianCalendar)GregorianCalendar.getInstance();
    	nextMonth.add(GregorianCalendar.MONTH, 1);
    	
    	GregorianCalendar today = (GregorianCalendar)GregorianCalendar.getInstance();
    	
    	GregorianCalendar nextNextMonth = (GregorianCalendar)GregorianCalendar.getInstance();
    	nextNextMonth.add(GregorianCalendar.MONTH,2);
    	
    	try{
    		bean.createItemData(CommonTestTopologyRemote.TESTMANDANT_NUMBER, TEST_ITEM);
	        
    		try{
	        	bean.createItemData(CommonTestTopologyRemote.TESTMANDANT_NUMBER, TEST_ITEM);
	        	fail("Cannot create item data twice");
	        } catch (InventoryException ex){
	        	//OK exists
	        }
    		bean.createItemData(CommonTestTopologyRemote.TESTMANDANT_NUMBER,InventoryTestTopologyRemote.ITEM_A1_NUMBER);

	        
	        bean.createAvis(CommonTestTopologyRemote.TESTMANDANT_NUMBER,
	                TEST_ITEM, TEST_LOT, new BigDecimal(1000), today.getTime(), nextNextMonth.getTime() , today.getTime(), false);
	        
	        bean.createAvis(CommonTestTopologyRemote.TESTMANDANT_NUMBER,
	                InventoryTestTopologyRemote.ITEM_A1_NUMBER, InventoryTestTopologyRemote.LOT_N1_A1_NAME, new BigDecimal(100000), today.getTime(), nextNextMonth.getTime() , today.getTime(), false);
	        
	        bean.createAvis(CommonTestTopologyRemote.TESTMANDANT_NUMBER,
	                InventoryTestTopologyRemote.ITEM_A1_NUMBER, TEST_LOT_X, new BigDecimal(7000),today.getTime(), nextNextMonth.getTime() , today.getTime(), true);
            
        } catch (InventoryException ex){
            InventoryExceptionKey k = ex.getInventoryExceptionKey();
            if (k.equals(InventoryExceptionKey.ITEMDATA_EXISTS)){
                //
            } else{
                throw ex;
            }
        }
    }
    
    public void testTransferStockUnit(){
     	
   	 StockUnitQueryRemote suQuery = TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class);
   	 
   	 LOSUnitLoadQueryRemote ulQuery = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
   	 LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator.getStateless(LOSStorageLocationQueryRemote.class);
   	 
   	 try {
		bean.createStockUnitOnStorageLocation(
				 CommonTestTopologyRemote.TESTCLIENT_NUMBER, 
				 slQuery.getNirwanaName(),
				 InventoryTestTopologyRemote.ITEM_A1_NUMBER, 
				 InventoryTestTopologyRemote.LOT_N1_A1_NAME, new BigDecimal(199),
				 "TransferFromMe");

		BODTO< StockUnit> su ;
		BODTO<LOSUnitLoad> ul;
		
		ul = ulQuery.autoCompletion("TransferFromMe").get(0);
		LOSUnitLoad unitLoad = ulQuery.queryById(ul.getId());
		StockUnit stockUnit = unitLoad.getStockUnitList().get(0);
		su = new BODTO<StockUnit>(stockUnit.getId(), stockUnit.getVersion(), stockUnit.getId());
		bean.transferStockUnit(su, ul, true, true, "X");
		
		stockUnit = suQuery.queryById(stockUnit.getId());
		assertFalse(stockUnit.isLocked());
		assertTrue(stockUnit.getReservedAmount().compareTo(new BigDecimal(0)) == 0);
		
	} catch (Throwable e) {
		log.error(e.getMessage(), e);
	}
   	 
    }
   
}
