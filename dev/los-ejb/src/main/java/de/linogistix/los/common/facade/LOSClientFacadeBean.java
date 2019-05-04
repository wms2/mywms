/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSClientFacadeBean implements LOSClientFacade  {
	static Logger log = Logger.getLogger(LOSClientFacadeBean.class);

	@EJB
	private ContextService contextService;
	@EJB
	private ClientService clientService;
    @PersistenceContext(unitName = "myWMS")
    private EntityManager manager;
    
	@SuppressWarnings("unchecked")
	@Override
	public BODTO<Client> getDefaultClient() {
        Query query = manager.createQuery("SELECT client FROM " + Client.class.getSimpleName() + " client WHERE lock=0 ");
        query.setMaxResults(2);
        List<Client> clients = query.getResultList();
        if( clients.size() == 1 ) {
        	Client client = clients.get(0);
        	return new BODTO<Client>(client.getId(), client.getVersion(), client.getNumber());
        }
        
        Client client = contextService.getCallersClient();
        if( !client.isSystemClient() ) {
        	return new BODTO<Client>(client.getId(), client.getVersion(), client.getNumber());
        }
        
        return null;
	}

	@Override
	public BODTO<Client> getUsersClient() {
		Client client = contextService.getCallersClient();
    	return new BODTO<Client>(client.getId(), client.getVersion(), client.getNumber());
	}

	@Override
	public BODTO<Client> getSystemClient() {
		Client client = clientService.getSystemClient();
		if( client == null ) {
			return null;
		}
    	return new BODTO<Client>(client.getId(), client.getVersion(), client.getNumber());
	}
}
