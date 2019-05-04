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

import de.linogistix.los.location.model.LOSLocationCluster;

/**
 *
 * @author krane
 */
@Local
public interface LOSLocationClusterService 
        extends BasicService<LOSLocationCluster>
{

    public LOSLocationCluster createLocationCluster(String name); 
    
    public LOSLocationCluster getByName(String name) 
    	throws EntityNotFoundException;
    
    public LOSLocationCluster getDefault();

}
