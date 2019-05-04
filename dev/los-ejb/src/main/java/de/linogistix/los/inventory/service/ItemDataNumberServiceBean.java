/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemDataNumber;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class ItemDataNumberServiceBean extends
		BasicServiceBean<ItemDataNumber> implements ItemDataNumberService {
	
	@EJB
	private ContextService ctxService;
	@EJB
	private EntityGenerator entityGenerator;
	
	public ItemDataNumber getByNumber( String number ){
		return getByNumber( null, number );
	}
	
    @SuppressWarnings("unchecked")
	public ItemDataNumber getByNumber(Client client, String number){

    	Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }

    	String queryStr = "SELECT idn FROM " + ItemDataNumber.class.getSimpleName() + " idn WHERE idn.number=:number ";
    	if( client != null ) {
    		queryStr += " AND id.client=:cl";
    	}

        Query query = manager.createQuery( queryStr );

        query.setParameter("number", number);
    	if( client != null ) {
    		query.setParameter("cl", client);
    	}

        List<ItemDataNumber> idnList = null;
        try {
        	idnList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        if( idnList != null && idnList.size() == 1 ) {
        	return idnList.get(0);
        }

        return null;
    }
    
    @SuppressWarnings("unchecked")
	public List<ItemDataNumber> getListByNumber(Client client, String number){
		
		Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }

    	String queryStr = "SELECT idn FROM " + ItemDataNumber.class.getSimpleName() + " idn WHERE idn.number=:number ";
    	if( client != null ) {
    		queryStr += " AND id.client=:cl";
    	}

        Query query = manager.createQuery( queryStr );

        query.setParameter("number", number);
    	if( client != null ) {
    		query.setParameter("cl", client);
    	}

        List<ItemDataNumber> idnList = null;
        try {
        	idnList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return idnList;
    }

	public ItemDataNumber create( ItemData itemData, String number ) throws FacadeException {
		ItemDataNumber idn = entityGenerator.generateEntity(ItemDataNumber.class);
		idn.setItemData(itemData);
		idn.setNumber(number);
		
		manager.persist(idn);
		manager.flush();
		
		return idn;
	}

	@SuppressWarnings("unchecked")
	public List<ItemDataNumber> getListByItemData( ItemData itemData ) {
		
    	String queryStr = "SELECT idn FROM " + ItemDataNumber.class.getSimpleName() + " idn WHERE idn.itemData=:itemData ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("itemData", itemData);

        List<ItemDataNumber> idnList = null;
        try {
        	idnList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return idnList;
	}

}
