/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.ItemData;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.service.QueryFixedAssignmentService;

@Stateless
public class LOSFixedLocationAssignmentServiceBean 
				extends BasicServiceBean<LOSFixedLocationAssignment>
				implements LOSFixedLocationAssignmentService 
{
	@EJB
	QueryFixedAssignmentService fixService;

	public boolean existsFixedLocationAssignment(ItemData item) {
		return fixService.existsByItemData(item);
	}

	public List<LOSFixedLocationAssignment> getByItemData(ItemData item) {
		return fixService.getByItemData(item);
	}

	public LOSFixedLocationAssignment getByLocation(LOSStorageLocation sl) {
		return fixService.getByLocation(sl);
	}
}
