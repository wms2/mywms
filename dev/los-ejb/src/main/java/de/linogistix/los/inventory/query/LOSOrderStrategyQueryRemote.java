/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */package de.linogistix.los.inventory.query;


import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.strategy.OrderStrategy;

/** 
*
* @author krane
*/
@Remote
public interface LOSOrderStrategyQueryRemote extends BusinessObjectQueryRemote<OrderStrategy>{ 
  
	public List<String> getNametList();

}
