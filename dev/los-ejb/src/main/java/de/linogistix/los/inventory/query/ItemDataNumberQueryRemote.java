/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.product.ItemDataNumber;

/**
 *
 * @author krane
 */
@Remote
public interface ItemDataNumberQueryRemote extends BusinessObjectQueryRemote<ItemDataNumber>{ 
  

}
