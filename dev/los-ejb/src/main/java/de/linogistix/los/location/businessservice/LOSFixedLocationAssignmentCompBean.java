/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.mywms.model.ItemData;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.service.QueryFixedAssignmentService;

@Stateless
public class LOSFixedLocationAssignmentCompBean implements
		LOSFixedLocationAssignmentComp {

	@EJB
	private QueryFixedAssignmentService flaService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	public void createFixedLocationAssignment(LOSStorageLocation sl, ItemData item) 
					throws LOSLocationException 
	{
		LOSFixedLocationAssignment fla = flaService.getByLocation(sl); 
		
		// if the assignment already exists, nothing to do
		if(fla!=null && fla.getItemData().equals(item)){
			return;
			
		}
		// if the location is already assigned to a different item, that is a problem
		else if(fla!=null && !(fla.getItemData().equals(item))){
			throw new LOSLocationException(
					LOSLocationExceptionKey.LOCATION_ALLREADY_ASSIGNED_TO_DIFFEREND_ITEM, 
					new Object[]{sl.getName(), item.getNumber()});
		}
		
		fla = new LOSFixedLocationAssignment();
		fla.setAssignedLocation(sl);
		fla.setItemData(item);
		
		manager.persist(fla);
		
		return;
		
	}

}
