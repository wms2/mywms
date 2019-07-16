package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Remote;

import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.FixAssignment;

@Remote
public interface QueryFixedAssignmentServiceRemote {
	public List<FixAssignment> getByItemData(ItemData item);
}
