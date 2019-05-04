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
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;

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
								 BigDecimal amount,
								 LOSUnitLoad unitLoad,
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
								 BigDecimal amount,
								 LOSUnitLoad unitLoad,
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
    public void transferStock(LOSUnitLoad src, LOSUnitLoad dest, String activityCode) throws InventoryException, FacadeException;
	public void transferStock(LOSUnitLoad src, LOSUnitLoad dest, String activityCode, boolean yesReallyDoIt) throws FacadeException;

    /**
     * Transfers given StockUnit from source UnitLoad to destination UnitLoad.
     * @param su
     * @param dest
     * @throws InventoryException
     * @throws FacadeException 
     */
    public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode) throws InventoryException, FacadeException; 
    
    /**
     * Transfers given StockUnit from source UnitLoad to destination UnitLoad.
     * @param su
     * @param dest
     * @param info
     * @throws InventoryException
     * @throws FacadeException 
     */
    public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode, String info) throws InventoryException, FacadeException;
    
    /**
     * Transfers given StockUnit from source UnitLoad to destination UnitLoad.
     * @param su
     * @param dest
     * @param activityCode
     * @param info
     * @throws InventoryException
     * @throws FacadeException
     */
    public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode, String info, String operator) throws InventoryException, FacadeException;
	public void transferStockUnit(StockUnit su, LOSUnitLoad dest, String activityCode, String comment, String operator, boolean yesReallyDoIt) throws FacadeException;

    /**
     * Tests whether the StockUnit can be placed on UnitLoad
     * @param su
     * @param ul
     * @return
     */
    boolean testSuiable(StockUnit su, LOSUnitLoad ul);
    
    /**
     * Returns amount of ItemData on this UnitLoad
     * @param idat
     * @return
     */
    BigDecimal getAmountOfUnitLoad(ItemData idat, LOSUnitLoad ul) throws InventoryException;
    
    /**
     * Returns total count of {@link StockUnit}s on this {@link UnitLoad} including carried {@link UnitLoad}s.
     * @param ul
     * @return
     * @throws InventoryException
     */
    public int getTotalStockUnitCount(LOSUnitLoad ul) throws InventoryException ;

	/**
     * Returns amount of ItemData on this StorageLocation
     * 
     * @param idat
     * @param sl
     * @return
     * @throws InventoryException
     */
	public BigDecimal getAmountOfStorageLocation(ItemData idat, LOSStorageLocation sl) throws InventoryException;

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
			LOSUnitLoad destUl, 
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
	 * Consolidates (combines) StockUnits on given UnitLoad paying attention to {@link LOSUnitLoadPackageType}
	 *
	 * @param ul
	 * @param activityCode
	 * @throws InventoryException
	 * @throws FacadeException
	 */
	public void consolidate(LOSUnitLoad ul, String activityCode) throws InventoryException, FacadeException;
	
	/**
	 * Tests whether all StockUnits on given {@link LOSUnitLoad} are of the same {@link ItemData} (than the given {@link StockUnit}).
	 * @param su
	 * @param ul
	 * @return true if all StockUnits on given {@link LOSUnitLoad} are of the same {@link ItemData}
	 * 
	 */
	public boolean testSameItemData(StockUnit su, LOSUnitLoad ul);
	
	/**
	 * Tests whether all StockUnits on given {@link LOSUnitLoad} are of the same {@link Lot} (than the given {@link Lot}).
	 *
	 * @param lot
	 * @param ul
	 * @return true if all StockUnits on given {@link LOSUnitLoad} are of the same {@link Lot}
	 */
	public boolean testSameLot(Lot lot, LOSUnitLoad ul);
	
	/**
	 * Tests whether all StockUnits on given {@link LOSUnitLoad} are of the same {@link ItemData} (than the given {@link ItemData}).
	 * @param idat
	 * @param ul
	 * @return true if all StockUnits on given {@link LOSUnitLoad} are of the same {@link ItemData}
	 * 
	 */
	public boolean testSameItemData(ItemData idat, LOSUnitLoad ul);
	
	/**
	 * 
	 * @param su
	 * @param ul
	 * @return
	 */
	public boolean testSameLot(StockUnit su, LOSUnitLoad ul);
	
	/**
	 * Sends {@link StockUnit} to special purpose {@link LOSStorageLocation} <strong>Nirwana</strong>.
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
	 * Sends {@link StockUnit} to special purpose {@link LOSStorageLocation} <strong>Nirwana</strong>.
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
	 * Sends all {@link StockUnit} of given {@link LOSUnitLoad} to special purpose {@link LOSStorageLocation} <strong>Nirwana</strong>.
	 * From here they will be deleted later.
	 * Programmers should be aware that after a call to this method
	 * the {@link StockUnit} should not be used any more.
	 * 
	 * @param ul
	 * @param activityCode
	 * @throws FacadeException
	 */
	public void sendStockUnitsToNirwana(LOSUnitLoad ul, String activityCode) throws FacadeException ;
	
	/**
	 * * Sends all {@link StockUnit} on given {@link LOSStorageLocation} to special purpose {@link LOSStorageLocation} <strong>Nirwana</strong>.
	 * From here they will be deleted later.
	 * Programmers should be aware that after a call to this method
	 * the {@link StockUnit} should not be used any more.
	 * 
	 * @param sl
	 * @param activityCode
	 * @throws FacadeException
	 */
	public void sendStockUnitsToNirwana(LOSStorageLocation sl, String activityCode) throws FacadeException; 
	
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
	public void sendUnitLoadToNirwanaIfEmpty(LOSUnitLoad ul) throws FacadeException;
	
	 /**
     * Removes {@link StockUnit} from system
     * 
     * @param su the stock unit to remove
     * @param activityCode for history {@link LOSStockUnitRecord}
     */
    void removeStockUnit(StockUnit su, String activityCode, boolean sendNotify) throws FacadeException;
    
    /**
     * Creates a StockUnit on given {@link LOSStorageLocation}
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
     * Deletes all StockUnits that are on the given {@link LOSStorageLocation}
     * 
     * @param sl
     * @param activityCode for history {@link LOSStockUnitRecord}
     * @throws FacadeException
     */
    void deleteStockUnitsFromStorageLocation(LOSStorageLocation sl, String activityCode) throws FacadeException;

	
	//--------------------------------------------------------------------------------------
	// Some Sanity Checks
	//--------------------------------------------------------------------------------------
	
	void cleanup() throws FacadeException;

	/**
	 * Recalculate the weight of a unit load.
	 * 
	 * @param unitLoad
	 */
	public BigDecimal recalculateWeight( LOSUnitLoad unitLoad );

	public LOSUnitLoad getOrCreateUnitLoad(Client c, ItemData idat, LOSStorageLocation sl, String ref) throws FacadeException;

}
