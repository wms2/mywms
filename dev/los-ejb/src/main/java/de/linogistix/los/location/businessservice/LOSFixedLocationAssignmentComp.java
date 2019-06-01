/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import javax.ejb.Local;

import de.linogistix.los.location.exception.LOSLocationException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

@Local
public interface LOSFixedLocationAssignmentComp {

	public void createFixedLocationAssignment(StorageLocation sl, ItemData item)
					throws LOSLocationException;
	
}
