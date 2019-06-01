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

package de.linogistix.los.location.entityservice;

import javax.ejb.Local;

import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.wms2.mywms.location.LocationCluster;

/**
 *
 * @author krane
 */
@Local
public interface LOSLocationClusterService 
        extends BasicService<LocationCluster>
{

    public LocationCluster createLocationCluster(String name); 
    
    public LocationCluster getByName(String name) 
    	throws EntityNotFoundException;
    
    public LocationCluster getDefault();

}
