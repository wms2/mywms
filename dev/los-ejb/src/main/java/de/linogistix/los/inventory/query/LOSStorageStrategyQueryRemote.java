/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.query;

import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSStorageStrategy;
import de.linogistix.los.query.BusinessObjectQueryRemote;

/**
 * @author krane
 *
 */
@Remote
public interface LOSStorageStrategyQueryRemote extends BusinessObjectQueryRemote<LOSStorageStrategy>{ 

}
