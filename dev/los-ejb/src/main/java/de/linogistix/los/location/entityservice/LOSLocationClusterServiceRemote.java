/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Remote;

import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.model.LOSLocationCluster;

/**
 *
 * @author krane
 */
@Remote
public interface LOSLocationClusterServiceRemote {

   
    public LOSLocationCluster getByName(String name) throws EntityNotFoundException;
    
    public LOSLocationCluster getDefault();

}
