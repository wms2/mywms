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

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

@Local
public interface QueryTypeCapacityConstraintServiceRemote {

	/**
	 * Search for a {@link TypeCapacityConstraint} for specified types.
	 * 
	 * @param slType affected {@link LocationType}
	 * @param ulType affected {@link UnitLoadType}
	 * @return matching {@link TypeCapacityConstraint} or NULL if there is none.
	 */
	public TypeCapacityConstraint getByTypes(LocationType slType, UnitLoadType ulType);
	
	
	/**
	 * Search for {@link TypeCapacityConstraint}s that affect the specified {@link LocationType}.
	 * 
	 * @param slType the {@link LocationType} to search for.
	 * @return {@link List} of {@link TypeCapacityConstraint}s. May be empty.
	 */
	List<TypeCapacityConstraint> getListByLocationType(LocationType slType);
}
