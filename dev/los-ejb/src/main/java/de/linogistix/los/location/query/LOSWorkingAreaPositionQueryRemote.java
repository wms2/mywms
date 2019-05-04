/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query;


import javax.ejb.Remote;

import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import de.linogistix.los.query.BusinessObjectQueryRemote;


/**
 * @author krane
 *
 */
@Remote
public interface LOSWorkingAreaPositionQueryRemote extends BusinessObjectQueryRemote<LOSWorkingAreaPosition>{ 

}
