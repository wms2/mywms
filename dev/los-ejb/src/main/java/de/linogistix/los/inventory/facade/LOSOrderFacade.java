/*
 * Copyright (c) 2011-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Document;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.location.StorageLocation;

/**
 * @author krane
 *
 */
@Remote
public interface LOSOrderFacade {

	/**
	 * creates a new customer order.<br>
	 * 
	 * @param clientNumber
	 * @param externalNumber
	 * @param positions
	 * @param documentUrl
	 * @param labelUrl
	 * @param destination
	 * @param type
	 * @param deliveryDate
	 * @param startPicking
	 * @param comment
	 * @return
	 * @throws FacadeException
	 */
	public DeliveryOrder order(
			String clientNumber,
			String externalNumber,
			OrderPositionTO[] positions,
			String documentUrl,
			String labelUrl,
			String destination, 
			String orderStrategyName,
			Date deliveryDate, 
			int prio,
			boolean startPicking, boolean completeOnly,
			String comment) throws FacadeException;


	/**
	 * Finishes a customer order.<br>
	 * The order is finished in the current state. No further processing is done.
	 * 
	 * @param orderNumber
	 * @throws FacadeException
	 */
	public DeliveryOrder finishOrder(Long orderId) throws FacadeException;

	/**
	 * Deletes a customer order.<br>
	 * 
	 * @param orderNumber
	 * @throws FacadeException
	 */
	public void removeOrder(Long orderId) throws FacadeException;


	
	/**
	 * Returns a List of all usable goods-out locations
	 * 
	 * @return
	 * @throws FacadeException
	 */
	public List<String> getGoodsOutLocations() throws FacadeException;
	public List<BODTO<StorageLocation>> getGoodsOutLocationsBO() throws FacadeException;

	public void changeOrderPrio( Long orderId, int prio ) throws FacadeException;

	
	public Document generateReceipt( Long orderId, boolean replace ) throws FacadeException;
	
	public Document generateUnitLoadLabel( String label, boolean save ) throws FacadeException;

	/**
	 * Generate goods-out request for pending order
	 * @param orderId
	 * @throws FacadeException
	 */
	void processOrderPickedFinish(List<BODTO<DeliveryOrder>> orders) throws FacadeException;


}
