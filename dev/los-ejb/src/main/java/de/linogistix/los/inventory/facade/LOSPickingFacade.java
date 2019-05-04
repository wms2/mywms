/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

/**
 * General methods to handle picking order and positions.
 * Used by the rich-client.
 * 
 * @author krane
 */
@Remote
public interface LOSPickingFacade {

	public void changePickingOrderUser( long orderId, String userName ) throws FacadeException;
	public void changePickingOrderDestination( long orderId, String destinationName ) throws FacadeException;
	public void changePickingOrderPrio( long orderId, int prio ) throws FacadeException;

	/**
	 * Releases the order for picking.<br>
	 * If the order is in progress, an exception is thrown.
	 * 
	 * @param orderId
	 * @throws FacadeException
	 */
	public void releaseOrder(long orderId) throws FacadeException; 

	/**
	 * Halts the order for picking.<br>
	 * If the order is in progress, an exception is thrown.
	 * 
	 * @param orderId
	 * @throws FacadeException
	 */
	public void haltOrder(long orderId) throws FacadeException; 
	
	/**
	 * Reserves the order for the given user.<br>
	 * If reservation is not possible, an exception is thrown.
	 * 
	 * @param orderId
	 * @param userName
	 * @throws FacadeException
	 */
	public void reserveOrder(long orderId, String userName) throws FacadeException;

	/**
	 * Resets the picking order. So another operator can use it.<br>
	 * Put the order back to the pool.<br>
	 * If reset is not possible, an exception is thrown.
	 * 
	 * @param orderId
	 * @throws FacadeException
	 */
	public void resetOrder(long orderId) throws FacadeException;
	
	/**
	 * Finishes a picking order in the current state.<br>
	 * Not finished unit loads of the picking order are moved to the clearing location.<br> 
	 * All not picked positions are moved back to the pool. 
	 * 
	 * @param orderId
	 * @throws FacadeException
	 */
	public void finishOrder(long orderId) throws FacadeException;
	
	/**
	 * Removes a picking order and all of its positions.
	 * If something does not work (Constraints,...) an exception is thrown.
	 * 
	 * @param orderId
	 * @throws FacadeException
	 */
	public void removeOrder(long orderId) throws FacadeException;

	/**
	 * Generate picking orders for the given customer order.
	 * 
	 * @param customerOrderId The ID of the customer order for what the picking orders are generated
	 * @param completeOnly If the complete order cannot be handled, an exception is thrown
	 * @param useSingleOrderService Use the service to create a single order
	 * @param useStratOrderService Use the service to create orders by strategy
	 * @param prio If => 0 this priority is given to the picking order. Otherwise the customer orders priority is used 
	 * @param destinationName The destination of the order. If null, the customer orders destination is used
	 * @param setProcessable 
	 * @param userName Reservation for one operator. If given, the order is reserved and the setProcessable flag is ignored
	 * @throws FacadeException
	 */
	public void createOrders( long customerOrderId, boolean completeOnly, boolean useSingleOrderService, boolean useStratOrderService, int prio, String destinationName, boolean setProcessable, String userName, String comment ) throws FacadeException;
	public void createOrders( List<Long> customerOrderIdList, boolean completeOnly, int prio, String destinationName, boolean setProcessable, String userName, String comment ) throws FacadeException;
	
	public void finishPickingUnitLoad( String label, String location ) throws FacadeException;

	public void confirmOrder( long orderId ) throws FacadeException;
}	