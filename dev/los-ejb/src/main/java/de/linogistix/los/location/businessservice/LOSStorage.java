/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LOSStorage {
	
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul) throws FacadeException;
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, int index, boolean ignoreLock, String info, String activityCode) throws FacadeException; 
	public void transferUnitLoad(String userName, LOSStorageLocation dest, LOSUnitLoad ul, int index, boolean ignoreLock, boolean reserve, String info, String activityCode) throws FacadeException;
	
	public void transferToCarrier(String userName, LOSUnitLoad source, LOSUnitLoad destination, String info, String activityCode) throws FacadeException; 
	
	public void sendToNirwana(String username, LOSUnitLoad u) throws FacadeException;

	public void sendToClearing(String username, LOSUnitLoad existing) throws FacadeException;
     
}
