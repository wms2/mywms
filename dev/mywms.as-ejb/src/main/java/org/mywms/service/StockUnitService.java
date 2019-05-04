/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import org.mywms.model.BusinessException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;

/**
 * This interface declares the service for the entity
 * PluginConfiguration. For this service it is save to call the
 * <code>get(String name)</code> method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface StockUnitService
    extends BasicService<StockUnit>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Creates a new StockUnit for Itemdata itemData with a default
     * amount of 1 on UnitLoad unitLoad, assignes it to Client client
     * and adds it to persistence context.
     * 
     * @param client the owning Client of the new StockUnit.
     * @param unitLoad the UnitLoad on which the new StockUnit is
     *            placed.
     * @param itemData the ItemData the new StockUnit contains.
     * @param amount 
     * @throws NullPointerException if any of the parameters is null.
     * @throws BusinessException if unitLoad or itemData are not
     *             assigned to Client client.
     */
	public StockUnit create(Client client, UnitLoad unitLoad, ItemData itemData, BigDecimal amount);

    /**
     * Searches for StockUnits that are placed on the specified
     * UnitLoad.
     * 
     * @param unitLoad the UnitLoad the StockUnits should be placed on.
     * @return list of matching StockUnits, might be empty.
     */
    List<StockUnit> getListByUnitLoad(UnitLoad unitLoad);

    /**
     * Searches for StockUnits of the specified item data. The list is
     * sorted by the created timestamp of the StockUnit.
     * 
     * @param itemData the item data of the stock unit
     * @return a list of stock units of the specified item data
     */
    List<StockUnit> getListByItemData(ItemData itemData);

    /**
     * Searches for StockUnits of the specified item data.
     * 
     * @param itemData the item data of the stock unit
     * @return a list of stock units of the specified item data
     */
    List<StockUnit> getListByItemDataOrderByDate(ItemData itemData);

    /**
     * Searches for StockUnits of the specified item data.
     * 
     * @param itemData the item data of the stock unit
     * @return a list of stock units of the specified item data
     */
    List<StockUnit> getListByItemDataOrderByAvailableAmount(ItemData itemData);

    /**
     * Returns the available stock (the sum of amounts minus the sum of
     * reserved items of the stock units)
     * 
     * @param itemData the according item data
     * @return the available stock
     */
    int getAvailableStock(ItemData itemData);

    /**
     * Returns the reserved stock (the sum of reserved items of the
     * stock units)
     * 
     * @param itemData the according item data
     * @return the reserved stock
     */
    int getReservedStock(ItemData itemData);

    /**
     * Returns the stock (the sum of the amounts of the stock units).
     * 
     * @param itemData the according item data
     * @return the stock
     */
    int getStock(ItemData itemData);

    /**
     * Returns the number of stock units of the according item data.
     * 
     * @param itemData the according item data
     * @return the number of stock units
     */
    int getCount(ItemData itemData);

    /**
     * Returns an info object about the stock of the specified item
     * data.
     * 
     * @param itemData the item data to read the info for
     * @return the info object
     */
    StockUnitInfoTO getInfo(ItemData itemData);

    /**
     * Returns {@link StockUnit} by labelId and {@link Client}.
     * 
     * @param labelId
     * @return
     * @throws EntityNotFoundException 
     */
	public StockUnit getByLabelId(String labelId) throws EntityNotFoundException;

	/**
     * Returns a list of matching stockunits
     * 
     * @param labelId
     * @return
     * @throws EntityNotFoundException
     * @author krane 
     */
	public List<StockUnit> getBySerialNumber(ItemData idat, String serialNumber);
	
}
