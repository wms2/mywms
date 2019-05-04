/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.example;

import de.linogistix.los.test.TestUtilities;
import junit.framework.TestCase;

/**
 *
 * @author trautm
 */
public class TopologyBeanCleanupTest extends TestCase {
 
    //-----------------------------------------------------------------------

	LocationTestTopologyRemote topology;
	
    public TopologyBeanCleanupTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	this.topology = TestUtilities.beanLocator.getStateless(LocationTestTopologyRemote.class);
    }

    public void testClearTopology() throws Exception {
        topology.clear();
    }
    
}
