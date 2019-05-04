/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference;


import java.io.FileInputStream;
import java.util.Properties;

import org.mywms.ejb.BeanLocator;

import de.linogistix.los.reference.facade.RefTopologyFacade;
import junit.framework.TestCase;

public class CreateRefTopology extends TestCase {
 
	private BeanLocator beanLocator;

    
    public void testTopology() throws Exception {
		
    	Properties props=new Properties();
    	props.load(new FileInputStream("../../config/context.properties"));

    	Properties appServerProps=new Properties();
    	appServerProps.load(new FileInputStream("../../config/appserver.properties"));
    	
       	beanLocator = new BeanLocator("admin", "admin", props, appServerProps);
      
    	RefTopologyFacade topo = beanLocator.getStateless(RefTopologyFacade.class);
    	topo.createDemoTopology();

	}

}
