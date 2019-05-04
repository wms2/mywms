/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;

@Local
public interface QueryGoodsReceiptService {

	/**
	 * Get a {@link LOSGoodsReceipt} by its database identifier.
	 * 
	 * @param id identifier to search for.
	 * @return {@link LOSGoodsReceipt} with specified id.
	 * @throws UnAuthorizedException if caller is not assigned to system client, but to another client than the {@link LOSGoodsReceipt} is.
	 * @throws EntityNotFoundException if there is no {@link LOSGoodsReceipt} for the specified id.
	 */
	public LOSGoodsReceipt getById(long id) throws UnAuthorizedException, EntityNotFoundException;
	
	/**
	 * Search for all {@link LOSGoodsReceipt}s that have one of the specified {@link LOSGoodsReceiptState}s.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all {@link LOSGoodsReceipt}s <br>
	 * - callers of a certain client will get only those {@link LOSGoodsReceipt}s that are also assigned to that client.
	 * 
	 * @param states the {@link LOSGoodsReceiptState}s to search for.
	 * @return {@link List} of {@link LOSGoodsReceipt}s. May be empty, if there are none.
	 */
	public List<LOSGoodsReceipt> getListByStates(LOSGoodsReceiptState ...states);
	
}
