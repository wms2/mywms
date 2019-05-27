/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.wms2.mywms.location.StorageLocation;

@Remote
public interface QueryStorageLocationServiceRemote {

	/**
	 * Search for an {@link StorageLocation} with specified name.
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the same client as the location is assigned to or <br>
	 * - belong to the system client.
	 * 
	 * @param name the name to search for
	 * @return matching location or NULL if there is none.
	 * @throws UnAuthorizedException
	 */
	public StorageLocation getByName(String name) throws UnAuthorizedException;
	
	/**
	 * Search for {@link StorageLocation}s that are assigned to the specified area.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all locations for the specified area type<br>
	 * - callers of a certain client will get only those locations that are also assigned to that client.
	 * 
	 * 
	 * @param areaType
	 * @return list of {@link StorageLocation}s or sub classes
	 */
	public List<StorageLocation> getListForGoodsIn();
	public List<StorageLocation> getListForGoodsOut();
	public List<StorageLocation> getListForStorage();

	
}
