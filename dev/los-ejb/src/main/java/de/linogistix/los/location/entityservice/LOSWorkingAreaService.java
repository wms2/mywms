/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;


import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.location.model.LOSWorkingArea;
import de.wms2.mywms.location.StorageLocation;



/**
 * @author krane
 *
 */
@Local
public interface LOSWorkingAreaService extends BasicService<LOSWorkingArea> {
	
	public LOSWorkingArea getByName(String name);    
	public boolean containsLocation(LOSWorkingArea area, StorageLocation location);    
    
}