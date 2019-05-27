/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * facade for processing goods receipt (incoming goods).
 * 
 * @author trautm
 */
@Remote
public interface LOSGoodsReceiptFacade {
    
    
    //-----------------------------------------------------------------------
    // Query data for Process input to choose from
    //----------------------------------------------------------------------
    
    /**
     * Returns a List of {@link CLient} {@link BODTO}s for whom this process is done
     * 
     * @return a List of {@link CLient} {@link BODTO}s
     */
    public List<BODTO<Client>> getAllowedClients() throws BusinessObjectQueryException;

    /**
     * Returns List of  {@link Lot} {@link BODTO}s by string expression (i.e. name of lot), {@link Client} and {@link ItemData}
     * 
     * @param lotExp LIKE expression for lot name
     * @param client client the lot belongs to
     * @param idat item data the lot belongs to
     * @return List of allowed lots
     */
    public List<BODTO<Lot>> getAllowedLots(String lotExp, BODTO<Client> client, BODTO<ItemData> idat );
    
    /**
     * Returns List of  {@link ItemData} {@link BODTO}s by string expression (i.e. number of item data), {@link Client} and {@link Lot}
	 *
     * @param exp LIKE expression for item data name
     * @param client client the item data belongs to
     * @param lot lot of the item data
     * @return List of allowed item datas
     */
    public List<BODTO<ItemData>> getAllowedItemData(String exp, BODTO<Client> client, BODTO<Lot> lot);
     
    /**
     * Returns List of {@link LOSAdvice} {@link BODTO}s by string expression (i.e. number of {@link LOSAdvice}), {@link Client}, {@link Lot} and {@link ItemData}
     * @param exp LIKE expression for advice number
     * @param client client the advice belongs to
     * @param lot lot of the advice
     * @param idat item data of the advice 
     * @return List of allowed advices
     * @throws InventoryException
     */
    public List<LOSAdvice> getAllowedAdvices(String exp, BODTO<Client> client, BODTO<Lot> lot, BODTO<ItemData> idat) throws InventoryException;
    
    /**
     * Returns {@link UnitLoad} by given parameters. If none is found creates a new one.
     *  
     * @param c the client of the unitload
     * @param sl the storage location where the unit load is 
     * @param type the unit load type of the unit load
     * @param ref the labelID of the unitload
     * @return An existing or newly created unit load.
     * @throws LOSLocationException
     */
    public BODTO<UnitLoad> getOrCreateUnitLoad(BODTO<Client> c,
            BODTO<StorageLocation> sl, BODTO<UnitLoadType> type, String ref) throws FacadeException;

    /**
     * Returns default {@link UnitLoadType} associated with the given {@link StorageLocation}.
     * 
     * @param sl the storage location where the process is done
     * @return
     */
    public BODTO<UnitLoadType> getUnitLoadType(BODTO<StorageLocation> sl);
        
    /**
     * Get all locations for goods receipt. If there are none yet created, 
     * a default GoodsReceiptLocation will be created and returned as part of the list.
     * 
     * @return a list of all goods receipt locations.
     */
    public List<BODTO<StorageLocation>> getGoodsReceiptLocations() throws LOSLocationException;

    /**
     * REturns/Reload Entity data from database
     * @param lazy a previously and lazily fetched {@link LOSGoodsReceipt}
     * @return An eager loaded {@link LOSGoodsReceipt}
     */
    public LOSGoodsReceipt getGoodsReceipt(LOSGoodsReceipt lazy);

    //------------------------------------------------------------------------
    // Process
    //-----------------------------------------------------------------------

    /**
     * Creates a {@link LOSGoodsReceipt}
     * 
     * @param operator
     * @param licencePlate
     * @param driverName
     * @param forwarder
     * @param deliveryNoteNumber
     * @param receiptDate
     * @return
     */
    public LOSGoodsReceipt createGoodsReceipt( BODTO<Client> client,
            String licencePlate,
            String driverName,
            String forwarder,
            String deliveryNoteNumber,
            Date receiptDate,
            BODTO<StorageLocation> goodsInLocation,
            String additionalInfo);

    /**
     * Creates a {@link LOSGoodsReceiptPosition} within the {@link LOSGoodsReceipt}.
     * 
     * @param client
     * @param gr
     * @param batch
     * @param item
     * @param unitLoad
     * @param amount
     * @param advice
     * @return
     * @throws FacadeException
     * @throws de.linogistix.los.report.ReportException
     */
    public LOSGoodsReceiptPosition createGoodsReceiptPosition(BODTO<Client> client,
            LOSGoodsReceipt gr,
            BODTO<Lot> batch,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType,
            BigDecimal amount, BODTO<LOSAdvice> advice) throws FacadeException, ReportException;
    /**
     * Creates {@link Lot} and calls {@link #createGoodsReceiptPosition(BODTO, LOSGoodsReceipt, BODTO, BODTO, String, BODTO, BigDecimal, BODTO)}
     * 
     * @param client
     * @param gr
     * @param lotName
     * @param validFrom
     * @param validTo
     * @param expireLot
     * @param item
     * @param unitLoadLabel
     * @param unitLoadType
     * @param amount
     * @param advice
     * @return
     * @throws FacadeException
     * @throws ReportException
     */
    public LOSGoodsReceiptPosition createGoodsReceiptPositionAndLot(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            String lotName,
            Date validFrom,
            Date validTo,
            boolean expireLot,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType,
            BigDecimal amount,
            BODTO<LOSAdvice> advice,
            LOSGoodsReceiptType receiptType,
            int lock,
            String lockCause
    	) throws FacadeException, ReportException;
            
