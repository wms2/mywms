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
import org.mywms.model.StockUnit;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;

@Local
public interface LOSStockTakingProcessComp {
	
	public void acceptOrder( long orderId ) throws LOSStockTakingException,  LOSLocationException, FacadeException;
	
	public void recountOrder( long orderId ) throws LOSStockTakingException;
	
	public int generateOrders(
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo, 
			Date invDate, 
			boolean enableEmptyLocations, boolean enableFullLocations );

	public int generateOrders(
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo, 
			Date invDate,
			boolean enableEmptyLocations, boolean enableFullLocations,
			boolean clientModeLocations, boolean clientModeItemData);

	public void processLocationStart(LOSStorageLocation sl) throws LOSStockTakingException, UnAuthorizedException;
	
	public void processLocationEmpty(LOSStorageLocation sl) throws UnAuthorizedException, LOSStockTakingException;

	public void processLocationCancel(LOSStorageLocation sl) throws LOSStockTakingException, UnAuthorizedException;
	
	public void processLocationFinish(LOSStorageLocation sl) throws UnAuthorizedException, LOSStockTakingException;

	public void processUnitloadStart(String location, String unitLoadLabel) throws LOSStockTakingException, UnAuthorizedException;

	public void processUnitloadMissing(LOSUnitLoad ul) throws UnAuthorizedException, LOSStockTakingException;
	
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

