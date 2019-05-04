/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Local;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface QueryUnitLoadService {

	/**
	 * Search for an {@link LOSUnitLoad} with specified label.
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the same client as the unit load is assigned to or <br>
	 * - belong to the system client.
	 * 
	 * @param label the label to search for
	 * @return matching unit load or NULL if there is none.
	 * @throws UnAuthorizedException
	 */
	public LOSUnitLoad getByLabelId(String label) throws UnAuthorizedException;
	
	/**
	 * In case of lazily loading you have to fetch unit loads extra.
	 *  For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link LOSUnitLoad}s for the specified location<br>
	 * - callers of a certain client will get only those {@link LOSUnitLoad}s that are also assigned to that client.
	 * 
	 * @param sl
	 * @return
	 */
	public List<LOSUnitLoad> getListByLocation(LOSStorageLocation sl);
}
