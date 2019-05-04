/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface QueryUnitLoadAdviceService extends	BasicService<LOSUnitLoadAdvice> {

	/**
	 * Search for a {@link LOSUnitLoadAdvice} that announces a {@link LOSUnitLoad} with specified label.
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the same client as the {@link LOSUnitLoadAdvice} is assigned to or <br>
	 * - belong to the system client.
	 * 
	 * @param labelId label to search for.
	 * @return matching {@link LOSUnitLoadAdvice} or NULL if there is none.
	 * @throws UnAuthorizedException
	 */
	public LOSUnitLoadAdvice getByLabelId(String labelId) throws UnAuthorizedException;
	
	/**
	 * Search for a {@link LOSUnitLoadAdvice} with specified number.
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the same client as the {@link LOSUnitLoadAdvice} is assigned to or <br>
	 * - belong to the system client.
	 * 
	 * @param adviceNumber the number to search for
	 * @return matching {@link LOSUnitLoadAdvice} or NULL if there is none.
	 * @throws UnAuthorizedException
	 */
	public LOSUnitLoadAdvice getByAdviceNumber(String adviceNumber) throws UnAuthorizedException;
	
}
