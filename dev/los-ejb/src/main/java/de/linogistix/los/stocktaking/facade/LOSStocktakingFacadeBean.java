/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.stocktaking.component.LOSStockTakingProcessComp;
import de.linogistix.los.stocktaking.customization.LOSManageStocktakingService;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;
import de.linogistix.los.stocktaking.exception.LOSStockTakingExceptionKey;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import de.linogistix.los.stocktaking.service.QueryStockTakingOrderService;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class LOSStocktakingFacadeBean implements LOSStocktakingFacade {
	private static final Logger log = Logger.getLogger(LOSStocktakingFacadeBean.class);

	@EJB
	private LOSStockTakingProcessComp stComp;

	@EJB
	private QueryStorageLocationService queryLocationService;
	
	@EJB
	private QueryUnitLoadTypeService queryUltService;
	
	@EJB
	private QueryTypeCapacityConstraintService queryTccService;
	
	@EJB
	private QueryStockTakingOrderService queryStOrderService;
	
	@EJB
	private ContextService contextService;
	
	@EJB
	private ClientService clientService;
	@EJB
	private LOSManageStocktakingService manageService;

	public void acceptOrder( Long orderId ) throws LOSStockTakingException, LOSLocationException, UnAuthorizedException, FacadeException {
		stComp.acceptOrder( orderId );
		
	}

	

	public void recountOrder( Long orderId ) throws LOSStockTakingException {
		stComp.recountOrder( orderId );
		
	}

	public int generateOrders( 
				boolean execute, 
				Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo,
				Date invDate, 
				boolean enableEmptyLocations, boolean enableFullLocations ) {
		
		return stComp.generateOrders(execute, 
				clientId, areaId, zoneId, rackId, locationId, locationName, itemId, itemNo,
				invDate, enableEmptyLocations, enableFullLocations);
		
	}

	public int generateOrders( 
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo,
			Date invDate, 
			boolean enableEmptyLocations, boolean enableFullLocations,
			boolean clientModeLocations, boolean clientModeItemData) {
	
		return stComp.generateOrders(execute, 
				clientId, areaId, zoneId, rackId, locationId, locationName, itemId, itemNo,
				invDate, enableEmptyLocations, enableFullLocations, clientModeLocations, clientModeItemData);
	}

	public void processLocationStart(LOSStorageLocation location)
			throws LOSStockTakingException, UnAuthorizedException {
		stComp.processLocationStart(location);
	}

	public void processLocationEmpty(LOSStorageLocation location) 
			throws UnAuthorizedException, LOSStockTakingException {
		stComp.processLocationEmpty(location);
	}

	public void processLocationCancel(LOSStorageLocation location)
			throws LOSStockTakingException, UnAuthorizedException {
		stComp.processLocationCancel(location);
	}

	public void processLocationFinish(LOSStorageLocation location)
			throws UnAuthorizedException, LOSStockTakingException {
		stComp.processLocationFinish(location);
	}

	
	public void processUnitloadStart(String location, String unitLoadLabel)
			throws LOSStockTakingException, UnAuthorizedException {
		stComp.processUnitloadStart(location, unitLoadLabel);
	}
	
	public void processUnitloadMissing(LOSUnitLoad unitLoad)
			throws UnAuthorizedException, LOSStockTakingException {
		stComp.processUnitloadMissing(unitLoad);
	}


	public void processStockCount(StockUnit stockUnit, BigDecimal countedAmount)
			throws LOSStockTakingException, UnAuthorizedException {
		stComp.processStockCount(stockUnit, countedAmount);
	}

	public void processStockCount(String clientNo, String location,
			String unitLoad, String itemNo, String lotNo, String serialNo,
			BigDecimal countedAmount, String ulType) throws LOSStockTakingException,
			UnAuthorizedException {
		stComp.processStockCount(clientNo, location, unitLoad, itemNo, lotNo, serialNo, countedAmount, ulType);
	}



	public List<String> getUnitloadTypes(String clientNo, String location) throws UnAuthorizedException, LOSStockTakingException {
		
		List<String> ultList = new ArrayList<String>();
		
		LOSStorageLocation sl = queryLocationService.getByName(location);
		
		if(sl == null){
			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.UNKNOWN_LOCATION, 
					new Object[]{location});
		}
		
		if(sl.getCurrentTypeCapacityConstraint() != null){
			ultList.add(sl.getCurrentTypeCapacityConstraint().getUnitLoadType().getName());
			return ultList;
		}
		else {
			
			// check if unit load type already specified through other counts
			List<LOSStocktakingOrder> soList;
			soList = queryStOrderService.getListByLocationAndState(sl.getName(), LOSStocktakingState.STARTED);
			
			String alreadyCountedUnitLoadType = null;
			for(LOSStocktakingOrder so:soList){
				
				for(LOSStocktakingRecord rec : so.getRecords()){
					
					alreadyCountedUnitLoadType = rec.getUlTypeNo();
					
					if(alreadyCountedUnitLoadType != null){
						break;
					}
				}
			}
			
			if(alreadyCountedUnitLoadType != null){
				ultList.add(alreadyCountedUnitLoadType);
				return ultList;
			}
			
			List<LOSTypeCapacityConstraint> tccList = queryTccService.getListByLocationType(sl.getType());
			
			if(tccList.size() == 0){
				ultList.add(queryUltService.getDefaultUnitLoadType().getName());
				return ultList;
			}
			else{
				
				for(LOSTypeCapacityConstraint tcc:tccList){
					ultList.add(tcc.getUnitLoadType().getName());
				}
				return ultList;
			}
			
		}
	}

	public void removeOrder( Long orderId ) throws LOSStockTakingException {
		stComp.removeOrder(orderId);
	}
	
	public Client getDefaultClient() {
		
		// Only one client
		Client systemClient;
		systemClient = clientService.getSystemClient();
		List<Client> clients = clientService.getList(systemClient);
		if( clients.size() == 1 ) {
			log.info("Only one client in system");
			return systemClient;
		}
		
		
		// Callers client not system-client
		Client callersClient = contextService.getCallersClient();
		if( !systemClient.equals(callersClient) ) {
			log.info("Caller is not system-client => only one client to use");
			return callersClient; 
		}
		
		
		log.info("Plenty clients");
		return null;
	}
	
	public String getNextLocation( Client client1, String lastLocation ) {
		return manageService.getNextLocation(client1, lastLocation);
	}
}
