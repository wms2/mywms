/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.UnitLoad;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
/**
 * After picking there are a number of {@link UnitLoad}s at the goods out area.
 * 
 * A GUI could perform the following work flow
 * <ul>
 * <li> show all raw {@link LOSGoodsOutRequest} {@link #getRaw()} in a combobox. Let the user choose one
 * <li> Accept and work on this current {@link LOSGoodsOutRequest}
 * <li> show all positions in a table and a scan field above
 * <li> when a label is scanned, mark the position as finished in the table
 * <li> when the page is left, call {@link #finish(). This should be possible if all positions are finished
 * </ul>
 * 
 * @author trautm
 *
 */
@Remote
public interface LOSGoodsOutFacade {
	
	/**
	 * 
	 * @return {@link LOSGoodsOutRequest} that are in LOSGoodsOut
	 */
	List<LOSGoodsOutRequestTO> getRaw();
	
	/**
	 * Start the operating of one {@link LOSGoodsOutRequest}
	 * @return {@link LOSGoodsOutRequest} that has been chosen
	 */
	LOSGoodsOutRequest start(Long orderId) throws FacadeException;
	
	/**
	 * Write additional info to the {@link LOSGoodsOutRequest} 
	 * @return {@link LOSGoodsOutRequest} that has been chosen
	 */
	LOSGoodsOutRequest update(Long orderId, String comment) throws FacadeException;
	
	/**
	 * finish one {@link LOSGoodsOutRequestPosition} by scanning the included {@link UnitLoad} labelId
	 * @param labelId
	 * @return
	 * @throws FacadeException
	 */
	LOSGoodsOutRequestPosition finishPosition(String labelId, Long orderId) throws FacadeException;
	
	/**
	 * finishes the {@link LOSGoodsOutRequest}. All {@link LOSGoodsOutRequestPosition} have to be finished before.
	 * @param out the current {@link LOSGoodsOutRequest}
	 * @return
	 * @throws FacadeException
	 */
	LOSGoodsOutRequest finish(Long orderId) throws FacadeException;

	/**
	 * Finish the order in the current state. Not confirmed positions will be canceled.
	 * @param out 
	 * @return
	 * @throws FacadeException
	 */
	LOSGoodsOutRequest finishOrder(Long goodsOutId) throws FacadeException;

	/**
	 * Confirms the {@link LOSGoodsOutRequest}. All positions will be confirmed too.
	 * @param goodsOutId
	 * @return
	 * @throws FacadeException
	 */
	public LOSGoodsOutRequest confirm(Long goodsOutId) throws FacadeException;

	/**
	 * Cancels the {@link LOSGoodsOutRequest}. Resets the state.
	 * @param pickingRequest
	 */
	void cancel(Long orderId) throws FacadeException;
	
	
	public LOSGoodsOutTO load(String number) throws FacadeException;
	
	public LOSGoodsOutTO getOrderInfo(Long orderId) throws FacadeException;
	
	/**
	 * Removes a finished order.
	 * @param req
	 * @throws FacadeException
	 */
	public void remove(Long reqId) throws FacadeException;


	/**
	 * Create one new shipping order for selected unit loads
	 * @param unitLoadIdList
	 * @throws FacadeException
	 */
	public void createGoodsOutOrder(List<Long> unitLoadIdList) throws FacadeException;
}
