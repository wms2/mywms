package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

@Remote
public interface QueryFixedAssignmentServiceRemote {
	
	public LOSFixedLocationAssignment getByLocation(StorageLocation sl);
	
	public boolean existsByItemData(ItemData item);
	
	public boolean existsByLocation(StorageLocation location);

	public List<LOSFixedLocationAssignment> getByItemData(ItemData item);
}
