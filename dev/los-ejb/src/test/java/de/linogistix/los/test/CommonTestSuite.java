/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.linogistix.los.user.UserCRUDBeanTest;

public class CommonTestSuite extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests Inventory Facades (from package de.linogistix.los.inventory.facade)");
		//$JUnit-BEGIN$
		suite.addTestSuite(UserCRUDBeanTest.class);
		
		//$JUnit-END$
		return suite;
	}
}


