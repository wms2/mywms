/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.linogistix.los.inventory.service.QueryStockService;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class InfoFacadeBean implements InfoFacade {
	Logger log = Logger.getLogger(InfoFacadeBean.class);
	
	@EJB
	private QueryUnitLoadService queryUlService;
	
	@EJB
	private QueryStorageLocationService queryLocationService;
	
	@EJB
	private QueryItemDataService queryItemData;
	
	@EJB
	private QueryStockService queryStock;
	
	@EJB
	private QueryFixedAssignmentService fixService;
	
	@EJB
	private ClientService clientService;
	
	@EJB
	private ContextService contextService;
	@EJB
	private LOSPickingUnitLoadService pickinUnitLoadService;
	@EJB
	private LOSPickingPositionService pickingPositionService;
	@EJB
	private LOSCustomerOrderService customerOrderService;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

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

	
	public InfoItemDataTO readItemData( String itemNumber ) {
		log.info("readItemData itemNumber="+itemNumber);
		ItemData item = null;
		List<ItemData> itemList = queryItemData.getListByItemNumber( itemNumber );
		if( itemList == null || itemList.size() < 1 ) {
			return null;
		}
		item = itemList.get(0);
		
		List<LOSFixedLocationAssignment> fixList = fixService.getByItemData(item);

		List<StockUnit> suList = queryStock.getListByItemData(item, true);
		
		return new InfoItemDataTO(item, fixList, suList);
	}
	
	public InfoLocationTO readLocation( String locationName ) {
		log.info("readLocation locationName="+locationName);
		LOSStorageLocation loc = null;
		try {
			loc = queryLocationService.getByName(locationName);
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( loc == null ) {
			return null;
		}
		
		List<LOSFixedLocationAssignment> fixList = new ArrayList<LOSFixedLocationAssignment>();
		LOSFixedLocationAssignment fix = fixService.getByLocation(loc);
		if( fix != null ) {
			fixList.add(fix);
		}

		InfoLocationTO locto = new InfoLocationTO( loc, fixList );
		if( locto.getNumUnitLoads() == 1 ) {
			readOrder( locto.getUnitLoad(), loc.getUnitLoads().get(0) );
		}
		
		return locto;
	}
	
	public List<InfoStockUnitTO> readStockUnitList( String itemNumber ) {
		log.info("readStockUnitList itemNumber="+itemNumber);
		
		List<InfoStockUnitTO> toList = new ArrayList<InfoStockUnitTO>();
		
		ItemData item = null;
		List<ItemData> itemList = queryItemData.getListByItemNumber( itemNumber );
		if( itemList == null || itemList.size() < 1 ) {
			return toList;
		}
		item = itemList.get(0);
		
		List<StockUnit> suList = queryStock.getListByItemData(item, true);
		if( suList == null || suList.size()<=0 ) {
			return toList;
		}
		
		for( StockUnit stock : suList ) {
			InfoStockUnitTO suto = new InfoStockUnitTO(stock);
			toList.add(suto);
		}
		
		return toList;
	}
	
	public List<InfoUnitLoadTO> readUnitLoadList( String locationName ) {
		log.info("readUnitLoadList locationName="+locationName);
		List<InfoUnitLoadTO> toList = new ArrayList<InfoUnitLoadTO>();
		LOSStorageLocation loc = null;
		try {
			loc = queryLocationService.getByName( locationName );
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( loc == null ) {
			return toList;
		}
		
		for( LOSUnitLoad ul : loc.getUnitLoads() ) {
			InfoUnitLoadTO ulto = new InfoUnitLoadTO(ul);
			readOrder(ulto, ul);
			
			toList.add(ulto);
			
		}
		
		return toList;
	}
	
	
	public InfoUnitLoadTO readUnitLoad( String label ) {
		log.info("readUnitLoad label="+label);
		LOSUnitLoad ul = null;
		try {
			ul = queryUlService.getByLabelId(label);
		} catch (UnAuthorizedException e) {
			// Do nothing
		}
		if( ul == null ) {
			return null;
		}

		InfoUnitLoadTO ulto = new InfoUnitLoadTO(ul);
		readOrder(ulto, ul);
		return ulto;

	}

	private void readOrder( InfoUnitLoadTO ulto, LOSUnitLoad ul ) {
		HashSet<LOSCustomerOrder> orderSetUl = new HashSet<LOSCustomerOrder>();
		HashSet<LOSCustomerOrder> pickSetUl = new HashSet<LOSCustomerOrder>();
		LOSCustomerOrder customerOrder = null;
		
		LOSPickingUnitLoad pul = pickinUnitLoadService.getByLabel(ul.getLabelId());
		if( pul != null ) {
			if( pul.getCustomerOrderNumber() != null ) {
				customerOrder = customerOrderService.getByNumber(pul.getCustomerOrderNumber());
				orderSetUl.add( customerOrder );
			}
		}
		
		for( StockUnit su : ul.getStockUnitList() ) {
			HashSet<LOSCustomerOrder> orderSetSu = new HashSet<LOSCustomerOrder>();
			HashSet<LOSCustomerOrder> pickSetSu = new HashSet<LOSCustomerOrder>();
			
			if( customerOrder != null ) {
				orderSetSu.add( customerOrder );
			}
			else {
				List<LOSPickingPosition> pickList = pickingPositionService.getByPickFromStockUnit(su);
				for( LOSPickingPosition pick : pickList ) {
					if( pick.getState() < State.PICKED ) {
						LOSCustomerOrderPosition orderPos = pick.getCustomerOrderPosition();
						if( orderPos != null ) {
							LOSCustomerOrder order = orderPos.getOrder();
							pickSetSu.add( order );
							pickSetUl.add( order );
						}
					}
				}
			}
			
			InfoStockUnitTO suto = new InfoStockUnitTO( su ) ;
			
			for( LOSCustomerOrder order : orderSetSu ) {
				suto.getOrderList().add( new InfoOrderTO(order) );
			}
			for( LOSCustomerOrder order : pickSetSu ) {
				suto.getPickList().add( new InfoOrderTO(order) );
			}
			
			ulto.getStockUnitList().add(suto);
		}

		for( LOSCustomerOrder order : orderSetUl ) {
			ulto.getOrderList().add( new InfoOrderTO(order) );
		}
		for( LOSCustomerOrder order : pickSetUl ) {
			ulto.getPickList().add( new InfoOrderTO(order) );
		}
		
	}

}
