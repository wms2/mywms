/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.location.exception.LOSLocationException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.StorageStrategy;

/**
 * Business Services for the Storage process (move goods/unitloads to the warehouse).
 *  
 * @author trautm
 */
@Local
public interface StorageBusiness {

    /**
     * Gets or Creates a LOSStorageRequest (Movement of a UnitLoad to a StorageLocation).
     * The returned LOSStorageRequest might not contain a destination yet if in state RAW.
     * 
	 * @param client
	 * @param unitLoad
	 * @param location, if null a location will be searched
	 * @param strategy, if null the default will be used
     *  
     * @return
     */
    LOSStorageRequest getOrCreateStorageRequest(Client c,UnitLoad ul) throws FacadeException;
    LOSStorageRequest getOrCreateStorageRequest(Client c,UnitLoad ul, boolean startProcessing) throws FacadeException;
	public LOSStorageRequest getOrCreateStorageRequest(Client c, UnitLoad ul, boolean startProcessing, StorageLocation location, StorageStrategy strategy) throws FacadeException;

    /**
     * Gets an open LOSStorageRequest (Movement of a UnitLoad to a StorageLocation).
     * 
     * @param ul
     *  
     * @return
     */
    LOSStorageRequest getOpenStorageRequest( String unitLoadLabel ) throws FacadeException;

    /**
     * Finish LOSStorageRequest by transferring all StockUnits to another UnitLoad in the warehouse (deutsch: Zuschuetten)
     *
     * @param req
     * @param destination
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws LOSLocationException 
     * @throws FacadeException 
     */
    public void finishStorageRequest(LOSStorageRequest req, UnitLoad destination) throws FacadeException, LOSLocationException, FacadeException;

    /**
     * Finish LOSStorageRequest by transferring the UnitLoad to the given StorageLocation.
     *
     * @param req
     * @param sl
     * @param force
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws de.linogistix.los.location.exception.LOSLocationException
     */
    public void finishStorageRequest(LOSStorageRequest req, StorageLocation sl, boolean force) throws FacadeException, LOSLocationException;
   

    /**
     * Determines to which client a UnitLoad belongs. If the UnitLoad is assigned to another
     * CLient than SystemClient it is this one. If not, but all StockUnits on the UnitLoad
     * belong to one client it is this client. If not all StockUnits belong to one client and
     * the UnitLoad is not assigned to a client different than SystemClient, it is the SystemClient.
     * 
     * @param ul
     * @return
     */
    public Client determineClient(UnitLoad ul);
    
    
	/**
	 * Cancellation of a storage request.
	 * The reservation of the designated location is released.
	 * 
	 * @param req
	 * @throws FacadeException
	 */
	public void cancelStorageRequest(LOSStorageRequest req) throws FacadeException;
	
	/**
	 * Delete a storage request.
	 * The reservation of the designated location is released.
	 * 
	 * @param req
	 * @throws FacadeException
	 */
	public void removeStorageRequest(LOSStorageRequest req) throws FacadeException;

}

