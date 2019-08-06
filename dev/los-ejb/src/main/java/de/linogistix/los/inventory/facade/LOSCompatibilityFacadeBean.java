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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderEntityService;
import de.wms2.mywms.picking.PickingOrderGenerator;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingOrderLineGenerator;
import de.wms2.mywms.picking.PickingStockFinder;

@Stateless
public class LOSCompatibilityFacadeBean implements LOSCompatibilityFacade {
	Logger log = Logger.getLogger(LOSCompatibilityFacadeBean.class);
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	@Inject
	private PickingOrderEntityService pickingOrderService;
	@Inject
	private PickingOrderLineGenerator pickingPosGenerator;
	@Inject
	private PickingOrderGenerator pickingOrderGenerator;
	@Inject
	private PickingStockFinder pickingStockService;
	
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private InventoryGeneratorService genService;
	@Inject
	private StorageLocationEntityService locationService;

	public void createPickRequests( List<CreatePickRequestPositionTO> chosenStocks ) throws FacadeException {
		String logStr = "createPickRequests ";
		log.debug(logStr);
		
		Map<String,List<PickingOrderLine>> pickingOrderMap = new HashMap<String, List<PickingOrderLine>>();
		
		StorageLocation target = null;
		
		for(CreatePickRequestPositionTO posTO:chosenStocks){	
			log.debug( "createPickRequestPosition posTO=" + posTO.orderPosition.getName() + ", amount=" + posTO.amountToPick );

			if( target == null && posTO.targetPlace != null ) {
				target = locationService.read( posTO.targetPlace.getName() );
			}
			
			DeliveryOrderLine orderPos = manager.find(DeliveryOrderLine.class, posTO.orderPosition.getId());
			DeliveryOrder order = orderPos.getDeliveryOrder();

			PickingOrder req;

			req = pickingOrderService.read(posTO.pickRequestNumber);
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

			PickingOrderLine pick;
			pick = pickingPosGenerator.generatePick(orderPos, order.getOrderStrategy(), su, amount, su.getClient(), null, null);
			
			String pickingOrderNumber = posTO.pickRequestNumber;
			List<PickingOrderLine> pickList = pickingOrderMap.get(pickingOrderNumber);
			if( pickList == null ) {
				pickList = new ArrayList<PickingOrderLine>();
				pickingOrderMap.put(pickingOrderNumber, pickList);
			}
			pickList.add(pick);
			
		}
		
		if( pickingOrderMap.size()>0 ) {
			for( String key : pickingOrderMap.keySet() ) {
				List<PickingOrderLine> pickList = pickingOrderMap.get(key);
				if( pickList.size()>0 ) {
					Collection<PickingOrder> pickingOrders = pickingOrderGenerator.generatePickingOrders(pickList);
					int i=1;
					for(PickingOrder pickingOrder:pickingOrders) {
						pickingOrder.setOrderNumber(key);
						if (pickingOrders.size() > 1) {
							pickingOrder.setOrderNumber(key + ":" + i++);
						}
						pickingOrder.setCreateFollowUpPicks(false);
						
						if( target != null ) {
							pickingOrder.setDestination(target);
						}
						
						orderBusiness.releasePickingOrder(pickingOrder);
					}
				}				
			}
			
		}
	}

	
	
	public LOSResultList<LOSOrderStockUnitTO> querySuitableStocksByOrderPosition(BODTO<DeliveryOrderLine> orderPosTO,
			  																	 BODTO<Lot> lotTO,
			  																	 BODTO<StorageLocation> locationTO) 
		throws InventoryException
	{
		
		if(orderPosTO == null){
			return new LOSResultList<LOSOrderStockUnitTO>();
		}
		
		DeliveryOrderLine orderPos = manager.find(DeliveryOrderLine.class, orderPosTO.getId());
		if(orderPos == null){
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ORDERPOSITION, new Object[0]);
		}
		
		Lot lot = null;
		if(lotTO != null){
			lot = manager.find(Lot.class, lotTO.getId());
		}
		
		List<StockUnit> stockList = pickingStockService.findSourceStockList( orderPos.getItemData(), orderPos.getClient(), lot, orderPos.getDeliveryOrder().getOrderStrategy());
		List<LOSOrderStockUnitTO> toList = new ArrayList<LOSOrderStockUnitTO>();
		
		for( StockUnit su : stockList ) {
			LOSOrderStockUnitTO suto = new LOSOrderStockUnitTO(su.getId(), su.getVersion(), su.getLot(), su.getUnitLoad().getLabelId(), su.getUnitLoad().getStorageLocation().getName(), su.getAmount(), su.getReservedAmount());
			toList.add(suto);
		}
		
		return new LOSResultList<LOSOrderStockUnitTO>(toList);
	}
	

	public String getNewPickRequestNumber() {
		
		return genService.generatePickOrderNumber(null, null);
	}
	

}
