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

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.test.TestUtilities;
import de.wms2.mywms.strategy.OrderPrio;
import junit.framework.TestCase;

/**
 *
 * @author trautm
 */
public class OrderFacadeBeanTest extends TestCase {
    
    private static final Logger logger = Logger.getLogger(OrderFacadeBeanTest.class);
    
    LOSOrderFacade bean;
    
    public OrderFacadeBeanTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bean = TestUtilities.beanLocator.getStateless(LOSOrderFacade.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testOrder(){
        
         String clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
         String orderRef = "TEST 1";
         String documentUrl ="";
         String labelUrl = "";
         String destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
         
         OrderPositionTO to = new OrderPositionTO();
         to.amount = new BigDecimal(10);
         to.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
         to.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
         to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
         
         OrderPositionTO to2 = new OrderPositionTO();
         to2.amount = new BigDecimal(5);
         to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
         to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT_X;
         to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
         
         OrderPositionTO[] positions = new OrderPositionTO[]{
        		 to,
        		 to2
         };
         
         try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), OrderPrio.NORMAL, true, false, null);
			
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
			fail(e.getMessage());
		}
		
		clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        orderRef = "TEST 2";
        documentUrl ="";
        labelUrl = "";
        destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
        
        to = new OrderPositionTO();
        to.amount = new BigDecimal(99800);
        to.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
        to.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
        to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        
        to2 = new OrderPositionTO();
        to2.amount = new BigDecimal(5);
        to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
        to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT_X;
        to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
        
        positions = new OrderPositionTO[]{
       		 to,
       		 to2
        };
        
        try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), OrderPrio.NORMAL, true, false, null);
			
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
			fail(e.getMessage());
		}
    }
    
    public void testOrderMultiClient(){
        
        String clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        String orderRef = "TEST 1";
        String documentUrl ="";
        String labelUrl = "";
        String destination = LocationTestTopologyRemote.SL_WA_TESTMANDANT_NAME;
        
        OrderPositionTO to = new OrderPositionTO();
        to.amount = new BigDecimal(10);
        to.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to.clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        
        OrderPositionTO to2 = new OrderPositionTO();
        to2.amount = new BigDecimal(21);
        to2.articleRef = ManageInventoryFacadeBeanTest.TEST_ITEM;
        to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT;
        to2.clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
        
        OrderPositionTO[] positions = new OrderPositionTO[]{
       		 to,
       		 to2
        };
        
        try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), OrderPrio.NORMAL, true, false, null);
			
		} catch (FacadeException e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
			fail(e.getMessage());
		}
		
		clientRef = CommonTestTopologyRemote.TESTMANDANT_NUMBER;
       orderRef = "TEST 2";
       documentUrl ="";
       labelUrl = "";
       destination = LocationTestTopologyRemote.SL_WA_TESTMANDANT_NAME;
       
       to = new OrderPositionTO();
       to.amount = new BigDecimal(99800);
       to.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
       to.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
       to.clientRef = clientRef;
       
       to2 = new OrderPositionTO();
       to2.amount = new BigDecimal(10);
       to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
       to2.batchRef = ManageInventoryFacadeBeanTest.TEST_LOT_X;
       to2.clientRef = clientRef;
       
       positions = new OrderPositionTO[]{
      		 to,
      		 to2
       };
       
       try {
			bean.order(clientRef, orderRef, positions, documentUrl, labelUrl, destination, null, new Date(), OrderPrio.NORMAL, true, false, null);
			
		} catch (FacadeException e) {
			logger.error(e,e);
			fail(e.getMessage());
		}
   }

    
   
}
