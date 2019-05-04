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
import de.linogistix.los.inventory.model.LOSStorageStrategy;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSStorageStrategyServiceBean extends BasicServiceBean<LOSStorageStrategy> implements LOSStorageStrategyService {
	Logger log = Logger.getLogger(LOSStorageStrategyServiceBean.class);

	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private ContextService contextService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private ClientService clientService;
	
	
	public LOSStorageStrategy getByName( String name ) {

    	String queryStr = "SELECT o FROM " + LOSStorageStrategy.class.getSimpleName() + " o WHERE o.name=:name ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("name", name);

		
		try {
			return (LOSStorageStrategy)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
        
    }


	public LOSStorageStrategy getDefault(){
		return getDefault(null);
	}
	
	public LOSStorageStrategy getDefault(Client client){
		if( client == null ) {
			client = contextService.getCallersClient();
		}

		String description =null;
		String name = propertyService.getStringDefault(client, null, LOSStorageStrategy.PROPERY_KEY_DEFAULT_STRATEGY, null);
		if( StringTools.isEmpty(name) ) {
			User user = contextService.getCallersUser();
			Locale locale = (user == null || user.getLocale()==null)?Locale.getDefault():new Locale(user.getLocale());
	        name = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyStorageDefaultName", locale);
			description = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyStorageDefaultDesc", locale);
			propertyService.createSystemProperty(clientService.getSystemClient(), null, LOSStorageStrategy.PROPERY_KEY_DEFAULT_STRATEGY, name, null, description, false, true);
		}
		
		LOSStorageStrategy strat = getByName(name);
		
		if( strat == null ) {
			strat = entityGenerator.generateEntity(LOSStorageStrategy.class);
			
			strat.setName(name);
			strat.setAdditionalContent(description);
			manager.persist(strat);
		}


		return strat;
	}
	
}
