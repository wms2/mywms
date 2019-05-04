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

import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSStorageLocationService 
        extends BasicService<LOSStorageLocation>
{

    public LOSStorageLocation createStorageLocation(Client client, String name, LOSStorageLocationType type);
    
    public LOSStorageLocation createStorageLocation(Client client, String name, LOSStorageLocationType type, LOSArea area);
    
    /**
     * Searches for a LOSStorageLocation which has the specified name. 
     * 
     * @param name specifies the LOSStorageLocation
     * @return a LOSStorageLocation, null if nothing was found
     */
    public LOSStorageLocation getByName(String name);

    public List<LOSStorageLocation> getListByArea(Client client, LOSArea area);
    
    public List<LOSStorageLocation> getListByRack(LOSRack rack);
    
	public LOSStorageLocation getNirwana();

	public LOSStorageLocation getClearing();
    
	public LOSStorageLocation getCurrentUsersLocation();
}
