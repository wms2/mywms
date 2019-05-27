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

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

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

	public LOSFixedLocationAssignment getByLocation(StorageLocation sl) {
		return fixService.getByLocation(sl);
	}
}
