/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.query.BusinessObjectQueryRemote;

@Remote
public interface LOSUnitLoadAdvicePositionQueryRemote extends
		BusinessObjectQueryRemote<LOSUnitLoadAdvicePosition> {

}
