/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.test;


import junit.framework.Test;
import junit.framework.TestSuite;
import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacadeBeanTest;
import de.linogistix.los.inventory.facade.ManageInventoryFacadeBeanTest;
import de.linogistix.los.inventory.facade.OrderFacadeBeanTest;
import de.linogistix.los.inventory.facade.StorageFacadeBeanTest;

/**
 *
 * @author trautm
 */
public class InventoryFacadeTestSuite extends TestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(InventoryFacadeTestSuite.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests Inventory Facades (from package de.linogistix.los.inventory.facade)");
		//$JUnit-BEGIN$
		suite.addTestSuite(TopologyBeanTest.class);
		suite.addTestSuite(ManageInventoryFacadeBeanTest.class);
        suite.addTestSuite(LOSGoodsReceiptFacadeBeanTest.class);
        suite.addTestSuite(StorageFacadeBeanTest.class);
        suite.addTestSuite(OrderFacadeBeanTest.class);
//        suite.addTestSuite(PickOrderFacadeBeanTest.class);
//        suite.addTestSuite(ManageExtinguishFacadeBeanTest.class);
//        suite.addTestSuite(GoodsOutFacadeBeanTest.class);
//        suite.addTestSuite(ReplenishFacadeBeanTest.class);
//        suite.addTestSuite(InventoryCockpitQueryBeanTest.class);
        //suite.addTestSuite(InventorySanityCheckFacadeBeanTest.class);
        // Clean up
//        suite.addTestSuite(InventoryFacadeCleanUpTestSuite.class);
		//$JUnit-END$
		return suite;
	}
}
