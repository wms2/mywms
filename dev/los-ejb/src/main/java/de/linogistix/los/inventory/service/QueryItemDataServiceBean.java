/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;

@Stateless
public class QueryItemDataServiceBean 
	implements QueryItemDataService, QueryItemDataServiceRemote 
{

	@EJB
	private ClientService clientService;
	
	@EJB
	private ContextService ctxService;
	
	@EJB
	private ItemDataNumberService idnService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryItemDataService#getByItemNumber(org.mywms.model.Client, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public ItemData getByItemNumber(Client client, String itemNumber) {
		
		Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }
        
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT id FROM ");
        sb.append(ItemData.class.getSimpleName()+ " id ");
        sb.append("WHERE id.number=:itemNumber ");
        if( client != null ) {
            sb.append(" AND id.client = :cl ");
        }
        
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("itemNumber", itemNumber);
        if( client != null ) {
        	query.setParameter("cl", client);
        }

        List<ItemData> idList = null;
        try {
        	idList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        if( idList != null && idList.size() == 1 ) {
        	return (ItemData)BusinessObjectHelper.eagerRead(idList.get(0));
        }
        else if( idList == null || idList.size() == 0 ) {
        	// Try to find some ItemData with the additional numbers
        	ItemDataNumber idn = idnService.getByNumber(client, itemNumber);
        	if( idn != null ) {
        		return (ItemData)BusinessObjectHelper.eagerRead(idn.getItemData());
        	}
        }
        
    	return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemData> getListByItemNumber(Client client, String itemNumber) {
		
		Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }
        
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT id FROM ");
        sb.append(ItemData.class.getSimpleName()+ " id ");
        sb.append("WHERE id.number=:itemNumber ");
        if( client != null ) {
            sb.append(" AND id.client = :cl ");
        }
        
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("itemNumber", itemNumber);
        if( client != null ) {
        	query.setParameter("cl", client);
        }

        List<ItemData> idList = null;
        try {
        	idList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        if( idList != null && idList.size() > 0 ) {
        	return idList;
        }
        
    	// Try to find some ItemData with the additional numbers
    	List<ItemDataNumber> idnList = idnService.getListByNumber(client, itemNumber);
    	if( idnList != null && idnList.size() > 0 ) {
    		List<ItemData> retList = new ArrayList<ItemData>();
    		for( ItemDataNumber idn : idnList ) {
    			retList.add(idn.getItemData());
    		}
    		return retList;
    	}
    	
    	return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryItemDataService#getByItemNumber(org.mywms.model.Client, java.lang.String)
	 */
//	public ItemData getByItemNumber(Client client, String itemNumber) {
//		
//		StringBuffer sb = new StringBuffer("SELECT id FROM ");
//        sb.append(ItemData.class.getSimpleName()+ " id ");
//        sb.append("WHERE id.number=:itemNumber ");
//        sb.append("AND id.client=:cl");
//        
//        Query query = manager.createQuery(sb.toString());
//        
//        query.setParameter("itemNumber", itemNumber);
//        query.setParameter("cl", client);
//
//        try {
//        	return (ItemData) query.getSingleResult();
//        }
//        catch (NoResultException ex) {
//        	// Try to find some ItemData with the additional numbers
//        	List<ItemDataNumber> idns = idnService.getByNumber(client, itemNumber);
//        	if( idns != null && idns.size() == 1 ) {
//        		return idns.get(0).getItemData();
//        	}
//        	
//            return null;
//            
//        }
//	}
	
	public ItemData getByItemNumber(String itemNumber) {
		return getByItemNumber(null, itemNumber);
		
//		Client callersClient = ctxService.getCallersClient();
//
//		StringBuffer sb = new StringBuffer("SELECT id FROM ");
//        sb.append(ItemData.class.getSimpleName()+ " id ");
//        sb.append("WHERE id.number=:itemNumber ");
//
//        if (!callersClient.isSystemClient()) {
//            sb.append("AND id.client = :cl ");
//        }
//        
//        Query query = manager.createQuery(sb.toString());
//        
//        query.setParameter("itemNumber", itemNumber);
//        if (!callersClient.isSystemClient()) {
//        	query.setParameter("cl", callersClient);
//        }
//
//        try {
//        	return (ItemData) query.getSingleResult();
//        }
//        catch (NoResultException ex) {
//            return null;
//        }
	}

	public List<ItemData> getListByItemNumber(String itemNumber) {
		return getListByItemNumber(null, itemNumber);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryItemDataService#getItemNumbers()
	 */
	@SuppressWarnings("unchecked")
	public List<ClientItemNumberTO> getItemNumbers() throws UnAuthorizedException {
		
		if(ctxService.getCallersUser() == null ||
		   !ctxService.getCallersClient().equals(clientService.getSystemClient()))
		{
			throw new UnAuthorizedException();
		}
		
		StringBuffer sb = new StringBuffer("SELECT new de.linogistix.los.inventory.service.ClientItemNumberTO");
		sb.append("(it.client.number, it.number)");
		sb.append(" FROM "+ItemData.class.getSimpleName()+" it ");
		
		Query query = manager.createQuery(sb.toString());
		
		return query.getResultList();
	}

	

}
