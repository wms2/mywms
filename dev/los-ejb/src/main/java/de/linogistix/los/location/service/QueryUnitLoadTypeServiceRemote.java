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

import org.mywms.model.UnitLoadType;

import de.linogistix.los.common.exception.UnAuthorizedException;

@Remote
public interface QueryUnitLoadTypeServiceRemote {

	/**
     * Returns the default {@link UnitLoadType} for the warehouse.
     * @return
     */
    public UnitLoadType getDefaultUnitLoadType();

    /**
     * Returns a unitLoadType whose UnitLoads are fixed to a StorageLocation. They cannot be moved and therefore 
     *  can be interpreted as dummy Unitloads.
     * @return
     */
	public UnitLoadType getPickLocationUnitLoadType();

	/**
	 * Get all {@link UnitLoadType}s.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link UnitLoadType}s<br>
	 * - callers of a certain client will get only those {@link UnitLoadType}s that are also assigned to that client.
	 * 
	 * @return {@link List} of {@link UnitLoadType}s
	 */
	public List<UnitLoadType> getList();
	
	/**
	 * Get all {@link UnitLoadType}s sorted as specified.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link UnitLoadType}s<br>
	 * - callers of a certain client will get only those {@link UnitLoadType}s that are also assigned to that client.
	 * 
	 * @return {@link List} of {@link UnitLoadType}s
	 */
	public List<UnitLoadType> getSortedList(boolean orderByName, 
											boolean orderByHeight,
											boolean orderByWidth,
    										boolean orderByDepth,
											boolean orderByWeight);

	/**
	 * Search for an {@link UnitLoadType} with specified name.
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the same client as the unit load type is assigned to or <br>
	 * - belong to the system client.
	 * 
	 * @param name the name to search for
	 * @return matching unit load type or NULL if there is none.
	 * @throws UnAuthorizedException
	 */
	public UnitLoadType getByName(String name) throws UnAuthorizedException;
}
