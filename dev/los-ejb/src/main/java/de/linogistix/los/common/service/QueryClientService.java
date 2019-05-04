/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.Local;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;

@Local
public interface QueryClientService {
	
	/**
     * The system client is the owner of a running warehouse management
     * system and/or the warehouse itself. Users who are assigned to the
     * system client are able to access the system regardless of client
     * partitioning.
     * 
     * @return the unique system client
     */
    Client getSystemClient();
	
	/**
	 * Search for a {@link Client} with the specified number;
	 * For security reasons this will only be allowed for callers who <br> 
	 * - belong to the matching client or <br>
	 * - belong to the system client.
	 * 
	 * @param number
	 * @return matching {@link Client} or NULL if there is none
	 * @throws UnAuthorizedException if the caller is not allowed to see the client.
	 */
	Client getByNumber(String number) throws UnAuthorizedException;
}
