/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.customization;

import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.service.ClientService;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import de.linogistix.los.util.businessservice.ContextService;


public class LOSManageStocktakingServiceBean implements LOSManageStocktakingService {
	private static final Logger log = Logger.getLogger(LOSManageStocktakingServiceBean.class);

	@EJB
	private ContextService contextService;
	
	@EJB
	private ClientService clientService;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	
	
	public String checkLotNo( Client client, ItemData itemData, String lotNo )  throws LOSStockTakingException {
		return lotNo;
	}
	
	public boolean isAllowUseStartedOrder() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public String getNextLocation( Client client1, String lastLocation ) {

		Client client2 = clientService.getSystemClient();
		Client callersClient = contextService.getCallersClient();
		if( !callersClient.isSystemClient() ) {
			client2 = callersClient;
		}
			
		StringBuffer sb = new StringBuffer("SELECT so.locationName FROM ");
		sb.append(LOSStocktakingOrder.class.getSimpleName()+" so, ");
		sb.append(LOSStorageLocation.class.getSimpleName()+" sl ");
		sb.append("WHERE sl.name=so.locationName ");
		sb.append(" AND so.state in (:stateCreated,:stateFree) ");
		sb.append(" AND sl.client in (:cl1,:cl2) ");
		sb.append(" ORDER BY so.locationName ");
		
		Query query = manager.createQuery(sb.toString());
		query.setParameter("stateCreated", LOSStocktakingState.CREATED);		
		query.setParameter("stateFree", LOSStocktakingState.FREE);		
		query.setParameter("cl1", client1);		
		query.setParameter("cl2", client2);

		log.info("getNextLocation: Query="+sb.toString());
		
		List<String> locList = query.getResultList();
		
		if( locList == null || locList.size()==0 ) {
			log.info("Nothing found");
			return null;
		}
		if( lastLocation == null || lastLocation.length() == 0 ) {
			log.info("No offset");
			return locList.get(0);
		}
		log.info("Found "+locList.size()+" orders");
		for( String s : locList ) {
			if( s.compareTo(lastLocation) >= 0 ) {
				log.info("Take location="+s);
				return s;
			}
		}
		log.info("No comparable order found");
		return locList.get(0);

	}
	
}
