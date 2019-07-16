/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.OrderReceipt;
import de.wms2.mywms.delivery.DeliveryOrder;

/**
 * @author krane
 *
 */
@Local
public interface LOSOrderReceiptReport {
	
	/**
	 * Generation of an order receipt.
	 * The object and report is generated. 
	 * It is not persisted.
	 * 
	 * @param order
	 * @return
	 * @throws FacadeException
	 */
	public OrderReceipt generateOrderReceipt(DeliveryOrder order) throws FacadeException;
	
	/**
	 * Persist an order receipt.
	 * If it is already existing, it will be replaced.
	 * 
	 * @param receipt
	 * @return
	 * @throws FacadeException
	 */
	public OrderReceipt storeOrderReceipt(OrderReceipt receipt) throws FacadeException;
	
}
