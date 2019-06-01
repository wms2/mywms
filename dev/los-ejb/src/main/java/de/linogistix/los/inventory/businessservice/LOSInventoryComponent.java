/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadPackageType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.PackagingUnit;

@Local
public interface LOSInventoryComponent {

	/**
	 * Creates a {@link StockUnit}
	 * 
	 * @param client to whom the created StockUnit belongs
	 * @param batch of that {@link Lot}
	 * @param item and of that {@link ItemData}
	 * @param amount of items
	 * @param unitLoad where the StockUnit will be placed on
	 * @param activityCode
	 * @param serialNumber
	 * @return the created {@link StockUnit}
	 * @throws InventoryException 
	 */
	public StockUnit createStock(Client client, 
								 Lot batch,
								 ItemData item,
								 BigDecimal amount, PackagingUnit packagingUnit,
								 UnitLoad unitLoad,
								 String activityCode, String serialNumber) throws FacadeException;
	
	/**
	 * Creates a {@link StockUnit}
	 * 
	 * @param client to whom the created StockUnit belongs
	 * @param batch of that {@link Lot}
	 * @param item and of that {@link ItemData}
	 * @param amount of items
	 * @param unitLoad where the StockUnit will be placed on
	 * @param activityCode
	 * @param serialNumber
	 * @param operator
	 * @return the created {@link StockUnit}
	 * @throws InventoryException 
	 */
	public StockUnit createStock(Client client, 
								 Lot batch,
								 ItemData item,
								 BigDecimal amount, PackagingUnit packagingUnit,
								 UnitLoad unitLoad,
								 String activityCode, String serialNumber, String operator, boolean sendNotify) throws FacadeException;

    /**
     * Transfers all StockUnits from source UnitLoad to destination UnitLoad.
     * 
     * 
     * @param src transfer from this unitLoad
     * @param dest to this unitLoad
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws FacadeException 
     */
    public void transferStock(UnitLoad src, UnitLoad dest, String activityCode) throws InventoryException, FacadeException;
	public void transferStock(UnitLoad src, UnitLoad dest, String activityCode, boolean yesReallyDoIt) throws FacadeException;

