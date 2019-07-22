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

import de.wms2.mywms.inventory.UnitLoadType;

@Remote
public interface QueryUnitLoadTypeServiceRemote {

	/**
     * Returns the default {@link UnitLoadType} for the warehouse.
     * @return
     */
    public UnitLoadType getDefaultUnitLoadType();

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
}
