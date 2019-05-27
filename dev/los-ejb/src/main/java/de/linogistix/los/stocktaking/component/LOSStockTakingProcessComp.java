/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.component;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

@Local
public interface LOSStockTakingProcessComp {
	
	public void acceptOrder( long orderId ) throws LOSStockTakingException,  LOSLocationException, FacadeException;
	
	public void recountOrder( long orderId ) throws LOSStockTakingException;
	
	public int generateOrders(
			boolean execute, 
			Long clientId, Long areaId, Long zoneId,Long locationId, String locationName, Long itemId, String itemNo, 
			Date invDate, 
			boolean enableEmptyLocations, boolean enableFullLocations );

	public int generateOrders(
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long locationId, String locationName, Long itemId, String itemNo, 
			Date invDate,
			boolean enableEmptyLocations, boolean enableFullLocations,
			boolean clientModeLocations, boolean clientModeItemData);

	public void processLocationStart(StorageLocation sl) throws LOSStockTakingException, UnAuthorizedException;
	
	public void processLocationEmpty(StorageLocation sl) throws UnAuthorizedException, LOSStockTakingException;

	public void processLocationCancel(StorageLocation sl) throws LOSStockTakingException, UnAuthorizedException;
	
	public void processLocationFinish(StorageLocation sl) throws UnAuthorizedException, LOSStockTakingException;

	public void processUnitloadStart(String location, String unitLoadLabel) throws LOSStockTakingException, UnAuthorizedException;

	public void processUnitloadMissing(UnitLoad ul) throws UnAuthorizedException, LOSStockTakingException;
	
	public void processStockCount(StockUnit su, BigDecimal counted) throws LOSStockTakingException, UnAuthorizedException;
	
	public void processStockCount(String clientNo, 
									String location, 
									String unitLoad, 
									String itemNo, 
									String lotNo, 
									String serialNo, 
									BigDecimal counted, 
									String ulType) throws LOSStockTakingException, UnAuthorizedException;
	
	public void removeOrder( Long orderId ) throws LOSStockTakingException;


}

