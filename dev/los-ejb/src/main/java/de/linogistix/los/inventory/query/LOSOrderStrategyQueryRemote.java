/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */package de.linogistix.los.inventory.query;


import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.query.BusinessObjectQueryRemote;

/** 
*
* @author krane
*/
@Remote
public interface LOSOrderStrategyQueryRemote extends BusinessObjectQueryRemote<LOSOrderStrategy>{ 
  
	public List<String> getNametList();

}
