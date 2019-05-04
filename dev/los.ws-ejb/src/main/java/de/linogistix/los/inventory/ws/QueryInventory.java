/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import de.linogistix.los.inventory.businessservice.QueryInventoryTO;
import de.linogistix.los.inventory.exception.InventoryException;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * Facade for retrieving inventory information from the wms.
 * 
 * @author trautm
 *
 */
@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface QueryInventory extends java.rmi.Remote {
	
	/**
	 * Returns array of {@link QueryInventoryTO} by unique batch reference.
	 * 
	 * One entry per unique batch/article reference.
	 * @param clientRef a reference to the client
	 * @param lotRef a reference to the batch
	 * 
	 * @return amount/number of pieces in stock
	 */
	@WebMethod
	QueryInventoryTO getInventoryByLot(
            @WebParam(name="clientRef") String clientRef, 
            @WebParam(name="articleRef") String articleRef, 
			@WebParam(name="lotRef") String lotRef) throws InventoryException;
	
	/**
	 * Returns an array of {@link QueryInventoryTO} by unique article reference.
	 * 
	 * One entry per batch if consolidateLot is false. One entry per article otherwise.
	 * @param clientRef a reference to the client
	 * @param articleRef a reference to the article
	 * 
	 * @return amount/number of pieces in stock
	 */
	@WebMethod
	QueryInventoryTO[] getInventoryByArticle(
            @WebParam(name="clientRef") String clientRef, 
            @WebParam(name="articleRef") String articleRef, 
			@WebParam(name="consolidateLot") boolean consolidateLot) throws InventoryException;
	
	/**
	 * Gets an inventory List by unique client reference containing all articles.
	 * 
	 * One entry per batch if consolidateLot is false. One entry per article otherwise.
	 * @param clientRef a reference to the client
	 * @param consolidateLot consolidate all lots of each item
	 * @param withAmountOnly only query items with amount
	 * 
	 * @return a list of amount/number of pieces in stock for each article
	 * @throws InventoryException 
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryAmountList(
            @WebParam(name = "clientRef")  String clientRef, 
            @WebParam(name="consolidateLot") boolean consolidateLot,
        	@WebParam(name="withAmountOnly") boolean withAmountOnly) throws InventoryException;

	//--------------------------------------------------------------------------------------
	
	
}
