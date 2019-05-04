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

import de.linogistix.los.location.model.LOSLocationCluster;
import de.linogistix.los.query.BusinessObjectQueryRemote;

/**
 *
 * @author krane
 */
@Remote
public interface LOSLocationClusterQueryRemote extends BusinessObjectQueryRemote<LOSLocationCluster>{ 
  
}
