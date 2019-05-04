/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import javax.ejb.Local;

import org.mywms.model.ItemData;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;

@Local
public interface LOSFixedLocationAssignmentComp {

	public void createFixedLocationAssignment(LOSStorageLocation sl, ItemData item)
					throws LOSLocationException;
	
}
