/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;

@Stateless
public class QueryStorageLocationServiceBean 
	implements	QueryStorageLocationService, QueryStorageLocationServiceRemote 
{

	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	ContextService ctxService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	@SuppressWarnings("unchecked")
	public List<StorageLocation> getListForGoodsIn() {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ StorageLocation.class.getSimpleName()+ " sl "
                    + ", " + Area.class.getSimpleName() + " a "
                	+ "WHERE sl.area.id = a.id "
                    + " and a.usages like '%" + AreaUsages.GOODS_IN + "%'");
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
		Query query = manager.createQuery(qstr.toString());

        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StorageLocation>) query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<StorageLocation> getListForGoodsOut() {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ StorageLocation.class.getSimpleName()+ " sl "
                    + ", " + Area.class.getSimpleName() + " a "
                	+ "WHERE sl.area.id = a.id "
                    + " and a.usages like '%" + AreaUsages.GOODS_OUT + "%'");
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
		Query query = manager.createQuery(qstr.toString());

        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StorageLocation>) query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<StorageLocation> getListForStorage() {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ StorageLocation.class.getSimpleName()+ " sl "
                    + ", " + Area.class.getSimpleName() + " a "
                    + "WHERE a.useages like '%" + AreaUsages.STORAGE + "%' AND sl.area.id = a.id ");
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
		Query query = manager.createQuery(qstr.toString());

        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StorageLocation>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryStorageLocationService#getByName(java.lang.String)
	 */
	public StorageLocation getByName(String name) throws UnAuthorizedException {
		
		if( StringTools.isEmpty(ctxService.getCallerUserName()) ){
    		throw new UnAuthorizedException();
    	}
		

		try {
			Query query = manager.createQuery("SELECT o FROM "
					+ StorageLocation.class.getSimpleName() + " o "
					+ "WHERE o.name=:na");
			query.setParameter("na", name);

			StorageLocation sl = (StorageLocation) query.getSingleResult();
			
			if(!ctxService.getCallersClient().equals(queryClientService.getSystemClient())
	        	&& !ctxService.getCallersClient().equals(sl.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			return sl;
			
		} catch (NoResultException ex) {
		}
		
		try {
			Query query = manager.createQuery("SELECT o FROM "
					+ StorageLocation.class.getSimpleName() + " o "
					+ "WHERE o.scanCode=:na");
			query.setParameter("na", name);

			StorageLocation sl = (StorageLocation) query.getSingleResult();
			
			if(!ctxService.getCallersClient().equals(queryClientService.getSystemClient())
	        	&& !ctxService.getCallersClient().equals(sl.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			return sl;
			
		} catch (NoResultException ex) {
		}
		
		return null;
		
	}

}
