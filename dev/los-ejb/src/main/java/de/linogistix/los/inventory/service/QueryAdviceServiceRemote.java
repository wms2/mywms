/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;


@Remote
public interface QueryAdviceServiceRemote {
	
	/**
	 * Get a {@link LOSAdvice} by its database identifier.
	 * 
	 * @param id identifier to search for.
	 * @return {@link LOSAdvice} with specified id.
	 * @throws UnAuthorizedException if caller is not assigned to system client, but to another client than the {@link LOSGoodsReceipt} is.
	 * @throws EntityNotFoundException if there is no {@link LOSAdvice} for the specified id.
	 */
	public LOSAdvice getById(long id) throws UnAuthorizedException, EntityNotFoundException;
	
	/**
	 * Search for an {@link LOSAdvice} with specified advice number.
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the same client as the advice is assigned to or <br>
	 * - belong to the system client.
	 * 
	 * @param number the number to search for
	 * @return matching advice or NULL if there is none.
	 * @throws UnAuthorizedException 
	 */
    public LOSAdvice getByAdviceNumber(String number) throws UnAuthorizedException;
    
    /**
     * Search for {@link LOSAdvice}s the specified {@link LOSGoodsReceipt} is related to.
     * 
     * @param gr the goods receipt to search for
     * @return list of {@link LOSAdvice}. Might be empty.
     */
    public List<LOSAdvice> getByGoodsReceipt(LOSGoodsReceipt gr);
    
    /**
     * Searches for advice that have one of the specified states 
     * and whose expected delivery date is previous to (new Date() + daysFromNow).
     * For security reasons this will only be allowed for callers who belong to the system client.
     *     
     * @param daysFromNow
     * @param adviceStates
     * @return
     * @throws UnAuthorizedException
     */
    public List<LOSAdvice> getByStateAndDateLimit(int daysFromNow, LOSAdviceState... adviceStates) throws UnAuthorizedException;
    
	public List<LOSAdvice> getListByGoodsReceipCode( LOSGoodsReceipt gr, String code, boolean selectFinished );

}
