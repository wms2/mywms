/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import junit.framework.TestCase;
import de.linogistix.los.location.model.LOSStorageLocation;

public class LOSRackLocationComparatorByNameTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCompare(){
		LOSRackLocationComparatorByName c ;
		
		c = new LOSRackLocationComparatorByName();
		
		LOSStorageLocation r1 = new LOSStorageLocation();
		LOSStorageLocation r2 = new LOSStorageLocation();
		
		r1.setName("R1-2-1-1");
		r2.setName("R1-10-1-1");
		
		assertTrue(c.compare(r1, r2) < 0);
		assertTrue(c.compare(r2,r1) > 0);
		assertTrue(c.compare(r2,r2) == 0);
		
		
		r1.setName("NOT-THE-RIGHT-FORMAT");
		r2.setName("NOT-THE-RIGHT-FORMAT-BUT-LONGER");
		
		assertTrue(c.compare(r1, r2) < 0);

		
		
	}

}
