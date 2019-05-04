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
import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LOSGoodsReceiptComponent {

//	/**
//	 * SystemProperty. The name of the printer for the unit load labels
//	 */
//	public static final String PROPERTY_KEY_PRINTER_GR = "GOODS_RECEIPT_PRINTER_NAME";
	
	/**
	 * SystemProperty. Enable to limit the receipt amount to the notified amount.
	 * If enabled, the receipt amount will be limited to the notified quantity of the advice 
	 * Otherwise, it is possible to receive as much as wanted
	 */
	public static final String GR_LIMIT_AMOUNT_TO_NOTIFIED = "GOODS_RECEIPT_LIMIT_AMOUNT_TO_NOTIFIED";


//	/**
//	 * Create a LOSStorageLocation assigned to the system client
//	 * with unbounded capacity, suitable for any UnitLoadType
//	 * and assigned to the GOODS_IN area.
//	 * 
//	 * @param name the name of the new location.
//	 * @return the new LOSStorageLocation
//	 */
//	public LOSStorageLocation createGoodsReceiptLocation(String name);
	
	/**
	 * Get all locations for goods receipt. If there are none yet created, 
	 * a default GoodsReceiptLocation will be created and returned as part of the list.
	 * 
	 * @return a list of all goods receipt locations.
	 */
	public List<LOSStorageLocation> getGoodsReceiptLocations() throws LOSLocationException;
	
	/**
	 * Retrieve {@link LOSGoodsReceipt} by its number.
	 * 
	 * @param number
	 * @return
	 */
	public LOSGoodsReceipt getByGoodsReceiptNumber(String number);
	
	/**
	 * Create a new Goods Receipt
	 * 
	 * @param operator
	 * @param licencePlate
	 * @param driverName
	 * @param forwarder
	 * @param deliveryNoteNumber
	 * @param receiptDate
	 * @return
	 */		
	public LOSGoodsReceipt createGoodsReceipt( Client c,
                                                    String licencePlate,
													String driverName,
													String forwarder,
													String deliveryNoteNumber,
													Date receiptDate);
	
	/**
	 * Cancels (undo) an existing {@link LOSGoodsReceipt}. All operations will be undone. 
	 * E.g. StockUnits which have been created will be deleted.
	 * 
	 * In case the state of receipt is {@link LOSGoodsReceiptState#RAW} the {@link LOSGoodsReceipt}
	 * can be deleted. Otherwise it has to be preserved an its state will be switched to {@link LOSGoodsReceiptState#CANCELED}.
	 * 
	 * @see #deleteGoodsReceipt(LOSGoodsReceipt)
	 * @see #remove(LOSGoodsReceipt, LOSGoodsReceiptPosition) 
	 * 
	 * @param gr
	 * @throws FacadeException
	 */
	public void cancel(LOSGoodsReceipt gr)throws FacadeException;
	
	
	/**
	 * Creates a new {@link LOSGoodsReceiptPosition}.
	 * @throws InventoryException 
	 * 
	 */
	public LOSGoodsReceiptPosition createGoodsReceiptPosition(Client client, 
															  LOSGoodsReceipt gr, 
															  String orderReference, BigDecimal amount
															  ) throws FacadeException;
	
	/**
	 * Creates a new {@link LOSGoodsReceiptPosition}.
	 * @throws InventoryException 
	 * 
	 */
	public LOSGoodsReceiptPosition createGoodsReceiptPosition(Client client, 
															  LOSGoodsReceipt gr, 
															  String orderReference,
															  BigDecimal amount,
															  LOSGoodsReceiptType receiptType,
															  String qaFault) throws FacadeException;
	
	public StockUnit receiveStock(LOSGoodsReceiptPosition grPosition, 
			  Lot batch,
			  ItemData item,
			  BigDecimal amount,
			  LOSUnitLoad unitLoad, String serialNumber)
		throws FacadeException;
    /**
     * Assigns a LOSAdvice to this LOSGoodsReceiptPosition. This is done during 
     * Goods receipt process
     * 
     * @param adv
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    public void assignAdvice(LOSAdvice adv, LOSGoodsReceiptPosition pos) throws FacadeException;
    
    /**
     * 
     * @return List of {@link LOSAdvice} suitable for this position, empty list if none exists 
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    public List<LOSAdvice> getSuitableLOSAdvice(String exp, Client c, Lot lot, ItemData itemData) throws InventoryException;
    
    /**
     * Removes a {@link LOSGoodsReceiptPosition} from its {@link LOSGoodsReceipt}.
     * In case the associated {@link LOSGoodsReceipt} is in state {@link LOSGoodsReceiptState.RAW},
     * the position can be removed from system. Otherwise it has to be preserved.
     * 
     * @param r 
     * @param pos
     * @throws InventoryException
     * @throws FacadeException 
     * @throws FacadeException 
     */
    void remove(LOSGoodsReceipt r, LOSGoodsReceiptPosition pos) throws FacadeException ;
    
    /**
     * Gets or creates a UnitLoad for the goods receipt process.
     * 
     * @param c the client for whom the process is done
     * @param sl the storage location where the process is done
     * @param type the type of the unit load
     * @param ref the labelID of the unit load
     * @return 
     * @throws LOSLocationException
     */
    public LOSUnitLoad getOrCreateUnitLoad(Client c,
            LOSStorageLocation sl, UnitLoadType type, String ref) throws FacadeException;
    
    /**
     * Finishes GoodsReceipt. After a call to this method the reference to {@link StockUnit}
     * at {@link LOSGoodsReceiptPosition.stockUnit} will be set to null. 
     * 
     * @param r 
     * @throws InventoryException
     * @throws InventoryTransferException 
     */
    public void finishGoodsReceipt(LOSGoodsReceipt r) throws InventoryException, InventoryTransferException;
    
    /**
     * Switch state to {@link LOSGoodsReceiptState#ACCEPTED}
     * @param gr
     * @throws InventoryException 
     */
    public void acceptGoodsReceipt(LOSGoodsReceipt gr) throws InventoryException;
    
//    /**
//     * creates an label for each {@link LOSGoodsReceiptPosition}
//     * 
//     * @param pos
//     * @return
//     * @throws FacadeException
//     */
//    public StockUnitLabel createStockUnitLabel(LOSGoodsReceiptPosition pos, String printer) throws FacadeException;

   /**
    * assigning an advice {@link LOSAdvice} to the {@link LOSGoodsReceipt} 
    * @param adv
    * @param r
 * @throws InventoryException 
    */
    public void assignAdvice(LOSAdvice adv, LOSGoodsReceipt r) throws InventoryException;
	
    /**
     * removes an advice {@link LOSAdvice} from  the {@link LOSGoodsReceipt}
     * @param adv
     * @param r
     * @throws InventoryException 
     */
	public void removeAssignedAdvice(LOSAdvice adv, LOSGoodsReceipt r) throws InventoryException;

	
	
}
