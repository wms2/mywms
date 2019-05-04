/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.Remote;

import de.linogistix.los.inventory.businessservice.QueryInventoryTO;
import de.linogistix.los.inventory.exception.InventoryException;


/**
 * Facade for retrieving inventory information from the wms.
 * 
 * @author trautm
 *
 */
@Remote
public interface QueryInventoryFacade{
	
	/**
	 * Returns array of {@link QueryInventoryTO} by unique batch reference.
	 * 
	 * One entry per unique batch/article reference.
	 * 
	 * @param clientRef a reference to the client
	 * @param lotRef a reference to the lot/batch
	 * @return amount/number of pieces in stock
	 */
	QueryInventoryTO getInventoryByLot(String clientRef, String articleRef, String lotRef) throws InventoryException;
	QueryInventoryTO getInventoryByLot(String clientRef, String articleRef, String lotRef, boolean withAmountOnly) throws InventoryException;

	/**
	 * Returns an array of {@link QueryInventoryTO} by unique article reference.
	 * 
	 * One entry per batch if consolidateLot is false. One entry per article otherwise.
	 * 
	 * @param clientRef a reference to the client
	 * @param articleRef a reference to the article
	 * @return amount/number of pieces in stock
	 */
	QueryInventoryTO[] getInventoryByArticle(String clientRef, String articleRef, boolean consolidateLot) throws InventoryException;
	QueryInventoryTO[] getInventoryByArticle(String clientRef, String articleRef, boolean consolidateLot, boolean withAmountOnly) throws InventoryException;

	/**
	 * Gets an inventory List by unique client reference containing all articles.
	 * 
	 * One entry per batch if consolidateLot is false. One entry per article otherwise.
	 * 
	 * @param clientRef a reference to the client
	 * @param articleRef a reference to the article
	 * @return a list of amount/number of pieces in stock for each article
	 */
	QueryInventoryTO[] getInventoryList(String clientRef, boolean consolidateLot) throws InventoryException;
	QueryInventoryTO[] getInventoryList(String clientRef, boolean consolidateLot, boolean withAmountOnly) throws InventoryException;
	
	//--------------------------------------------------------------------------------------
	
	
}
