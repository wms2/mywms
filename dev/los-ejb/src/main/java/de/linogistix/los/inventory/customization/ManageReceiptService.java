/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.product.ItemData;


@Local
public interface ManageReceiptService {

	/**
	 * UserExit
	 * This method is called at the beginning of the GoodsReceipt posting
	 * 
	 * @param gr
	 * @throws InventoryException
	 */
	public void finishGoodsReceiptStart(LOSGoodsReceipt gr) throws InventoryException, InventoryTransferException;
	
	/**
	 * UserExit
	 * This method is called at the end of the GoodsReceipt posting
	 * 
	 * @param gr
	 * @throws InventoryException
	 */
	public void finishGoodsReceiptEnd(LOSGoodsReceipt gr) throws InventoryException;

	public void onGoodsReceiptPositionCollected(LOSGoodsReceiptPosition grPos) throws FacadeException;
	public void onGoodsReceiptPositionDeleted(Client client, ItemData itemData, BigDecimal amount, LOSAdvice advice, String unitLoadLabel, UnitLoadType unitLoadType) throws FacadeException;

}
