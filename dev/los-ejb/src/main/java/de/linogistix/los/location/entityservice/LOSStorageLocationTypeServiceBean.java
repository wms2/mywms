/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.LocationTypeEntityService;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationTypeServiceBean 
        extends BasicServiceBean<LocationType>
        implements LOSStorageLocationTypeService, LOSStorageLocationTypeServiceRemote {
	@Inject
	private LocationTypeEntityService locationTypeEntityService;
	
	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>0</code> as default.
	 */
	public LocationType getDefaultStorageLocationType() {
		return locationTypeEntityService.getDefault();
	}
}
