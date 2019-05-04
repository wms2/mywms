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
import de.linogistix.los.location.model.LOSLocationCluster;


/**
 * @author krane
 *
 */
@Stateless
public class LOSLocationClusterCRUDBean extends BusinessObjectCRUDBean<LOSLocationCluster> implements LOSLocationClusterCRUDRemote {

	@EJB 
	LOSLocationClusterService service;
	
	@Override
	protected BasicService<LOSLocationCluster> getBasicService() {
		
		return service;
	}
}
