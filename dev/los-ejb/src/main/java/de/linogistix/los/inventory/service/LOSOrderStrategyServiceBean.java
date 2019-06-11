/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.location.StorageLocation;

/**
 * @author krane
 *
 */
@Stateless
public class LOSOrderStrategyServiceBean extends BasicServiceBean<LOSOrderStrategy> implements LOSOrderStrategyService {
	Logger log = Logger.getLogger(LOSOrderStrategyServiceBean.class);

	@EJB
	private ContextService contextService;
	@Inject
	private ClientBusiness clientService;
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSStorageLocationService locService;
	@EJB
	private QueryStorageLocationService locationService;

	public LOSOrderStrategy getByName(Client client, String name) {
		if( client == null ) {
			client = contextService.getCallersClient();
		}
		String queryStr = 
				"SELECT o FROM " + LOSOrderStrategy.class.getSimpleName() + " o " +
				"WHERE o.name=:name and client=:client";
		Query query = manager.createQuery(queryStr);
		query.setParameter("name", name);
		query.setParameter("client", client);
		
		try {
			return (LOSOrderStrategy) query.getSingleResult();
		}
		catch(NoResultException ne){
			if( ! client.isSystemClient() ) {
				return getByName(clientService.getSystemClient(), name);
			}
		}

		return null;
	}

	
	public LOSOrderStrategy getDefault(Client client) {
		
		if( client == null ) {
			client = contextService.getCallersClient();
		}

		String description =null;
		String name = propertyService.getStringDefault(client, null, LOSOrderStrategy.KEY_DEFAULT_STRATEGY, null);
		if( StringTools.isEmpty(name) ) {
			User user = contextService.getCallersUser();
			Locale locale = (user == null || user.getLocale()==null)?Locale.getDefault():new Locale(user.getLocale());
	        name = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyDefaultName", locale);
			description = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyDefaultDesc", locale);
			propertyService.createSystemProperty(clientService.getSystemClient(), null, LOSOrderStrategy.KEY_DEFAULT_STRATEGY, name, LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, description, true);
		}
		
		LOSOrderStrategy strat = getByName(client, name);
		
		if( strat == null ) {
			strat = entityGenerator.generateEntity(LOSOrderStrategy.class);
			
			strat.setName(name);
			strat.setClient(client);
			strat.setManualCreationIndex(0);
			strat.setAdditionalContent(description);
			
			List<StorageLocation> locations = locationService.getListForGoodsOut();
			if( locations.size()==1 ) {
				strat.setDefaultDestination(locations.get(0));
				
			}
			manager.persist(strat);
		}

		return strat;
	}

	public LOSOrderStrategy getExtinguish(Client client) {
		
		if( client == null ) {
			client = contextService.getCallersClient();
		}

		String description =null;
		String name = propertyService.getStringDefault(client, null, LOSOrderStrategy.KEY_EXTINGUISH_STRATEGY, null);
		if( StringTools.isEmpty(name) ) {
			User user = contextService.getCallersUser();
			Locale locale = (user == null || user.getLocale()==null)?Locale.getDefault():new Locale(user.getLocale());
	        name = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyExtinguishName", locale);
			description = BundleHelper.resolve(InventoryBundleResolver.class, "StrategyExtinguishDesc", locale);
			propertyService.createSystemProperty(clientService.getSystemClient(), null, LOSOrderStrategy.KEY_EXTINGUISH_STRATEGY, name, LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, description, true);
		}
		
		LOSOrderStrategy strat = getByName(client, name);
		
		if( strat == null ) {
			strat = entityGenerator.generateEntity(LOSOrderStrategy.class);
			
			strat.setName(name);
			strat.setClient(client);
			strat.setManualCreationIndex(0);
			strat.setAdditionalContent(description);
			
			strat.setCreateFollowUpPicks(false);
			strat.setCreateGoodsOutOrder(false);
			strat.setManualCreationIndex(-1);
			strat.setPreferUnopened(false);
			strat.setUseLockedLot(true);
			strat.setUseLockedStock(true);
			strat.setPreferMatchingStock(false);
			
			strat.setDefaultDestination( locService.getClearing() );
			
			manager.persist(strat);
		}

		return strat;
	}

}
