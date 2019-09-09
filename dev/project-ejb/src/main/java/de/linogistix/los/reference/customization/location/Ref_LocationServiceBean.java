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
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.customization.CustomLocationServiceBean;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.transport.TransportOrderEntityService;

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
	@Inject
	private TransportOrderEntityService transportOrderService;
	
	@Override
	public void onLocationGetsEmpty(StorageLocation location) throws FacadeException {
		String logStr = "onLocationGetsEmpty ";
		// Check existing storage orders
		List<TransportOrder> storageRequestList = transportOrderService.readOpen(location);
		if( storageRequestList.size() == 0 ) {
			location.setAllocation(BigDecimal.ZERO);
			location.setCurrentTypeCapacityConstraint(null);
		}
		else {
			log.warn(logStr+"Try to deallocate reserved location. restore reservation.");
			for( TransportOrder storageRequest : storageRequestList ) {
				UnitLoad unitLoad = storageRequest.getUnitLoad();
				if( unitLoad != null ) {
					TypeCapacityConstraint constraint = capacityService.getByTypes(location.getLocationType(), unitLoad.getUnitLoadType());
					if( location.getCurrentTypeCapacityConstraint() == null ) {
						location.setCurrentTypeCapacityConstraint(constraint);
					}
					location.setAllocation( location.getAllocation().add(constraint.getAllocation()));
				}
			}
		}
	}
	
}
