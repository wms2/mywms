/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoadType;

@Remote
public interface InventoryProcessFacade {
	
	public void doInventoyForStorageLocation(
			String clientNumber,
			String slName, 
			String ulName,
			UnitLoadType t,
			String suId,
			String itemNumber, 
			String lot,
			BigDecimal amount,
			boolean forceUlNotEmpty,
			boolean forceExistingUnitLoad,
			boolean forceFullStorageLocation
			)
			throws FacadeException;
	
	/**
	 * Starts with an empty UnitLoad. If Stockunits exists on this UnitLoad there are send to Nirwana first.
	 * 
	 * Sets all force parameters to true.
	 * 
	 * @param client
	 * @param slName
	 * @param ulName
	 * @param t
	 * @param suId
	 * @param itemData
	 * @param lot
	 * @param amount
	 * @throws FacadeException
	 */
	public void doInventoyForStorageLocationFromScratch(
			String client,
			String slName, 
			String ulName,
			UnitLoadType t,
			String suId,
			String itemData, 
			String lot,
			BigDecimal amount
			)
			throws FacadeException;
	
	void changeAmount(BODTO<StockUnit> su, BigDecimal amount) throws FacadeException;

}
