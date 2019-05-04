/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.query;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.util.businessservice.ContextService;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
@PermitAll
public class ClientQueryBean extends BusinessObjectQueryBean<Client> implements ClientQueryRemote{

	@EJB
	ContextService ctxService;
	
    @Override
    public String getUniqueNameProp() {
        return "number";
    }

    public Client getSystemClient(){
        Client c = manager.find(Client.class, 0L);
        if (c==null){
            throw new NullPointerException("No System Client found");
        } else{
            return c;
        } 
    }
    
    
    /**
     * Returns the client with the specified number ignoring case.
     * 
     * @param clientNumber the number of the clients
     * @return the client with the specified number
     */
    public Client getByNumberIgnoreCase( String clientNumber ) throws UnAuthorizedException {
    	
        Query query = manager.createQuery(
        		" SELECT c FROM " + Client.class.getSimpleName() + " c " +
                " WHERE c.number=:number");
        query.setParameter("number", clientNumber);

        Client client = null;
        try {
            client = (Client) query.getSingleResult();
        }
        catch (NoResultException ex) {
            query = manager.createQuery(
            		" SELECT c FROM " + Client.class.getSimpleName() + " c " +
                    " WHERE LOWER (c.number) = :number");
            query.setParameter("number", clientNumber.toLowerCase());

            try {
                client = (Client) query.getSingleResult();
            }
            catch (NoResultException ex2) { }
        }

        if( client != null ) {
        	Client sysClient = getSystemClient();
        	Client callClient = ctxService.getCallersClient();
        	if( !callClient.equals(sysClient) && !callClient.equals(client) ) {
        		throw new UnAuthorizedException();
        	}
        }
        return client;
    }
    
}
