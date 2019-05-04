/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import org.mywms.model.ItemDataNumber;

import de.linogistix.los.query.BusinessObjectQueryRemote;

/**
 *
 * @author krane
 */
@Remote
public interface ItemDataNumberQueryRemote extends BusinessObjectQueryRemote<ItemDataNumber>{ 
  

}
