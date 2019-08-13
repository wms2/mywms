/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.User;

import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.LocationReserver;


// TODO krane i18n
/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishBusinessBean implements LOSReplenishBusiness {
	private final static Logger log = Logger.getLogger(LOSReplenishBusinessBean.class);
	
	@EJB
	private ContextService contextService;
	@EJB
	private InventoryGeneratorService inventoryGenerator;
	@EJB
	private ManageOrderService manageOrderService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private LocationReserver locationReserver;
	@Inject
	private InventoryBusiness inventoryBusiness;

    public LOSReplenishOrder finishOrder(LOSReplenishOrder order) throws FacadeException {
		String logStr = "finishOrder ";
		log.debug(logStr+"orderNumber="+order.getNumber());
		
		int stateOld = order.getState();
		
		if( order.getState()>State.FINISHED) {
			log.error("Finishing of already finished customer order. orderNumber="+order.getNumber()+", state="+order.getState());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_CANNOT_REMOVE_ACTIVE, new Object[]{});
		}
		if( order.getState()==State.FINISHED) {
			log.warn("Finishing of already finished customer order. Ignore. orderNumber="+order.getNumber()+", state="+order.getState());
			return order;
		}

		StockUnit sourceStock = order.getStockUnit();
		
		if( sourceStock != null ) {
			sourceStock.setReservedAmount(BigDecimal.ZERO);
			
			if( order.getDestination() != null ) {
				locationReserver.deallocateLocation(order.getDestination(), sourceStock.getUnitLoad());
			}
		}
		
		order.setState(State.CANCELED);
		
		manageOrderService.onReplenishStateChange(order, stateOld);
    	return order;
    }
    
    
    public void removeOrder(LOSReplenishOrder order) throws FacadeException {
		String logStr = "removeOrder ";
		log.debug(logStr+"orderNumber="+order.getNumber());
		
		if( order.getState() > State.RAW && order.getState() < State.FINISHED ) {
			log.error(logStr+"Removal of active replenish order is not allowed. order="+order.getNumber()+", state="+order.getState());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_CANNOT_REMOVE_ACTIVE, new Object[]{});
		}
		
		// Fix by dbruegmann
	    // reduces reservation
		order.getStockUnit().releaseReservedAmount(order.getRequestedAmount());

		manager.remove(order);
    }
    
	public void startOrder(LOSReplenishOrder order, User user) throws FacadeException {
		int stateOld = order.getState();
		
		if( order.getState() >= State.FINISHED ) {
			log.warn("Order already finished. number="+order.getNumber()+", state="+order.getState());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_ALREADY_FINISHED, new Object[]{});
		}

		if( order.getOperator() != null && !order.getOperator().equals(user) ) {
			log.warn("Order reserved by different user. number="+order.getNumber()+", state="+order.getState()+", operator="+order.getOperator());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_RESERVED, new Object[]{});
		}
		
		order.setOperator(user);
		order.setState(State.STARTED);
		
		manageOrderService.onReplenishStateChange(order, stateOld);

	}
	
	public void resetOrder(LOSReplenishOrder order) throws FacadeException {
		int stateOld = order.getState();

		if( order.getState() >= State.FINISHED ) {
			log.warn("Order already finished. number="+order.getNumber()+", state="+order.getState());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_ALREADY_FINISHED, new Object[]{});
		}
		if( order.getState() < State.PROCESSABLE ) {
			log.warn("Order not released. number="+order.getNumber()+", state="+order.getState());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_NOT_RELEASED, new Object[]{});
		}

		order.setOperator(null);
		order.setState(State.PROCESSABLE);
		
		manageOrderService.onReplenishStateChange(order, stateOld);
	}
	
    public LOSReplenishOrder confirmOrder(LOSReplenishOrder order, StockUnit sourceStock, StorageLocation destinationLocation, BigDecimal amount) throws FacadeException {
		String logStr = "confirmOrder ";
		log.debug(logStr+"orderNumber="+order.getNumber());

		int stateOld = order.getState();
		
		if( order.getState()>=State.FINISHED) {
			log.warn("Order already finished. number="+order.getNumber()+", state="+order.getState());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_ALREADY_FINISHED, new Object[]{});
			
		}

		if( sourceStock == null ) {
			sourceStock = order.getStockUnit();
		}
		if( sourceStock == null ) {
			log.error("Cannot confirm replenish order without source stock. number="+order.getNumber());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_MISSING_SOURCE, new Object[]{});
		}

		if( destinationLocation == null ) {
			destinationLocation = order.getDestination();
		}
		if( destinationLocation == null ) {
			log.error("Cannot confirm replenish order without destination location. number="+order.getNumber());
			throw new InventoryException(InventoryExceptionKey.REPLENISH_MISSING_DESTINATION, new Object[]{});
		}

		sourceStock.setReservedAmount(BigDecimal.ZERO);
		if( order.getStockUnit()!=null ) {
			order.getStockUnit().setReservedAmount(BigDecimal.ZERO);
		}
		
		boolean moveComplete = false;
		if( amount == null || amount.compareTo(sourceStock.getAmount())==0 ) {
			moveComplete = true;
		}
		
		UnitLoad destinationUnitLoad = null;
		List<UnitLoad> unitLoadList = unitLoadService.readByLocation(destinationLocation);
		
		for( UnitLoad ul : unitLoadList ) {
			destinationUnitLoad = ul;
			for( StockUnit su : ul.getStockUnitList() ) {
				if( !su.getItemData().equals(sourceStock.getItemData()) ) {
					destinationUnitLoad = null;
					break;
				}
			}
			if( destinationUnitLoad != null ) {
				break;
			}
		}
		
		UnitLoadType virtual = unitLoadTypeService.getVirtual();
		User operator = contextService.getCallersUser();

		if( destinationUnitLoad == null && moveComplete ) {
			log.debug(logStr+"Move complete source unit load to location. label="+sourceStock.getUnitLoad().getLabelId()+" location="+destinationLocation.getName());
			sourceStock.getUnitLoad().setUnitLoadType(virtual);
			inventoryBusiness.checkTransferUnitLoad(sourceStock.getUnitLoad(), destinationLocation, false);
			inventoryBusiness.transferUnitLoad(sourceStock.getUnitLoad(), destinationLocation, order.getNumber(), operator, null);
		}
		else {
			if (destinationUnitLoad == null) {
				destinationUnitLoad = inventoryBusiness.createUnitLoad(sourceStock.getClient(), null, virtual,
						destinationLocation, StockState.ON_STOCK, order.getNumber(), operator, null);
			}
			if( amount == null ) {
				amount = sourceStock.getAmount();
			}

			inventoryBusiness.transferStock(sourceStock, destinationUnitLoad, amount, StockState.ON_STOCK,
					order.getNumber(), operator, null);
		}
		
		order.setState(State.FINISHED);
		manageOrderService.onReplenishStateChange(order, stateOld);

    	return order;
    }
}
