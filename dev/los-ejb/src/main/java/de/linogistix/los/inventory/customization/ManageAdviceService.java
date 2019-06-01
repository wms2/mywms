/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.OutOfRangeException;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.StockExistException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

@Local
public interface ManageAdviceService {

	/**
	 * Get a system wide unique number for a new advice. 
	 * 
	 * @return new advice number
	 */
	public String getNewAdviceNumber();
	
	/**
	 * Create a new managed instance of {@link LOSAdvice} or subclass.
	 * 
	 * @param cl client the advice should be assigned to. Must not be NULL.
	 * @param adviceNumber system wide unique advice number. See getNewAdviceNumber(). Must not be NULL.
	 * @param item the notified {@link ItemData}. Must not be NULL.
	 * @param notifiedAmount the notified amount. Must not be NULL.
	 * @return new advice
	 * @throws OutOfRangeException if notified amount is negative.
	 */
	public LOSAdvice createAdvice(Client cl, String adviceNumber, ItemData item, BigDecimal notifiedAmount) throws OutOfRangeException;
	
	/**
	 * Update is always possible.
	 * 
	 * @param adv advice to change
	 * @param newValue new additional content
	 * @return changed advice
	 */
	public LOSAdvice updateAdditionalContent(LOSAdvice adv, String newValue);
	
	/**
	 * Update is always possible.
	 * 
	 * @param adv advice to change
	 * @param newValue new external advice number
	 * @return changed advice
	 */
	public LOSAdvice updateExternalAdviceNumber(LOSAdvice adv, String newValue);
	
	/**
	 * Until no goods receipt the {@link ItemData} of {@link LOSAdvice} could be changed.
	 * 
	 * @param adv advice to change
	 * @param newValue new {@link ItemData}
	 * @return changed advice
	 * @throws StockExistException if stocks already have been receipt.
	 */
	public LOSAdvice updateItemData(LOSAdvice adv, ItemData newValue) throws StockExistException;
	
	/**
	 * Until no goods receipt the {@link Lot} of {@link LOSAdvice} could be changed.
	 * 
	 * @param adv advice to change
	 * @param newValue new {@link Lot}
	 * @return changed advice
	 * @throws StockExistException if stocks already have been receipt.
	 */
	public LOSAdvice updateLot(LOSAdvice adv, Lot newValue) throws StockExistException;
	
	/**
	 * Update is always possible.
	 * 
	 * @param adv advice to change
	 * @param newValue new expected delivery
	 * @return changed advice
	 */
	public LOSAdvice updateExpectedDelivery(LOSAdvice adv, Date newValue);

	/**
	 * Raising the notified amount is always possible.
	 * The notified amount could only be reduced down to the already received amount.
	 * 
	 * @param adv advice to change
	 * @param newValue new notified amount
	 * @return changed advice
	 * @throws StockExistException if the already received amount is higher than newValue.
	 * @throws OutOfRangeException if newValue is negative.
	 */
	public LOSAdvice updateNotifiedAmount(LOSAdvice adv, BigDecimal newValue) throws StockExistException, OutOfRangeException;

	
	public void deleteAdvice(LOSAdvice adv) throws InventoryException;
	
	/**
	 * Rereads the Advice-data from host.<br>
	 * 
	 * @param adv
	 */
	public void updateFromHost(LOSAdvice adv) throws Exception;
	
    public boolean isAdviceChangeable( LOSAdvice adv );

}
