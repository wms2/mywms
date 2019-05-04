/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishGenerator {

	/**
	 * Request a replenish order. <b>Only the request is stored in a own transaction.</b><br>
	 * The order (with stock selection and location reservation) has to be calculated in a separate step.<br>
	 * This method is dedicated to processes like the release of orders which rollback their transaction, 
	 * if the complete amount is not available.<br>
	 * Use calculateReleasedOrders() to make processable replenish orders.
	 *  
	 * @param itemData The requested item data. Must not be null
	 * @param lot If not null, this lot is requested
	 * @param requestedAmount If not null, it tries to replenish the requested amount
	 * @param requestedLocation If not null, the replenish will go to this location
	 * @param requestedRack If not null, the replenish will go to this rack
	 */
	public void generateRequest( ItemData itemData, Lot lot, BigDecimal requestedAmount, LOSStorageLocation requestedLocation, LOSRack requestedRack );
	
	/**
	 * Generates and calculates a replenish order. If no stock is found it will return a null value.
	 * 
	 * @param itemData The requested item data. Must not be null
	 * @param lot If not null, this lot is requested
	 * @param requestedAmount If not null, it tries to replenish the requested amount
	 * @param requestedLocation If not null, the replenish will go to this location
	 * @param requestedRack If not null, the replenish will go to this rack
	 * @return
	 * @throws FacadeException
	 */
	public LOSReplenishOrder calculateOrder( ItemData itemData, Lot lot, BigDecimal requestedAmount, LOSStorageLocation destinationLocation, LOSRack destinationRack ) throws FacadeException;

	/**
	 * All fixed locations are checked and refilled
	 * 
	 * @return
	 * @throws FacadeException
	 */
	public List<LOSReplenishOrder> refillFixedLocations() throws FacadeException;

	/**
	 * Calculates all replenish orders in state State.RELEASED.
	 * 
	 * @throws FacadeException
	 */
	public void calculateReleasedOrders() throws FacadeException;

}
