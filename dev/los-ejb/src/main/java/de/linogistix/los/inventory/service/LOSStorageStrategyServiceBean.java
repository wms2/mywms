/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.service;

import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.strategy.StorageStrategy;

/**
 * @author krane
 *
 */
@Stateless
public class LOSStorageStrategyServiceBean extends BasicServiceBean<StorageStrategy> implements LOSStorageStrategyService {
	Logger log = Logger.getLogger(LOSStorageStrategyServiceBean.class);

	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private ContextService contextService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private ClientService clientService;
	
	
	public StorageStrategy getByName( String name ) {

    	String queryStr = "SELECT o FROM " + StorageStrategy.class.getSimpleName() + " o WHERE o.name=:name ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("name", name);

		
		try {
			return (StorageStrategy)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
        
    }


	public StorageStrategy getDefault(){
		return getDefault(null);
	}
	
	public StorageStrategy getDefault(Client client){
		if( client == null ) {
			client = contextService.getCallersClient();
		}

		String description =null;
		String name = propertyService.getStringDefault(client, null, StorageStrategy.PROPERY_KEY_DEFAULT_STRATEGY, null);
		if( StringTools.isEmpty(name) ) {
			User user = contextService.getCallersUser();
			Locale locale = (user == null || user.getLocale()==null)?Locale.getDefault():new Locale(user.getLocale());
	        name = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyStorageDefaultName", locale);
			description = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyStorageDefaultDesc", locale);
			propertyService.createSystemProperty(clientService.getSystemClient(), null, StorageStrategy.PROPERY_KEY_DEFAULT_STRATEGY, name, LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, description);
		}
		
		StorageStrategy strat = getByName(name);
		
		if( strat == null ) {
			strat = entityGenerator.generateEntity(StorageStrategy.class);
			
			strat.setName(name);
			strat.setAdditionalContent(description);
			manager.persist(strat);
		}


		return strat;
	}
	
}
