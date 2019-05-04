/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import javax.ejb.Local;

import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnit;

import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.exception.StockExistException;

@Local
public interface ManageItemDataService {

	/**
	 * A constraint safety method to create an ItemData. 
	 * 
	 * Constraints that will be checked are<br>
	 * Unique(client, number)
	 * 
	 * @param cl the client the new ItemData should be assigned to
	 * @param number item number
	 * @param name item name
	 * @param hu handling unit of the new item
	 * @return new managed instance of ItemData or sub class.
	 * @throws InventoryTransactionException
	 */
	public ItemData createItemData(Client cl, String number, String name, ItemUnit hu)
		throws InventoryTransactionException;
	
	/**
	 * ItemUnit could only be changed when there are no stocks
	 * of specified item in the warehouse.
	 * 
	 * @param item the item to change
	 * @param newValue new ItemUnit
	 * @return ItemData with changed ItemUnit
	 * @throws StockExistException if there are any stocks of specified item in warehouse
	 */
	public ItemData updateItemUnit(ItemData item, ItemUnit newValue) throws StockExistException;
	
	/**
	 * Scale could only be changed when there are no stocks 
	 * of specified item in the warehouse.
	 * 
	 * @param item the item to change
	 * @param newValue new scale
	 * @return ItemData with changed scale
	 * @throws StockExistException if there are any stocks of specified item in warehouse
	 */
	public ItemData updateScale(ItemData item, int newValue) throws StockExistException;
	
	/**
	 * Switch to NOT mandatory is always possible.
	 * Switch to mandatory is NOT possible if there are any stocks of specified item in the warehouse
	 * that have no lot assigned.
	 * 
	 * @param item the item to change
	 * @param newValue new scale
	 * @return ItemData with changed scale
	 * @throws StockExistException if there are any stocks of specified item in warehouse that have no lot assigned.
	 */
	public ItemData updateLotMandatory(ItemData item, boolean newValue) throws StockExistException;
	
	/**
	 * Switch to {@link SerialNoRecordType} NO_RECORD is always possible.
	 * Switch to {@link SerialNoRecordType} GOODS_OUT_RECORD is only possible 
	 * if there are no picked stocks of specified item.
	 * Switch to {@link SerialNoRecordType} ALWAYS_RECORD is only possible when there are no stocks at all
	 * of specified item in the warehouse.
	 * 
	 * @param item the item to change
	 * @param newValue new serial number record type
	 * @return ItemData with changed serial number record type
	 * @throws StockExistException if one of the conditions above failed.
	 */
	public ItemData updateSerialNoRecordType(ItemData item, SerialNoRecordType newValue) throws StockExistException;
	
	public ItemData updateName(ItemData item, String newValue);
	
	public ItemData updateDescription(ItemData item, String newValue);
	
	public ItemData updateAdviceMandatory(ItemData item, boolean newValue);
	
	public ItemData updateSaftyStock(ItemData item, int newValue);
	
	/**
	 * Deleting an ItemData is only possible when there are no stocks at all
	 * of specified item in the warehouse.
	 * 
	 * @param item ItemData to delete
	 * @throws StockExistException if there are any stocks of specified item in the warehouse.
	 */
	public void deleteItemData(ItemData item) throws StockExistException;
}
