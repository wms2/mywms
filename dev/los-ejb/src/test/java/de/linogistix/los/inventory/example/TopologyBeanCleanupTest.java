/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.example;

/**
 *
 * @author trautm
 */
public class TopologyBeanCleanupTest extends TopologyBeanTest {
 
    //-----------------------------------------------------------------------

    public TopologyBeanCleanupTest(String testName) {
        super(testName);
    }

    @Override
    public void testTopology() throws Exception {
        invTopology.clear();
        locTopology.clear();
        commonTopology.clear();
    }
    
}
