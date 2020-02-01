/*
 * Copyright (c) 2012 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.location.StorageLocation;

@Remote
public interface LOSCompatibilityFacade {
	
	@Deprecated
	void createPickRequests(List<CreatePickRequestPositionTO> chosenStocks) throws FacadeException;

	@Deprecated
	public LOSResultList<LOSOrderStockUnitTO> querySuitableStocksByOrderPosition(
			BODTO<DeliveryOrderLine> orderPosTO, String lotNumber,
			BODTO<StorageLocation> locationTO) throws InventoryException;
	
	@Deprecated
	public String getNewPickRequestNumber();

}
