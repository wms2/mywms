/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.facade;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

@Remote
public interface RefTopologyFacade {

	public void createBasicTopology() throws FacadeException;
	
	public void createDemoTopology() throws FacadeException;
	
	public boolean checkDemoData() throws FacadeException;
}
