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

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

@Local
public interface LOSStorage {
	
	public void transferUnitLoad(String userName, StorageLocation dest, UnitLoad ul) throws FacadeException;
	public void transferUnitLoad(String userName, StorageLocation dest, UnitLoad ul, int index, boolean ignoreLock, String info, String activityCode) throws FacadeException; 
	public void transferUnitLoad(String userName, StorageLocation dest, UnitLoad ul, int index, boolean ignoreLock, boolean reserve, String info, String activityCode) throws FacadeException;
	
	public void transferToCarrier(String userName, UnitLoad source, UnitLoad destination, String info, String activityCode) throws FacadeException; 
	
	public void sendToNirwana(String username, UnitLoad u) throws FacadeException;

	public void sendToClearing(String username, UnitLoad existing) throws FacadeException;
     
}
