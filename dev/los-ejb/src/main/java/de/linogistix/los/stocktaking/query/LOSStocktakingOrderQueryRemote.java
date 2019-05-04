/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.stocktaking.query;


import javax.ejb.Remote;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;

/**
 *
 * @author krane
 */
@Remote
public interface LOSStocktakingOrderQueryRemote extends BusinessObjectQueryRemote<LOSStocktakingOrder>{ 
  
}
