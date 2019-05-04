/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.customization.location;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.customization.CustomLocationServiceBean;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;

/**
 * @author krane
 *
 */
@Stateless
public class Ref_LocationServiceBean extends CustomLocationServiceBean implements CustomLocationService {
	private static final Logger log = Logger.getLogger(Ref_LocationServiceBean.class);

	@EJB
	private LOSStorageRequestService storageService;
	@EJB
	private QueryTypeCapacityConstraintService capacityService;
	
	@Override
	public void onLocationGetsEmpty(LOSStorageLocation location) throws FacadeException {
		String logStr = "onLocationGetsEmpty ";
		// Check existing storage orders
		List<LOSStorageRequest> storageRequestList = storageService.getActiveListByDestination(location);
		if( storageRequestList.size() == 0 ) {
			location.setAllocation(BigDecimal.ZERO);
			location.setCurrentTypeCapacityConstraint(null);
		}
		else {
			log.warn(logStr+"Try to deallocate reserved location. restore reservation.");
			for( LOSStorageRequest storageRequest : storageRequestList ) {
				LOSUnitLoad unitLoad = storageRequest.getUnitLoad();
				if( unitLoad != null ) {
					LOSTypeCapacityConstraint constraint = capacityService.getByTypes(location.getType(), unitLoad.getType());
					if( location.getCurrentTypeCapacityConstraint() == null ) {
						location.setCurrentTypeCapacityConstraint(constraint);
					}
					location.setAllocation( location.getAllocation().add(constraint.getAllocation()));
				}
			}
		}
	}
	
}
