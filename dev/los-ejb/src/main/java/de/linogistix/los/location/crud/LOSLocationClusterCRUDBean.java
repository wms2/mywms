/*
 * LOSLocationCluster
 *
 * Created on 2009
 *
 * Copyright (c) 2009 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.location.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.entityservice.LOSLocationClusterService;
import de.wms2.mywms.location.LocationCluster;


/**
 * @author krane
 *
 */
@Stateless
public class LOSLocationClusterCRUDBean extends BusinessObjectCRUDBean<LocationCluster> implements LOSLocationClusterCRUDRemote {

	@EJB 
	LOSLocationClusterService service;
	
	@Override
	protected BasicService<LocationCluster> getBasicService() {
		
		return service;
	}
}
