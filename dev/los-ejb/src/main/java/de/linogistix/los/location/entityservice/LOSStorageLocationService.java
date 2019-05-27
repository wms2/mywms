/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.StorageLocation;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSStorageLocationService 
        extends BasicService<StorageLocation>
{

    public StorageLocation createStorageLocation(Client client, String name, LocationType type);
    
    public StorageLocation createStorageLocation(Client client, String name, LocationType type, Area area);
    
    /**
     * Searches for a LOSStorageLocation which has the specified name. 
     * 
     * @param name specifies the LOSStorageLocation
     * @return a LOSStorageLocation, null if nothing was found
     */
    public StorageLocation getByName(String name);

    public List<StorageLocation> getListByArea(Client client, Area area);
    
    public List<StorageLocation> getListByRack(String rack);
    
	public StorageLocation getNirwana();

	public StorageLocation getClearing();
    
	public StorageLocation getCurrentUsersLocation();
}
