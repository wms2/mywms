/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

@Remote
public interface QueryStockServiceRemote {

	/**
	 * In case of lazily loading you have to fetch stocks extra.
	 *  For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link StockUnit}s for the specified {@link LOSUnitLoad}<br>
	 * - callers of a certain client will get only those {@link StockUnit}s that are also assigned to that client.
	 * 
	 * @param ul {@link LOSUnitLoad} the stocks are placed on
	 * @return {@link List} of {@link StockUnit}s
	 */
	public List<StockUnit> getListByUnitLoad(LOSUnitLoad ul);
	
	/**
	 * Reading the stock units of a unit load.<br>
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link StockUnit}s for the specified unit load<br>
	 * - callers of a certain client will get only those {@link StockUnit}s that are also assigned to that client.
	 * 
	 * @param label, the label of the unit load
	 * @return {@link List} of {@link StockUnit}s
	 */
	public List<StockUnit> getListByUnitLoadLabel( String label );
	/**
	 * Search for all stocks of specified lot. 
	 * If lot is NULL, search for stocks that are not assigned to any lot.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link StockUnit}s for the specified {@link Lot}<br>
	 * - callers of a certain client will get only those {@link StockUnit}s that are also assigned to that client.
	 * 
	 * @param lot {@link Lot} to search for.
	 * @param checkAvailable Only unlocked material with amount > 0 is searched
	 * @return {@link List} of {@link StockUnit}s
	 */
	public List<StockUnit> getListByLot(Lot lot, boolean checkAvailable);
	
	/**
	 * Search for all stocks of specified {@link ItemData}.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link StockUnit}s for the specified {@link ItemData}<br>
	 * - callers of a certain client will get only those {@link StockUnit}s that are also assigned to that client.
	 * 
	 * @param item
	 * @param checkAvailable Only unlocked material with amount > 0 is searched
	 * @return
	 */
	public List<StockUnit> getListByItemData(ItemData item, boolean checkAvailable);
	
	/**
	 * Search for all stocks of specified {@link ItemData} that are not assigned to any lot.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link StockUnit}s for the specified {@link ItemData}<br>
	 * - callers of a certain client will get only those {@link StockUnit}s that are also assigned to that client.
	 * 
	 * @param item
	 * @return
	 */
	public List<StockUnit> getListByItemDataNoLot(ItemData item);
	
	/**
	 * Search for all stocks of specified location.
	 * For security reasons result will be limited according to the callers client.
	 * 
	 * @param loc
	 * @return
	 */
	public List<StockUnit> getListByStorageLocation(LOSStorageLocation loc);

}
