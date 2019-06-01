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

package de.linogistix.los.location.query;


import javax.ejb.Remote;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.location.LocationCluster;

/**
 *
 * @author krane
 */
@Remote
public interface LOSLocationClusterQueryRemote extends BusinessObjectQueryRemote<LocationCluster>{ 
  
}
