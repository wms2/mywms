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

import org.mywms.model.UnitLoadType;

import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;

@Local
public interface QueryTypeCapacityConstraintServiceRemote {

	/**
	 * Search for a {@link LOSTypeCapacityConstraint} for specified types.
	 * 
	 * @param slType affected {@link LOSStorageLocationType}
	 * @param ulType affected {@link UnitLoadType}
	 * @return matching {@link LOSTypeCapacityConstraint} or NULL if there is none.
	 */
	public LOSTypeCapacityConstraint getByTypes(LOSStorageLocationType slType, UnitLoadType ulType);
	
	
	/**
	 * Search for {@link LOSTypeCapacityConstraint}s that affect the specified {@link LOSStorageLocationType}.
	 * 
	 * @param slType the {@link LOSStorageLocationType} to search for.
	 * @return {@link List} of {@link LOSTypeCapacityConstraint}s. May be empty.
	 */
	List<LOSTypeCapacityConstraint> getListByLocationType(LOSStorageLocationType slType);
}
