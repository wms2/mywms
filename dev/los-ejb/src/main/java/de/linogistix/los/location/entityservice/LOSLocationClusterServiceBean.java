/*
 * LOSLocationCluster
 *
 * Created on 2009
 *
 * Copyright (c) 2009 - 2012 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.LocationClusterEntityService;

/**
 * @see de.linogistix.los.location.entityservice.LOSLocationClusterService
 * 
 * @author krane
 */
@Stateless
public class LOSLocationClusterServiceBean extends BasicServiceBean<LocationCluster>
		implements LOSLocationClusterService, LOSLocationClusterServiceRemote {
	@Inject
	private LocationClusterEntityService locationClusterEntityService;

	public LocationCluster getDefault() {
		return locationClusterEntityService.getDefault();
	}
}
