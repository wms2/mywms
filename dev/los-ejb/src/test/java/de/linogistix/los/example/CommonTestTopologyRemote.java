/*
 * TopologyRemote.java
 *
 * Created on 12. September 2006, 11:36
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.example;

import javax.ejb.Remote;

import org.mywms.model.BasicEntity;

import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * Creates a topology.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote()
public interface CommonTestTopologyRemote {
  
	public static final String TESTCLIENT_NUMBER = "Test Client";
	
	public static final String TESTMANDANT_NUMBER = "Test Mandant";
	

	void clear() throws CommonTopologyException;

    void create() throws CommonTopologyException;

    void createClients() throws CommonTopologyException, BusinessObjectExistsException, BusinessObjectCreationException, BusinessObjectSecurityException;

    void remove(Class<BasicEntity> clazz) throws CommonTopologyException;
  
}
