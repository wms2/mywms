/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;


import javax.ejb.Remote;

import de.wms2.mywms.location.Area;


/**
 * @author krane
 *
 */
@Remote
public interface LOSAreaServiceRemote {
    public Area getDefault();
}