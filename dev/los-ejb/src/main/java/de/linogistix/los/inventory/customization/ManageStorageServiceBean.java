/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;


/**
 * @author krane
 *
 */
public class ManageStorageServiceBean implements ManageStorageService {
	
	public void onStorageRequestStateChange(LOSStorageRequest storageRequest, LOSStorageRequestState stateOld) throws FacadeException{
	}
	public void onStorageRequestPartialProcessed(LOSStorageRequest storageRequest) throws FacadeException {
	}

}
