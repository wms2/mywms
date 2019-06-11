/*
/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.query;


import javax.ejb.Remote;

import de.wms2.mywms.property.SystemProperty;


/**
 * @author krane
 *
 */
@Remote
public interface LOSSystemPropertyQueryRemote extends BusinessObjectQueryRemote<SystemProperty>{ 
}
