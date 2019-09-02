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

import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.wms2.mywms.inventory.UnitLoad;
/**
 * After picking there are a number of {@link UnitLoad}s at the goods out area.
 * 
 * A GUI could perform the following work flow
 * <ul>
 * <li> show all raw LOSGoodsOutRequest {@link #getRaw()} in a combobox. Let the user choose one
 * <li> Accept and work on this current GoodsOutRequest
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
	 * @return LOSGoodsOutRequest that are in LOSGoodsOut
	 */
	List<LOSGoodsOutRequestTO> getRaw();
	
	/**
	 * Start the operating of one order
	 * @return LOSGoodsOutRequest that has been chosen
	 */
	void start(Long orderId) throws FacadeException;
	
	/**
	 * finish one line by scanning the included {@link UnitLoad} labelId
	 * @param labelId
	 * @return
	 * @throws FacadeException
	 */
	void finishPosition(String labelId, Long orderId, String destination) throws FacadeException;
	
	/**
	 * finishes the LOSGoodsOutRequest. All LOSGoodsOutRequestPosition have to be finished before.
	 * @param out the current LOSGoodsOutRequest
	 * @return
	 * @throws FacadeException
	 */
	void finish(Long orderId) throws FacadeException;

	/**
	 * Finish the order in the current state. Not confirmed positions will be canceled.
	 * @param out 
	 * @return
	 * @throws FacadeException
	 */
	void finishOrder(Long goodsOutId) throws FacadeException;

	/**
	 * Confirms the LOSGoodsOutRequest. All positions will be confirmed too.
	 * @param goodsOutId
	 * @return
	 * @throws FacadeException
	 */
	void confirm(Long goodsOutId) throws FacadeException;

	/**
	 * Cancels the order. Resets the state.
	 */
	void cancel(Long orderId) throws FacadeException;

	/**
	 * Cancels the order lines.
	 */
	void cancelLine(List<Long> lineIdList) throws FacadeException;

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
