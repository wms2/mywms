/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.StockUnit;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;


@Remote
public interface LOSStocktakingFacade {

	/**
	 * Accepting an order.
	 * 
	 * @param orderId
	 * @throws LOSStockTakingException
	 * @throws LOSLocationException
	 * @throws UnAuthorizedException
	 * @throws FacadeException
	 */
	public void acceptOrder( Long orderId ) throws LOSStockTakingException, LOSLocationException, UnAuthorizedException, FacadeException;

	/**
	 * Recount the order.
	 * All records of the order will be deleted.
	 *  
	 * @param orderId
	 * @throws LOSStockTakingException
	 */
	public void recountOrder( Long orderId ) throws LOSStockTakingException;

	/**
	 * Generate a list of orders.
	 * With the given parameters all matching locations are selected and for every matching
	 * location an order is generated.
	 * 
	 * @param execute
	 * @param clientId
	 * @param areaId
	 * @param rackId
	 * @param locationId
	 * @param locationName
	 * @param itemId
	 * @param itemNo
	 * @param invDate
	 * @param enableEmptyLocations
	 * @param enableFullLocations
	 * @return
	 */
	public int generateOrders( 
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo,
			Date invDate, 
			boolean enableEmptyLocations, boolean enableFullLocations );

	/**
	 * Generate a list of orders.
	 * With the given parameters all matching locations are selected and for every matching
	 * location an order is generated.
	 * 
	 * @param execute
	 * @param clientId
	 * @param areaId
	 * @param rackId
	 * @param locationId
	 * @param locationName
	 * @param itemId
	 * @param itemNo
	 * @param invDate
	 * @param enableEmptyLocations
	 * @param enableFullLocations
	 * @param clientModeLocations
	 * @param clientModeItemData
	 * @return
	 */
	public int generateOrders( 
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo,
			Date invDate, 
			boolean enableEmptyLocations, boolean enableFullLocations, boolean clientModeLocations, boolean clientModeItemData );

	/**
	 * Processing an empty counted location.
	 * The required records are generated with an amount of 0.
	 * 
	 * @param location
	 * @throws UnAuthorizedException
	 * @throws LOSStockTakingException
	 */
	public void processLocationEmpty(LOSStorageLocation location) throws UnAuthorizedException, LOSStockTakingException;

	/**
	 * Start the counting of a location.
	 * First, the location is checked. If there are open requests or locks, it will throw an exception.
	 * The location will be locked for stock-taking.
	 * 
	 * @param location
	 * @throws LOSStockTakingException
	 * @throws UnAuthorizedException
	 */
	public void processLocationStart(LOSStorageLocation location) throws LOSStockTakingException, UnAuthorizedException;
	
	/**
	 * Cancel the counting of a location.
	 * The counted records will be deleted and the order will be reseted.
	 * The lock of the location will be removed.
	 *  
	 * @param location
	 * @throws LOSStockTakingException
	 * @throws UnAuthorizedException
	 */
	public void processLocationCancel(LOSStorageLocation location) throws LOSStockTakingException, UnAuthorizedException;
	
	/**
	 * Finish the counting of a location.
	 * The counting-order and -records are marked as COUNTED.
	 * If all records are automatically finished. the order is accepted and the location is unlocked.
	 *  
	 * @param location
	 * @throws UnAuthorizedException
	 * @throws LOSStockTakingException
	 */
	public void processLocationFinish(LOSStorageLocation location) throws UnAuthorizedException, LOSStockTakingException;

	/**
	 * Start the counting of a unit-load.
	 * All previous counted records of the order and unit-load will be removed.
	 * 
	 * @param location
	 * @param unitLoadLabel
	 * @throws LOSStockTakingException
	 * @throws UnAuthorizedException
	 */
	public void processUnitloadStart(String location, String unitLoadLabel) throws LOSStockTakingException, UnAuthorizedException;

	/**
	 * Processing of a no more found unit-load.
	 * Generate empty count-records for the unit-load.
	 * 
	 * @param unitLoad
	 * @throws UnAuthorizedException
	 * @throws LOSStockTakingException
	 */
	public void processUnitloadMissing(LOSUnitLoad unitLoad) throws UnAuthorizedException, LOSStockTakingException;

	/**
	 * Processing the counting of a stock-unit.
	 * This works for setting the counted amount for an existing stock-unit.
	 * A counting-record will be generated.
	 *  
	 * @param stockUnit
	 * @param countedAmount
	 * @throws LOSStockTakingException
	 * @throws UnAuthorizedException
	 */
	public void processStockCount(StockUnit stockUnit, BigDecimal countedAmount) throws LOSStockTakingException, UnAuthorizedException;
	
	/**
	 * Processing the counting of a stock-unit.
	 * This works for setting the counted amount for a not existing stock-unit.
	 * A counting-record will be generated.
	 * 
	 * @param clientNo
	 * @param location
	 * @param unitLoad
	 * @param itemNo
	 * @param lotNo
	 * @param serialNo
	 * @param countedAmount
	 * @throws LOSStockTakingException
	 * @throws UnAuthorizedException
	 */
	public void processStockCount(String clientNo, 
									String location, 
									String unitLoad, 
									String itemNo, 
									String lotNo, 
									String serialNo, 
									BigDecimal countedAmount,
									String ulType) throws LOSStockTakingException, UnAuthorizedException;
	
	/**
	 * Generate a list of the allowed unit load types for the specified location.
	 * 
	 * @param clientNo
	 * @param location
	 * @return 
	 * @throws UnAuthorizedException 
	 * @throws LOSStockTakingException 
	 */
	public List<String> getUnitloadTypes(String clientNo, String location) throws UnAuthorizedException, LOSStockTakingException;
	

	public void removeOrder( Long orderId ) throws LOSStockTakingException;
	
	/**
	 * Get the default-client. 
	 * @return In case of only one client in system or the caller has not 
	 * the system-client, the appropriate client is returned. In the other
	 * case, a null-value will be returned.  
	 * 
	 */
	public Client getDefaultClient();

	/**
	 * read the next possible location to count.
	 * @param client
	 * @param lastLocation
	 * @return
	 */
	public String getNextLocation( Client client, String lastLocation );
}
