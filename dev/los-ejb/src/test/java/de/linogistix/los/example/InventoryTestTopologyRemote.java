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

/**
 * Creates a topology.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote()
public interface InventoryTestTopologyRemote {
  
	String ITEM_A1_NUMBER = "Test Item A1";
	String ITEM_A2_NUMBER = "Test Item A2";
	String LOT_N1_A1_NAME = "Test LOT N1-A1";
	String LOT_N2_A2_NAME = "Test LOT N2-A2";	
	String ITEM_KG_NAME = "kg";
	String ITEM_G_NAME = "g";
   
	void clear() throws InventoryTopologyException;

    void create() throws InventoryTopologyException;

    void remove(Class<BasicEntity> clazz) throws InventoryTopologyException;
  
}
