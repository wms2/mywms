package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.ItemData;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

@Local
public interface QueryFixedAssignmentService  {
	
	public LOSFixedLocationAssignment getByLocation(LOSStorageLocation sl);
	
	public boolean existsByItemData(ItemData item);
	
	public boolean existsByLocation(LOSStorageLocation location);

	public List<LOSFixedLocationAssignment> getByItemData(ItemData item);
}
