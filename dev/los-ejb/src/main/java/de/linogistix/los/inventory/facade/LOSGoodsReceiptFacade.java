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
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.report.ReportException;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.inventory.Lot;
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
        
    /**
     * Get all locations for goods receipt. If there are none yet created, 
     * a default GoodsReceiptLocation will be created and returned as part of the list.
     * 
     * @return a list of all goods receipt locations.
     */
    public List<BODTO<StorageLocation>> getGoodsReceiptLocations() throws LOSLocationException;

	public GoodsReceipt getGoodsReceipt(GoodsReceipt lazy);

    //------------------------------------------------------------------------
    // Process
    //-----------------------------------------------------------------------

	GoodsReceipt createGoodsReceipt(BODTO<Client> clientDto, String senderName, String forwarder, String deliveryNoteNumber, Date receiptDate,
			BODTO<StorageLocation> goodsInLocation, String additionalContent, int orderType);

	GoodsReceiptLine createGoodsReceiptLineAndLot(BODTO<Client> clientDto, GoodsReceipt goodsReceipt,
			String lotName, Date validFrom, Date validTo, BODTO<ItemData> itemDataDto, String unitLoadLabel,
			BODTO<UnitLoadType> unitLoadTypeDto, BigDecimal amount, BODTO<AdviceLine> adviceDto, int lock,
			String lockCause) throws FacadeException, ReportException;

	GoodsReceiptLine createGoodsReceiptLine(BODTO<Client> client, GoodsReceipt gr, BODTO<Lot> batch,
			BODTO<ItemData> item, String unitLoadLabel, BODTO<UnitLoadType> unitLoadType, BigDecimal amount,
			BODTO<AdviceLine> advice, int lock, String lockCause) throws FacadeException, ReportException;

	GoodsReceiptLine createGoodsReceiptLine(BODTO<Client> client, GoodsReceipt gr, BODTO<Lot> batch,
			BODTO<ItemData> item, String unitLoadLabel, BODTO<UnitLoadType> unitLoadType, BigDecimal amount,
			BODTO<AdviceLine> advice, int lock, String lockCause, String serialNumber, String targetLocationName,
			String targetUnitLoadName, String printer) throws FacadeException, ReportException;
    
    /**
     * Removes a GoodsReceipt position from GoodsReceipt
     */
    void removeGoodsReceiptLine(GoodsReceiptLine goodsReceiptLine) throws InventoryException, FacadeException;

    
    /**
     * assigning an advice to the GoodsReceipt
     * @param adv
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
	void assignAdvice(BODTO<AdviceLine> adviceLineDto, GoodsReceipt goodsReceipt) throws BusinessException;

    /**
     * removes an advice from  the GoodsReceipt
     * @param adv
     * @param pos
     * @throws de.linogistix.los.inventory.exception.InventoryException
     */
	void removeAssigendAdvice(BODTO<AdviceLine> adviceLineDto, GoodsReceipt goodsReceipt) throws BusinessException;

    /**
     * To accept the receipt.
     */
    void acceptGoodsReceipt(GoodsReceipt goodsReceipt) throws InventoryException, BusinessException;

    /**
     * To finish the process
     */
    void finishGoodsReceipt(GoodsReceipt r) throws InventoryException, BusinessException;

	
    public Lot createLot(String lotNameInput, ItemData itemData, Date validTo);
    
	/**
	 * Create and print labels for all positions of the goods receipt
	 */
	void createStockUnitLabel(GoodsReceipt goodsReceipt, String printer) 	throws FacadeException;

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
