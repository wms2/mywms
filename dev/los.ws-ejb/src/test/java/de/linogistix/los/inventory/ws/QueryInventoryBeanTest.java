/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.ws.jaxwsgen.InventoryException_Exception;
import de.linogistix.los.inventory.ws.jaxwsgen.InventoryTO;
import de.linogistix.los.inventory.ws.jaxwsgen.InventoryTOArray;
import de.linogistix.los.inventory.ws.jaxwsgen.QueryInventory;
import de.linogistix.los.inventory.ws.jaxwsgen.QueryInventoryBeanService;
import junit.framework.TestCase;

public class QueryInventoryBeanTest extends TestCase {

	String ITEM_A1_NUMBER = "Test Item A1";
	String ITEM_A2_NUMBER = "Test Item A2";
	String LOT_N1_A1_NAME = "Test LOT N1-A1";
	
	
	private static final Logger logger = Logger.getLogger(QueryInventoryBeanTest.class);
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetInventoryList(){
		
		QueryInventory proxy;
		try {
			
			QueryInventoryBeanService service = new QueryInventoryBeanService();
		    proxy = service.getQueryInventoryBeanPort();
			if (proxy == null){
				throw new NullPointerException();
			}
			
			BindingProvider bp = (BindingProvider)proxy;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "System Admin");
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "myWMS");

			InventoryTOArray ret;
			ret = proxy.getInventoryList("Test Client", false);
			for (InventoryTO to : ret.getItem()){
				logger.info(to.toString());
			}
			
			ret = proxy.getInventoryList("Test Client", true);
			
			for (InventoryTO to : ret.getItem()){
				logger.info(to.toString());
			}
				
			ret = proxy.getInventoryByArticle("Test Client", ITEM_A1_NUMBER,true);
			assertEquals(1, ret.getItem().size());
			
			try{
				ret = proxy.getInventoryByArticle("Test Client", "DOESN'T EXIST",true);
				fail("DOESN'T EXIST");
			} catch (InventoryException_Exception ex){
				// OK
			}
			
			InventoryTO to = proxy.getInventoryByLot("Test Client", ITEM_A1_NUMBER, LOT_N1_A1_NAME);
			logger.info(to.toString());
			
			try{
				to = proxy.getInventoryByLot("Test Client", ITEM_A2_NUMBER, LOT_N1_A1_NAME);
				fail("LOT MISMATCH");
			} catch (InventoryException_Exception ex){
				// OK
			}
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
		
	}
	
	public void testGetInventoryByLot(){
		
		QueryInventory proxy;
		try {
			
			QueryInventoryBeanService service = new QueryInventoryBeanService();
		    proxy = service.getQueryInventoryBeanPort();
			if (proxy == null){
				throw new NullPointerException();
			}
			
			BindingProvider bp = (BindingProvider)proxy;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "System Admin");
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "myWMS");

			
			InventoryTO to = proxy.getInventoryByLot("Test Client", ITEM_A1_NUMBER, LOT_N1_A1_NAME);
			logger.info(to.toString());
			
			try{
				to = proxy.getInventoryByLot("Test Client", ITEM_A2_NUMBER, LOT_N1_A1_NAME);
				fail("LOT MISMATCH");
			} catch (InventoryException_Exception ex){
				// OK
			}
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
		
	}
	
	public void testGetInventoryByArticle(){
		
		QueryInventory proxy;
		try {
			
			QueryInventoryBeanService service = new QueryInventoryBeanService();
		    proxy = service.getQueryInventoryBeanPort();
			if (proxy == null){
				throw new NullPointerException();
			}
			
			BindingProvider bp = (BindingProvider)proxy;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "System Admin");
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "myWMS");

			InventoryTOArray ret;
				
			ret = proxy.getInventoryByArticle("Test Client", ITEM_A1_NUMBER,true);
			assertEquals(1, ret.getItem().size());
			
			try{
				ret = proxy.getInventoryByArticle("Test Client", "DOESN'T EXIST",true);
				fail("DOESN'T EXIST");
			} catch (InventoryException_Exception ex){
				// OK
			}
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
		
	}

}