    /**
     * 
     * @param client
     * @param gr
     * @param batch
     * @param item
     * @param unitLoad
     * @param amount
     * @param advice
     * @param receiptType
     * @param lock Lock of the created StockUnit (quality assurance)
     * @param lockCause why it is locked
     * @return
     * @throws FacadeException
     * @throws ReportException
     */
    public LOSGoodsReceiptPosition createGoodsReceiptPosition(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            BODTO<Lot> batch,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType,
            BigDecimal amount,
            BODTO<LOSAdvice> advice,
            LOSGoodsReceiptType receiptType,
            int lock,
            String lockCause) throws FacadeException, ReportException;
    /**
     * Creates a GoodsRceiptPosition.
     * 
     * @param client
     * @param gr
     * @param batch
     * @param item
     * @param unitLoadLabel
     * @param unitLoadType
     * @param amount
     * @param advice
     * @param receiptType
     * @param lock
     * @param lockCause
     * @param serialNumber, null means no serial number
     * @param targetLocationName, null means generation on default GR-Location. Otherwise the unit load is moved to the given location
     * @return
     * @throws FacadeException
     * @throws ReportException
     */
    public LOSGoodsReceiptPosition createGoodsReceiptPosition(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            BODTO<Lot> batch,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType, 
            BigDecimal amount,
            BODTO<LOSAdvice> advice,
            LOSGoodsReceiptType receiptType,
            int lock,
            String lockCause, String serialNumber, String targetLocationName, String targetUnitLoadName, String printer) throws FacadeException, ReportException;
    
    
    /**
     * Removes a {@link LOSGoodsReceiptPosition} from {@link LOSGoodsReceipt}
     * 
     * @param r
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws FacadeException 
     */
    public void removeGoodsReceiptPosition(LOSGoodsReceipt r, LOSGoodsReceiptPosition pos) throws FacadeException;
    
    /**
     * Cancels a {@link LOSGoodsReceipt}: Removes all positions and delete the {@link LOSGoodsReceipt} itself.
     * @param r
     * @throws FacadeException 
     */
    public void cancelGoodsReceipt(LOSGoodsReceipt r) throws FacadeException;
    
    /**
     * assigning an advice (Avis) to the {@link LOSGoodsReceiptPosition}
     * @param adv
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    public void assignLOSAdvice(LOSAdvice adv, LOSGoodsReceiptPosition pos) throws FacadeException;
    
    /**
     * assigning an advice {@link LOSAdvice} to the {@link LOSGoodsReceipt}
     * @param adv
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    public void assignLOSAdvice(BODTO<LOSAdvice> adv, LOSGoodsReceipt pos) throws FacadeException;
    
    /**
     * removes an advice {@link LOSAdvice} from  the {@link LOSGoodsReceipt}
     * @param adv
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    public void removeAssigendLOSAdvice(BODTO<LOSAdvice> adv, LOSGoodsReceipt r) throws InventoryException;
    
    /**
     * To accept the receipt.
     * 
     * @param r
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
    public void acceptGoodsReceipt(LOSGoodsReceipt r) throws InventoryException;

    /**
     * To finish the process
     * @param r
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws InventoryTransferException 
     */
    public void finishGoodsReceipt(LOSGoodsReceipt r) throws InventoryException, InventoryTransferException;

	
    public Lot createLot(Client client, String lotNameInput, ItemData itemData, Date validTo);
    
	/**
	 * Create and print labels for all positions of the goods receipt
	 * @param LOSGoodsReceipt 
	 * @throws FacadeException
	 */
	public void createStockUnitLabel(LOSGoodsReceipt rec, String printer) 	throws FacadeException;
	
	/**
	 * Create and print a label for the goods receipt position
	 * @param LOSGoodsReceiptPosition
	 * @return
	 * @throws FacadeException
	 */
	public StockUnitLabel createStockUnitLabel(LOSGoodsReceiptPosition pos, String printer) 	throws FacadeException;
    
	/**
	 * Checks, whether the serialNo is allowed to use in goods receipt.
	 * 
	 * @param serialNo
	 * @param itemData
	 * @return
	 */
	public boolean checkSerialNumber( String serialNo, ItemData itemData);
	
	public void createStock(
		    String clientNumber,
		    String lotName,
		    String itemDataNumber,
		    String unitLoadLabel,
		    String unitLoadTypeName, 
		    BigDecimal amount,
		    int lock, String lockCause, 
		    String serialNumber, 
		    String targetLocationName, 
		    String targetUnitLoadName) throws FacadeException;
	
	/**
	 * Find the location with the oldest stock, where new stock can be added.
	 * @param itemData
	 * @param lot
	 * @return
	 * @throws FacadeException
	 */
	public String getOldestLocationToAdd( ItemData itemData, Lot lot ) throws FacadeException;
	
}
