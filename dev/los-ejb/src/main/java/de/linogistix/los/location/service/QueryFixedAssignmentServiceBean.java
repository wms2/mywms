package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;

@Stateless
public class QueryFixedAssignmentServiceBean implements QueryFixedAssignmentServiceRemote {

	@Inject
	private FixAssignmentEntityService fixService;

	public List<FixAssignment> getByItemData(ItemData item) {
		return fixService.readList(item, null, null, null, null);
	}
}
