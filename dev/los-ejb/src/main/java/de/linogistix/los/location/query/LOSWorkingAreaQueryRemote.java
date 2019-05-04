/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query;


import javax.ejb.Remote;

import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.query.BusinessObjectQueryRemote;


@Remote
public interface LOSWorkingAreaQueryRemote extends BusinessObjectQueryRemote<LOSWorkingArea>{ 

}
