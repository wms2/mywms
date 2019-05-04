/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class QueryClientServiceBean implements QueryClientService {

	@EJB
	private ContextService ctxService;

	@EJB
	private ClientService clientService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.common.service.QueryClientService#getSystemClient()
	 */
	public Client getSystemClient() {
		
		return manager.find(Client.class, new Long(0));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryClientService#getByNumber(java.lang.String)
	 */
	public Client getByNumber(String number) throws UnAuthorizedException {
				
		if( StringTools.isEmpty(ctxService.getCallerUserName()) ){
    		throw new UnAuthorizedException();
    	}
		
		StringBuffer sb = new StringBuffer("SELECT c FROM ");
        sb.append(Client.class.getSimpleName()+ " c ");
        sb.append("WHERE c.number=:number");
        
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("number", number);

        try {
        	Client cl = (Client) query.getSingleResult();
        	
        	if(!ctxService.getCallersClient().equals(clientService.getSystemClient())
        		&& !ctxService.getCallersClient().equals(cl))
        	{
        		throw new UnAuthorizedException();
        	}
        	
        	return cl;
        	
        }
        catch (NoResultException ex) {
            return null;
        }
	}

	

}
