/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import de.linogistix.los.query.TemplateQueryWhereToken;
import junit.framework.TestCase;

public class TemplateQueryWhereTokenTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testToParameterName() {
		TemplateQueryWhereToken t = new TemplateQueryWhereToken(
				"=","client.id","1234");
		assertEquals(t.getParameterName(), "clientid");
		
		t = new TemplateQueryWhereToken(
				"=","clientid","1234");
		assertEquals(t.getParameterName(), "clientid");
	}

}
