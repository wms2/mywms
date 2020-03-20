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
import org.mywms.model.User;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;

/**
 * Business service to handle goods out order operations.
 * <p>
 * The basic entity of the picking process is the {@link PickingOrderLine}.<br>
 * The {@link PickingOrder} is just a pooling of some PickingPositions. <br>
 * All items collected for one picking order, are put into one unit load.<br>
 * </p><p>
 * For parallel picking multiple picking orders are assigned to a user at one 
 * time.  
 * </p><p>
 * <b>Unit load handling:</b>
 * One unit load is assigned to a picking order. Here is a restriction, that it
 * is only possible to pick one order into one target unit load at one time.<br>
 * Normally the unit load is generated on the users home location. When 
 * finishing the picking tour, the unit load is placed somewhere on a 
 * destination location. Maybe goods out or packing.<br>
 * Per default, if you do not explicitly move the unit load, it is moved to
 * the default goods out location on finishing the order.
 * </p>
 * 
 * 
 * @author krane
 */
@Local
public interface LOSOrderBusiness {


	/**
	 * Finishes a customer order in the current state.<br>
	 * Related picking orders are NOT considered.<br>
	 * 
	 * @param deliveryOrder
	 * @throws FacadeException
	 */
    public DeliveryOrder finishDeliveryOrder(DeliveryOrder deliveryOrder) throws FacadeException;

	/**
	 * Confirmation of a customer order position.<br>
	 * The picked amount is calculated and the state is set.<br>
	 * If all positions are done, the customer order is finished too.
	 * 
	 * @param deliveryOrderLine
	 * @param amount
	 * @throws FacadeException
	 */
	public DeliveryOrderLine confirmDeliveryOrderLine(DeliveryOrderLine deliveryOrderLine, BigDecimal amount) throws FacadeException;

	/**
	 * Releases the picking order for picking.
	 * 
	 * @param pickingOrder
	 * @throws FacadeException
	 */
	public PickingOrder releasePickingOrder(PickingOrder pickingOrder) throws FacadeException;

	/**
	 * Halt a released picking order.
	 * 
	 * @param pickingOrder
	 * @throws FacadeException
	 */
	public PickingOrder haltPickingOrder(PickingOrder pickingOrder) throws FacadeException;

	/**
	 * Reserves the picking order for the given user.<br>
	 * If reservation is not possible, an exception is thrown.
	 * 
	 * @param pickingOrder
	 * @param user
	 * @param ignoreReservationGap If TRUE, a reservation for a different user will be ignored
	 * @return
	 * @throws FacadeException
	 */
	public PickingOrder reservePickingOrder(PickingOrder pickingOrder, User user, boolean ignoreReservationGap) throws FacadeException;
	
	/**
	 * Starts a picking order.<br>
	 * If start is not possible, an exception is thrown.
	 * 
	 * @param pickingOrder
	 * @param ignoreReservationGap If TRUE, a reservation for a different user will be ignored
	 * @throws FacadeException
	 */
	public PickingOrder startPickingOrder(PickingOrder pickingOrder, boolean ignoreReservationGap) throws FacadeException;

	/**
	 * Resets the picking order. <br>
	 * Put the order back to the pool.<br>
	 * If there are already picked positions, a new picking order is generated for the remaining positions.
	 * If reset is not possible, an exception is thrown.
	 * 
	 * @param pickingOrder
	 * @throws FacadeException
	 */
	public PickingOrder resetPickingOrder(PickingOrder pickingOrder) throws FacadeException;
	
	/**
	 * Finishes a picking order in the current state.<br>
	 * Not finished unit loads of the picking order are moved to the clearing location.<br> 
	 * All not picked positions are moved back to the pool. 
	 * 
	 * @param pickingOrder
	 * @throws FacadeException
	 */
	public PickingOrder finishPickingOrder(PickingOrder pickingOrder) throws FacadeException;

	/**
	 * Confirm one picking position.
	 * 
	 * @param pick
	 * @param pickToUnitLoad
	 * @param amountPicked
	 * @param amountRemain
	 * @param serialNoList
	 * @param counted. If TRUE the stocktaking record for the location will be written. Only valid if amountRemain is set.
	 * @throws FacadeException
	 */
	public void confirmPick(PickingOrderLine pick, Packet pickToUnitLoad, BigDecimal amountPicked, BigDecimal amountRemain, List<String> serialNoList, boolean counted) throws FacadeException;
	public void confirmPick(PickingOrderLine pick, Packet pickToUnitLoad, BigDecimal amountPicked, BigDecimal amountRemain, List<String> serialNoList) throws FacadeException;
	void confirmCompletePick(PickingOrderLine pick, StorageLocation destination) throws FacadeException;

	/**
	 * Cancellation of a single picking position.<br>
	 * The picking order is not affected or recalculated!
	 * 
	 * @param pick
	 * @throws FacadeException
	 */
	public PickingOrderLine cancelPick(PickingOrderLine pick) throws FacadeException;

	/**
	 * Changes the pick-from stock unit.
	 * 
	 * @param pick
	 * @param pickFromStockUnit
	 * @throws FacadeException
	 */
	public PickingOrderLine changePickFromStockUnit(PickingOrderLine pick, StockUnit pickFromStockNew) throws FacadeException;

	public Packet confirmPickingUnitLoad( Packet pickingUnitLoad, StorageLocation destination, int state ) throws FacadeException;

	public PickingOrder recalculatePickingOrderState( PickingOrder pickingOrder ) throws FacadeException;

	public void haltPickingPosition(PickingOrderLine pick) throws FacadeException;

}	