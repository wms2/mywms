/*
 * Copyright (c) 2012 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.businessservice.LOSPickingOrderGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingPosGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingStockService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;

@Stateless
public class LOSCompatibilityFacadeBean implements LOSCompatibilityFacade {
	Logger log = Logger.getLogger(LOSCompatibilityFacadeBean.class);
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	@EJB
	private LOSPickingOrderService pickingOrderService;
	@EJB
	private LOSPickingPosGenerator pickingPosGenerator;
	@EJB
	private LOSPickingOrderGenerator pickingOrderGenerator;
	@EJB
	private LOSPickingStockService pickingStockService;
	
	@EJB
	private LOSStorageLocationService locationService;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private InventoryGeneratorService genService;
	
	public void createPickRequests( List<CreatePickRequestPositionTO> chosenStocks ) throws FacadeException {
		String logStr = "createPickRequests ";
		log.debug(logStr);
		
		Map<String,List<LOSPickingPosition>> pickingOrderMap = new HashMap<String, List<LOSPickingPosition>>();
		
		LOSStorageLocation target = null;
		
		for(CreatePickRequestPositionTO posTO:chosenStocks){	
			log.debug( "createPickRequestPosition posTO=" + posTO.orderPosition.getName() + ", amount=" + posTO.amountToPick );

			if( target == null && posTO.targetPlace != null ) {
				target = locationService.getByName( posTO.targetPlace.getName() );
			}
			
			LOSCustomerOrderPosition orderPos = manager.find(LOSCustomerOrderPosition.class, posTO.orderPosition.getId());
			LOSCustomerOrder order = orderPos.getOrder();

			LOSPickingOrder req;

			req = pickingOrderService.getByNumber(posTO.pickRequestNumber);
			if( req != null && req.getState() < State.FINISHED ) {
				throw new InventoryException(InventoryExceptionKey.WRONG_STATE, ""+req.getState());
			}
			
			StockUnit su = manager.find(StockUnit.class, posTO.stock.getId());
log.debug(logStr+" amountReserved="+su.getReservedAmount());
			BigDecimal amount = posTO.amountToPick;
			
			if (su.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0){
				log.error("--- !!! Stock : amount = "+su.getAmount()+" Reserved = "+su.getReservedAmount());
				throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, 
						new String[]{""+su.getAvailableAmount(), su.toUniqueString()});
			}
			
			// The GUI has already reserved the amount. Kill this here to avoid duplicate reservation
			su.releaseReservedAmount(amount);

			LOSPickingPosition pick = pickingPosGenerator.generatePick(amount, su, order.getStrategy(), orderPos);
			
			String pickingOrderNumber = posTO.pickRequestNumber;
			List<LOSPickingPosition> pickList = pickingOrderMap.get(pickingOrderNumber);
			if( pickList == null ) {
				pickList = new ArrayList<LOSPickingPosition>();
				pickingOrderMap.put(pickingOrderNumber, pickList);
			}
			pickList.add(pick);
			
		}
		
		if( pickingOrderMap.size()>0 ) {
			for( String key : pickingOrderMap.keySet() ) {
				List<LOSPickingPosition> pickList = pickingOrderMap.get(key);
				if( pickList.size()>0 ) {
					LOSPickingOrder pickingOrder = pickingOrderGenerator.createSingleOrder(pickList);
					if( pickingOrder != null ) {
						pickingOrder.setNumber(key);
						pickingOrder.setManualCreation(true);
						
						if( target != null ) {
							pickingOrder.setDestination(target);
						}
						
						orderBusiness.releasePickingOrder(pickingOrder);
					}
				}				
			}
			
		}
	}

	
	
	public LOSResultList<LOSOrderStockUnitTO> querySuitableStocksByOrderPosition(BODTO<LOSCustomerOrderPosition> orderPosTO,
			  																	 BODTO<Lot> lotTO,
			  																	 BODTO<LOSStorageLocation> locationTO) 
		throws InventoryException
	{
		
		if(orderPosTO == null){
			return new LOSResultList<LOSOrderStockUnitTO>();
		}
		
		LOSCustomerOrderPosition orderPos = manager.find(LOSCustomerOrderPosition.class, orderPosTO.getId());
		if(orderPos == null){
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ORDERPOSITION, new Object[0]);
		}
		
		Lot lot = null;
		if(lotTO != null){
			lot = manager.find(Lot.class, lotTO.getId());
		}
		
		List<StockUnit> stockList = pickingStockService.getPickFromStockList( orderPos.getClient(), orderPos.getItemData(), lot, null, null, orderPos.getOrder().getStrategy(), true );

		List<LOSOrderStockUnitTO> toList = new ArrayList<LOSOrderStockUnitTO>();
		
		for( StockUnit su : stockList ) {
			LOSOrderStockUnitTO suto = new LOSOrderStockUnitTO(su.getId(), su.getVersion(), su.getLot(), su.getUnitLoad().getLabelId(), ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().getName(), su.getAmount(), su.getReservedAmount());
			toList.add(suto);
		}
		
		return new LOSResultList<LOSOrderStockUnitTO>(toList);
	}
	

	public String getNewPickRequestNumber() {
		
		return genService.generatePickOrderNumber(null, null);
	}
	

}
