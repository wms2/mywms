/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;

/**
 * User exits of storage processing.
 * 
 * @author krane
 *
 */
@Local
public interface ManageStorageService {
	
	/**
	 * User exit. Is called when the state of a storage request changes.<br>
	 * 
	 * @param storageRequest
	 * @param stateOld
	 * @throws FacadeException
	 */
	public void onStorageRequestStateChange(LOSStorageRequest storageRequest, LOSStorageRequestState stateOld) throws FacadeException;
	
	/**
	 * User exit. Is called when a storage request is processed partially.<br>
	 * 
	 * @param storageRequest
	 * @throws FacadeException
	 */
	public void onStorageRequestPartialProcessed(LOSStorageRequest storageRequest) throws FacadeException;

}
