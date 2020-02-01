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

import org.mywms.model.Client;

import de.linogistix.los.common.exception.OutOfRangeException;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.model.LOSAdviceType;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.inventory.model.LOSUnitLoadAdviceState;
import de.wms2.mywms.product.ItemData;

@Local
public interface ManageUnitLoadAdviceService {
	
	/**
	 * Get a system wide unique number for a new advice. 
	 * 
	 * @return new advice number
	 */
	public String getNewAdviceNumber();
	
	/**
	 * Create a new managed instance of {@link LOSUnitLoadAdvice} or subclass.
	 * 
	 * @param cl client the advice should be assigned to. Must not be NULL.
	 * @param adviceNumber system wide unique advice number. See getNewAdviceNumber(). Must not be NULL.
	 * @param type specify {@link LOSAdviceType}. Must not be NULL.
	 * @param labelId bar code of the unit load. Must not be NULL.
	 * @return new advice
	 * @throws OutOfRangeException if notified amount is negative.
	 */
	public LOSUnitLoadAdvice createAdvice(Client cl, String adviceNumber, LOSAdviceType type, String labelId) ;
	
	/**
	 * Create a new managed instance of {@link LOSUnitLoadAdvicePosition} or subclass
	 * and add the newly created position to {@link LOSUnitLoadAdvice}. 
	 * Each {@link LOSUnitLoadAdvicePosition} meets one stock unit on the advised unit load. 
	 * 
	 * @param unitLoadAdvice the advice the position should be added. Must not be NULL.
	 * @param lotNumber Number of the lot of the announced stock.
	 * @param itemData {@link ItemData} of the announced stock. Must not be NULL.
	 * @param notifiedAmount amount of the announced stock. Must not be NULL.
	 * @return the given {@link LOSUnitLoadAdvice} with a new position added.
	 * @throws OutOfRangeException if notified amount is negative.
	 */
	public LOSUnitLoadAdvice addPosition(LOSUnitLoadAdvice unitLoadAdvice, 
										 String lotNumber, 
										 ItemData itemData, 
										 BigDecimal notifiedAmount) throws OutOfRangeException;

	/**
	 * Change the type of the specified unit load advice into one of {@link LOSAdviceType}s.
	 * 
	 * @param ulAdvice {@link LOSUnitLoadAdvice} to change
	 * @param newType the new {@link LOSAdviceType}
	 * @return changed {@link LOSUnitLoadAdvice}
	 */
	public LOSUnitLoadAdvice updateAdviceType(LOSUnitLoadAdvice ulAdvice, LOSAdviceType newType);
		
	/**
	 * Switch the state of the specified {@link LOSUnitLoadAdvice} to a new {@link LOSUnitLoadAdviceState}.
	 *  
	 * @param ulAdvice the {@link LOSUnitLoadAdvice} to change
	 * @param newState the {@link LOSUnitLoadAdviceState} the advice should be switched to.
	 * @param switchStateInfo additional info 
	 * @return the changed advice or NULL if the advice had been deleted because of change to state FINISH.
	 * @throws InventoryException 
	 */
	public LOSUnitLoadAdvice switchState(LOSUnitLoadAdvice ulAdvice, LOSUnitLoadAdviceState newState, String switchStateInfo) throws InventoryException, InventoryTransactionException;

	/**
	 * Delete the specified {@link LOSUnitLoadAdvice} from database.
	 * 
	 * @param ulAdvice the {@link LOSUnitLoadAdvice} to delete.
	 */
	public void deleteUnitLoadAdvice(LOSUnitLoadAdvice ulAdvice);
}
