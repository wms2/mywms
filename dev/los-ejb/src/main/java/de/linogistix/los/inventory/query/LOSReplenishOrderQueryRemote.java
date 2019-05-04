/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.query.BusinessObjectQueryRemote;

/** 
*
* @author krane
*/
@Remote
public interface LOSReplenishOrderQueryRemote extends BusinessObjectQueryRemote<LOSReplenishOrder>{ 
  
}
