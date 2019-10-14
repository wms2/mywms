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

import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

@Remote
public interface QueryStockServiceRemote {

	/**
	 * In case of lazily loading you have to fetch stocks extra.
	 *  For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link StockUnit}s for the specified {@link UnitLoad}<br>
	 * - callers of a certain client will get only those {@link StockUnit}s that are also assigned to that client.
	 * 
	 * @param ul {@link UnitLoad} the stocks are placed on
	 * @return {@link List} of {@link StockUnit}s
	 */
	public List<StockUnit> getListByUnitLoad(UnitLoad ul);

	/**
	 * Search for all stocks of specified location.
	 * For security reasons result will be limited according to the callers client.
	 * 
	 * @param loc
	 * @return
	 */
	public List<StockUnit> getListByStorageLocation(StorageLocation loc);

}
