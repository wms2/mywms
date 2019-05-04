/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class QueryUnitLoadServiceBean 
		implements QueryUnitLoadService, QueryUnitLoadServiceRemote 
{

	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	private ContextService ctxService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadService#getByLabelId(java.lang.String)
	 */
	public LOSUnitLoad getByLabelId(String label) throws UnAuthorizedException {
		
		if( StringTools.isEmpty(ctxService.getCallerUserName()) ){
    		throw new UnAuthorizedException();
    	}
		
		Query query = manager.createNamedQuery("LOSUnitLoad.queryByLabel");
		query = query.setParameter("label", label);

		try {
			LOSUnitLoad ul = (LOSUnitLoad) query.getSingleResult();
			
			if(!ctxService.getCallersClient().equals(queryClientService.getSystemClient())
	        	&& !ctxService.getCallersClient().equals(ul.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			return ul;
			
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadService#getListByLocation(de.linogistix.los.location.model.LOSStorageLocation)
	 */
	@SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListByLocation(LOSStorageLocation sl) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT ul FROM "
                	+ LOSUnitLoad.class.getSimpleName()+ " ul "
                	+ "WHERE ul.storageLocation = :sl");
		
        if (!callersClient.isSystemClient()) {
            qstr.append(" AND ul.client = :cl ");
        }
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("sl", sl);
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<LOSUnitLoad>) query.getResultList();
	}
}
