/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.query.BODTO;

/**
 *
 * @author trautm
 */
@Remote
public interface StorageFacade {
    /**
     * Gets (or create) a  {@link LOSStorageRequest}
     * 
     * @param labelId the label of the stockUnit/UnitLoad
     * 
     * @return a  {@link LOSStorageRequest}
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws org.mywms.service.EntityNotFoundException
     */
    LOSStorageRequest getStorageRequest(String labelId, boolean startRequest) throws FacadeException;
    
    /**
     * finish the process for the given {@link LOSStorageRequest}. I.e. the 
     * operator has put the StockUnit/UnitLoad on the StorageLocation.
     * 
     * @param srcLabel the labeldID of the StockUnit/UnitLoad
     * @param destination the label of a UnitLoad or name of a StorageLocation
     * @param addToExisting if destination is a UnitLoad this must be set to true for adding the StockUnit to the UnitLaod (zusch�tten)
     * @param overwrite true: take destination even if destination is not the assigned destination (as in LOSStorageRequest) 
     * @throws de.linogistix.los.inventory.exception.InventoryException 
     * @throws FacadeException 
     */
    void finishStorageRequest(String srcLabel, String destination, boolean addToExisting, boolean overwrite) throws InventoryException, LOSLocationException, FacadeException;
    
    /**
     * The storage request of the given unit load will be canceled.
     * 
     * @param unitLoadLabel
     * @throws FacadeException
     */
    public void cancelStorageRequest(String unitLoadLabel) throws FacadeException;
    
    /**
     * The storage request of the given unit load will be canceled.
     * 
     * @param unitLoadLabel
     * @throws FacadeException
     */
    public void cancelStorageRequest(BODTO<LOSStorageRequest> req) throws FacadeException;
    
    /**
     * The storage request of the given unit load will be canceled.
     * 
     * @param storageRequest BODTO<LOSStorageRequest>
     * @throws FacadeException
     */
    public void removeStorageRequest(BODTO<LOSStorageRequest> r) throws FacadeException;

}
