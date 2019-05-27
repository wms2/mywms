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

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.wms2.mywms.location.LocationCluster;


/**
 * CRUD operations for User entities
 * @see  BusinessObjectCRUDRemote
 * 
 * @author krane
 *
 */
@Remote
public interface LOSLocationClusterCRUDRemote extends BusinessObjectCRUDRemote<LocationCluster>{

}