    /**
     * Transfers given StockUnit from source UnitLoad to destination UnitLoad.
     * @param su
     * @param dest
     * @throws InventoryException
     * @throws FacadeException 
     */
    public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode) throws InventoryException, FacadeException; 
    
    /**
     * Transfers given StockUnit from source UnitLoad to destination UnitLoad.
     * @param su
     * @param dest
     * @param info
     * @throws InventoryException
     * @throws FacadeException 
     */
    public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode, String info) throws InventoryException, FacadeException;
    
    /**
     * Transfers given StockUnit from source UnitLoad to destination UnitLoad.
     * @param su
     * @param dest
     * @param activityCode
     * @param info
     * @throws InventoryException
     * @throws FacadeException
     */
    public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode, String info, String operator) throws InventoryException, FacadeException;
	public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode, String comment, String operator, boolean yesReallyDoIt) throws FacadeException;

    /**
     * Tests whether the StockUnit can be placed on UnitLoad
     * @param su
     * @param ul
     * @return
     */
    boolean testSuiable(StockUnit su, UnitLoad ul);
    
    /**
     * Returns amount of ItemData on this UnitLoad
     * @param idat
     * @return
     */
    BigDecimal getAmountOfUnitLoad(ItemData idat, UnitLoad ul) throws InventoryException;
    
    /**
     * Returns total count of {@link StockUnit}s on this {@link UnitLoad} including carried {@link UnitLoad}s.
     * @param ul
     * @return
     * @throws InventoryException
     */
    public int getTotalStockUnitCount(UnitLoad ul) throws InventoryException ;

	/**
     * Returns amount of ItemData on this StorageLocation
     * 
     * @param idat
     * @param sl
     * @return
     * @throws InventoryException
     */
	public BigDecimal getAmountOfStorageLocation(ItemData idat, StorageLocation sl) throws InventoryException;

	/**
	 * Transfers the given amount of one StockUnit to another and reduces reservation
	 * @param su
	 * @param dest
	 * @param amount
	 * @param activityCode
	 * @throws InventoryException if reservedAmount is not sufficient
	 * @throws FacadeException
	 */
	public void transferStockFromReserved(StockUnit su, StockUnit dest, BigDecimal amount, String activityCode) throws InventoryException, FacadeException;
	
	/**
	 * Transfers the given amount of one StockUnit to another and pays <strong>no</strong> attention to reserved amount. Thus the reserved amount will not be reduced.
	 *
	 * @param su
	 * @param dest
	 * @param amount
	 * @param activityCode
	 * @throws FacadeException
	 */
	public void transferStock(StockUnit su, StockUnit dest, BigDecimal amount, String activityCode) throws FacadeException;
	
	/**
	 * Reduces the stock to split, creates a new one with amount = takeAwayAmount on unit load destUl.
	 * 
	 * @param stockToSplit
	 * @param destUl
	 * @param takeAwayAmount
	 * @param activityCode
	 * @return
	 * @throws FacadeException
	 */
	public StockUnit splitStock(StockUnit stockToSplit, 
			UnitLoad destUl, 
			BigDecimal takeAwayAmount, 
			String activityCode) throws FacadeException;
	
	/**
	 * Changes the amount and pays attention to reserved amount.
	 * Releases reservedAmount  <code>if (forceRelease == true)</code>
	 * 
	 * @param su
	 * @param amount
	 * @param forceRelease if true and new amount is smaller than the current reservedAmount this one will be reduced
	 * @param activityCode
	 * @throws InventoryException if amount reduction will interfere with reservedAmount
	 */
	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode) throws FacadeException;
	
	/**
	 * Changes the amount and pays attention to reserved amount.
	 * Releases reservedAmount  <code>if (forceRelease == true)</code>
	 * 
	 * @param su
	 * @param amount
	 * @param forceRelease if true and new amount is smaller than the current reservedAmount this one will be reduced
	 * @param activityCode
	 * @param comment
	 * @throws InventoryException if amount reduction will interfere with reservedAmount
	 */
	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment) throws FacadeException;
	
	/**
	 * Changes the amount and pays attention to reserved amount.
	 * Releases reservedAmount  <code>if (forceRelease == true)</code>
	 * @param su
	 * @param amount
	 * @param forceRelease
	 * @param activityCode
	 * @param comment
	 * @param operator if null, the currently logged on user will be taken
	 * @throws InventoryException
	 */
	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment, String operator) throws FacadeException;
	
	/**
	 * Changes the amount and pays attention to reserved amount.
	 * Releases reservedAmount  <code>if (forceRelease == true)</code>
	 * @param su
	 * @param amount
	 * @param forceRelease
	 * @param activityCode
	 * @param comment
	 * @param operator if null, the currently logged on user will be taken
	 * @param sendNotify A host-message will be sent.
	 * @throws InventoryException
	 */
	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment, String operator, boolean sendNotify) throws FacadeException;

	/**
	 * Changes the reservedAmount
	 * @param su
	 * @param reservedAmount
	 * @param forceRelease
	 * @param activityCode
	 * @throws InventoryException if new reservedAmount is greater than avaiableAmount
	 */
	public void changeReservedAmount(StockUnit su, BigDecimal reservedAmount,
			String activityCode) throws InventoryException;
	
	/**
	 * Consolidates (combines) StockUnits on given UnitLoad paying attention to {@link UnitLoadPackageType}
	 *
	 * @param ul
	 * @param activityCode
	 * @throws InventoryException
	 * @throws FacadeException
	 */
	public void consolidate(UnitLoad ul, String activityCode) throws InventoryException, FacadeException;
	
	/**
	 * Tests whether all StockUnits on given {@link UnitLoad} are of the same {@link ItemData} (than the given {@link StockUnit}).
	 * @param su
	 * @param ul
	 * @return true if all StockUnits on given {@link UnitLoad} are of the same {@link ItemData}
	 * 
	 */
	public boolean testSameItemData(StockUnit su, UnitLoad ul);
	
	/**
	 * Tests whether all StockUnits on given {@link UnitLoad} are of the same {@link Lot} (than the given {@link Lot}).
	 *
	 * @param lot
	 * @param ul
	 * @return true if all StockUnits on given {@link UnitLoad} are of the same {@link Lot}
	 */
	public boolean testSameLot(Lot lot, UnitLoad ul);
	
	/**
	 * Tests whether all StockUnits on given {@link UnitLoad} are of the same {@link ItemData} (than the given {@link ItemData}).
	 * @param idat
	 * @param ul
	 * @return true if all StockUnits on given {@link UnitLoad} are of the same {@link ItemData}
	 * 
	 */
	public boolean testSameItemData(ItemData idat, UnitLoad ul);
	
	/**
	 * 
	 * @param su
	 * @param ul
	 * @return
	 */
	public boolean testSameLot(StockUnit su, UnitLoad ul);
	
	/**
	 * Sends {@link StockUnit} to special purpose {@link StorageLocation} <strong>Nirwana</strong>.
	 * From here they will be deleted later.
	 * Programmers should be aware that after a call to this method
	 * the {@link StockUnit} should not be used any more.
	 * 
	 * @param su
	 * @param activityCode
	 * @throws InventoryException
	 * @throws FacadeException
	 */
	public void sendStockUnitsToNirwana(StockUnit su, String activityCode) throws InventoryException, FacadeException;
	/**
	 * Sends {@link StockUnit} to special purpose {@link StorageLocation} <strong>Nirwana</strong>.
	 * From here they will be deleted later.
	 * Programmers should be aware that after a call to this method
	 * the {@link StockUnit} should not be used any more.
	 * @param su
	 * @param activityCode
	 * @param operator
	 * @throws FacadeException
	 */
	public void sendStockUnitsToNirwana(StockUnit su, String activityCode, String operator) throws FacadeException;

	/**
	 * Sends all {@link StockUnit} of given {@link UnitLoad} to special purpose {@link StorageLocation} <strong>Nirwana</strong>.
	 * From here they will be deleted later.
	 * Programmers should be aware that after a call to this method
	 * the {@link StockUnit} should not be used any more.
	 * 
	 * @param ul
	 * @param activityCode
	 * @throws FacadeException
	 */
	public void sendStockUnitsToNirwana(UnitLoad ul, String activityCode) throws FacadeException ;
	
	/**
	 * * Sends all {@link StockUnit} on given {@link StorageLocation} to special purpose {@link StorageLocation} <strong>Nirwana</strong>.
	 * From here they will be deleted later.
	 * Programmers should be aware that after a call to this method
	 * the {@link StockUnit} should not be used any more.
	 * 
	 * @param sl
	 * @param activityCode
	 * @throws FacadeException
	 */
	public void sendStockUnitsToNirwana(StorageLocation sl, String activityCode) throws FacadeException; 
	
	/**
	 * Sets dates as indicated and locks the Lot if necessary.
	 * 
	 * @param lot
	 * @param bestBeforeEnd
	 * @param useNotBefore
	 */
	void processLotDates(Lot lot, Date bestBeforeEnd, Date useNotBefore);
	
	/**
	 * Send Unitload to Nirwana if no Stokunit is left.
	 * @param ul
	 * @throws FacadeException
	 */
	public void sendUnitLoadToNirwanaIfEmpty(UnitLoad ul) throws FacadeException;
	
	 /**
     * Removes {@link StockUnit} from system
     * 
     * @param su the stock unit to remove
     * @param activityCode for history {@link LOSStockUnitRecord}
     */
    void removeStockUnit(StockUnit su, String activityCode, boolean sendNotify) throws FacadeException;
    
    /**
     * Creates a StockUnit on given {@link StorageLocation}
     * 
     * @param clientRef
     * @param slName
     * @param articleRef
     * @param lotRef
     * @param amount
     * @param unitLoadRef
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws org.mywms.facade.FacadeException
     */
    StockUnit createStockUnitOnStorageLocation(String clientRef, String slName, String articleRef, String lotRef, BigDecimal amount, String unitLoadRef, String activityCode, String serialNumber) 
            throws EntityNotFoundException,InventoryException, FacadeException;
    
    /**
     * Deletes all StockUnits that are on the given {@link StorageLocation}
     * 
     * @param sl
     * @param activityCode for history {@link LOSStockUnitRecord}
     * @throws FacadeException
     */
    void deleteStockUnitsFromStorageLocation(StorageLocation sl, String activityCode) throws FacadeException;

	
	//--------------------------------------------------------------------------------------
	// Some Sanity Checks
	//--------------------------------------------------------------------------------------
	
	void cleanup() throws FacadeException;

	/**
	 * Recalculate the weight of a unit load.
	 * 
	 * @param unitLoad
	 */
	public BigDecimal recalculateWeight( UnitLoad unitLoad );

	public UnitLoad getOrCreateUnitLoad(Client c, ItemData idat, StorageLocation sl, String ref) throws FacadeException;

}
