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
import de.linogistix.los.inventory.example.TopologyBeanCleanupTest;

/**
 * 
 * @author trautm
 */
public class InventoryFacadeCleanUpTestSuite extends TestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(InventoryFacadeCleanUpTestSuite.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Tests Inventory Facades (from package de.linogistix.los.inventory.facade)");
		// $JUnit-BEGIN$
		// Clean up
		suite.addTestSuite(TopologyBeanCleanupTest.class);
		// $JUnit-END$
		return suite;
	}
}
