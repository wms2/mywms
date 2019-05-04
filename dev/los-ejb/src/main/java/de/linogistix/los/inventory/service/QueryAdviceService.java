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

import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;


@Local
public interface QueryAdviceService extends BasicService<LOSAdvice>{
	
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
    
    
    /**
     * Searches an advice of the client by the field adviceNumber
     * 
     * @param client. If null no restriction is done.
     * @param number
     * @return
     */
    public LOSAdvice getByAdviceNumber( Client client, String number ) throws UnAuthorizedException;
    
    /**
     * Searches an advice of the client by the field externalNumber
     * 
     * @param client. If null no restriction is done.
     * @param number
     * @return
     */
    public LOSAdvice getByExternalNumber( Client client, String number ) throws UnAuthorizedException;
    
    /**
     * Searches an advice of the client by the field externalId
     * 
     * @param client. If null no restriction is done.
     * @param number
     * @return
     */
    public LOSAdvice getByExternalId( Client client, String number ) throws UnAuthorizedException;
}
