/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.test.TestUtilities;

public class StockUnitQueryBeanTest extends TestCase {
	
	StockUnitQueryRemote bean;
	
	private static final Logger log = Logger.getLogger(StockUnitQueryBeanTest.class);
	protected void setUp() throws Exception {
		super.setUp();
		this.bean = TestUtilities.beanLocator.getStateless(StockUnitQueryRemote.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testQueryByStorageLocation(){
		LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator.getStateless(LOSStorageLocationQueryRemote.class);
		try{
			
			LOSStorageLocation sl = slQuery.queryByIdentity("T1-1-1-1");
			BODTO<LOSStorageLocation> slTo = new BODTO<LOSStorageLocation>(sl.getId(), sl.getVersion(), sl.getName());
			bean.queryByStorageLocation(slTo, new QueryDetail(0, Integer.MAX_VALUE));
			
		} catch (Throwable t){
			log.error(t.getMessage(), t);
			fail(t.getMessage());
		}
	}

}
