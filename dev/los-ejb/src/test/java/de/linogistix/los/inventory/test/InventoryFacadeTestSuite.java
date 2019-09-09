/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.test;


import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.facade.ManageInventoryFacadeBeanTest;
import de.linogistix.los.inventory.facade.OrderFacadeBeanTest;
import junit.framework.Test;
import junit.framework.TestSuite;

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
        suite.addTestSuite(OrderFacadeBeanTest.class);
		//$JUnit-END$
		return suite;
	}
}
