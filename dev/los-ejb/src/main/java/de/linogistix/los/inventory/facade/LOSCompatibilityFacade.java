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
import org.mywms.model.Lot;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;

@Remote
public interface LOSCompatibilityFacade {
	
	@Deprecated
	void createPickRequests(List<CreatePickRequestPositionTO> chosenStocks) throws FacadeException;

	@Deprecated
	public LOSResultList<LOSOrderStockUnitTO> querySuitableStocksByOrderPosition(
			BODTO<LOSCustomerOrderPosition> orderPosTO, BODTO<Lot> lotTO,
			BODTO<LOSStorageLocation> locationTO) throws InventoryException;
	
	@Deprecated
	public String getNewPickRequestNumber();

}
