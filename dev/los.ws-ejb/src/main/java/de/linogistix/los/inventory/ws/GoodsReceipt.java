/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import javax.ejb.Remote;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.location.model.LOSStorageLocation;

@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface GoodsReceipt extends java.rmi.Remote{
	
	/**
	 * Creates a {@link LOSGoodsReceipt}.
	 * 
	 * @param client The client for whom the process is done
	 * @param storageLocation the place (i.e. a {@link LOSStorageLocation} for goods in where the process is done
	 * @param licencePlate the license plate of the delivery truck
	 * @param driver The name of the driver
	 * @param forwarder the forwarder company
	 * @param deliveryNoteNumber An external receipt number on the document
	 * @param positions maps to {@link LOSGoodsReceiptPosition}
	 * @throws InventoryException
	 * @throws FacadeException 
	 */
	public void create(
			@WebParam( name="client") String client, 
			@WebParam( name="storageLocation") String storageLocation, 
			@WebParam( name="licencePlate") String licencePlate,
			@WebParam( name="driver") String driver,
			@WebParam( name="deliveryNoteNumber") String deliveryNoteNumber,
			@WebParam( name="forwarder") String forwarder,
			@WebParam( name="positions") GoodsReceiptPositionTO[] positions) throws InventoryException, FacadeException; 
	
	
	public String[] getSuitableAdvice(
			@WebParam( name="client") String client, 
			@WebParam( name="itemData")String itemData,
			@WebParam( name="lot")String lot);
	
}
