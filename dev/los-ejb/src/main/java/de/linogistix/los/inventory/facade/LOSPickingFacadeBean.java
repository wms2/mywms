/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.User;

import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderGenerator;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingOrderLineGenerator;
import de.wms2.mywms.picking.PickingUnitLoad;
import de.wms2.mywms.picking.PickingUnitLoadEntityService;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.user.UserBusiness;

// TODO krane: I18N
/**
 * 
 * @author krane
 */
@Stateless
public class LOSPickingFacadeBean implements LOSPickingFacade {
	Logger log = Logger.getLogger(LOSPickingFacadeBean.class);
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	@EJB
	private ManageOrderService manageOrderService;
	@Inject
	private UserBusiness userService;
	@Inject
	private PickingOrderLineGenerator pickingPosGenerator;
	@Inject
	private PickingOrderGenerator pickingOrderGenerator;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private InventoryGeneratorService sequenceService;
	@EJB
	private ContextService contextService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;

	@Inject
	private PickingUnitLoadEntityService pickingUnitLoadService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private InventoryBusiness inventoryBusiness;

	public void changePickingOrderPrio( long orderId, int prio ) throws FacadeException {
		String logStr = "changePickingOrderPrio ";
		log.debug(logStr+"order="+orderId);

		PickingOrder order = manager.find(PickingOrder.class, orderId);

		int prioOld = order.getPrio();
		if( prio != prioOld ) {
			order.setPrio(prio);
			manageOrderService.onPickingOrderPrioChange(order, prioOld);
		}
	}

	public void changePickingOrderDestination( long orderId, String destinationName ) throws FacadeException {
		String logStr = "changePickingOrderDestination ";
		log.debug(logStr+"order="+orderId);

		PickingOrder order = manager.find(PickingOrder.class, orderId);

		if( !StringTools.isEmpty(destinationName) ) {
			StorageLocation loc = locationService.read(destinationName);
			if( loc != null ) {
				order.setDestination(loc);
			}
			else {
				log.warn(logStr+"Location is unknown. name="+destinationName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Location is unknown. name="+destinationName);
			}
		} 
		else {
			order.setDestination(null);
		}
	}
	
	public void changePickingOrderUser( long orderId, String userName ) throws FacadeException {
		String logStr = "changePickingOrderUser ";
		log.debug(logStr+"order="+orderId);

		PickingOrder order = manager.find(PickingOrder.class, orderId);
		int stateOld = order.getState();
		
		if( order.getState() >= State.STARTED ) {
			log.warn(logStr+"Will not change user of started order. number="+order.getOrderNumber());
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order is already in progress. => Cannot change user.");
		}
		
		if( !StringTools.isEmpty(userName) ) {
			User user = userService.readUser(userName);
			if(user!=null) {
				order.setOperator(user);
				if( order.getState()<State.RESERVED ) {
					order.setState(State.RESERVED);
				}
			}
		}
		else {
			order.setOperator(null);
			if( order.getState()==State.RESERVED ) {
				order.setState(State.PROCESSABLE);
			}
		}
		
		if( order.getState() != stateOld ) {
			manageOrderService.onPickingOrderStateChange(order, stateOld);
		}
	}
	
	public void releaseOrder(long orderId) throws FacadeException {
		String logStr = "releaseOrder ";
		log.debug(logStr+"order="+orderId);
		
		PickingOrder order = manager.find(PickingOrder.class, orderId);
		if( order.getState()>State.PROCESSABLE ) {
			log.error(logStr+"Order is already in progress. => Cannot release.");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order is already in progress. => Cannot release.");
		}
		else if( order.getState()<State.PROCESSABLE ) {
			orderBusiness.releasePickingOrder(order);
		}
	}
	

	public void haltOrder(long orderId) throws FacadeException {
		String logStr = "haltOrder ";
		log.debug(logStr+"order="+orderId);
		
		PickingOrder order = manager.find(PickingOrder.class, orderId);
		
		if( order.getState()>State.RESERVED ) {
			log.error(logStr+"Order is already in progress. => Cannot release.");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order is already in progress. => Cannot halt.");
		}
		else {
			orderBusiness.haltPickingOrder(order);
		}
	}
	
	public void reserveOrder(long orderId, String userName) throws FacadeException {
		String logStr = "reserveOrder ";
		log.debug(logStr+"order="+orderId);
		
		PickingOrder order = manager.find(PickingOrder.class, orderId);
		if( order.getState()>=State.STARTED ) {
			log.error(logStr+"Order is already in progress. => Cannot reserve.");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order is already in progress. => Cannot reserve.");
		}
		
		int stateOld = order.getState();

		User user = null;
		if( userName != null && userName.length()>0 ) {
			user = userService.readUser(userName);
			if (user == null) {
				log.info(logStr + "User not found. name=" + userName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "User not found");
			}
		}
		
		order.setOperator(user);
		if( order.getState()<State.RESERVED ) {
			order.setState(State.RESERVED);
		}
		
		if( order.getState() != stateOld ) {
			manageOrderService.onPickingOrderStateChange(order, stateOld);
		}
	}

	public void resetOrder(long orderId) throws FacadeException {
		PickingOrder order = manager.find(PickingOrder.class, orderId);
		orderBusiness.resetPickingOrder(order);
	}

	public void finishOrder(long orderId) throws FacadeException {
		PickingOrder order = manager.find(PickingOrder.class, orderId);
		orderBusiness.finishPickingOrder(order);
	}

	public void removeOrder(long orderId) throws FacadeException {
		PickingOrder order = manager.find(PickingOrder.class, orderId);
		
		for( PickingOrderLine pick : order.getLines() ) {
			orderBusiness.cancelPick(pick);
			manager.remove(pick);
		}
		for( PickingUnitLoad ul : order.getUnitLoads() ) {
			manager.remove(ul);
		}
		manager.remove(order);
	}
	
	public void createOrders( List<Long> deliveryOrderIdList, int prio, String destinationName, boolean setProcessable, String userName, String comment ) throws FacadeException {
		String logStr = "createOrders ";
		log.debug(logStr);

		StorageLocation destination = null;
		if( destinationName != null && destinationName.length()>0 ) {
			destination = locationService.read(destinationName);
			if( destination == null ) {
				log.info(logStr+"Location not found. name="+destinationName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Location not found");
			}
		}

		User user = null;
		if( userName != null && userName.length()>0 ) {
			user = userService.readUser(userName);
			if (user == null) {
				log.info(logStr + "User not found. name=" + userName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "User not found");
			}
		}

		List<PickingOrderLine> pickList = new ArrayList<PickingOrderLine>();
		OrderStrategy strat = null;
		
		for( Long deliveryOrderId : deliveryOrderIdList ) {
			DeliveryOrder deliveryOrder = manager.find(DeliveryOrder.class, deliveryOrderId);
			if( deliveryOrder == null ) {
				log.error(logStr+"Customer order not found. id="+deliveryOrderId);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order not found");
			}
			if( strat == null ) {
				strat = deliveryOrder.getOrderStrategy();
			}
			if( !strat.equals(deliveryOrder.getOrderStrategy()) ) {
				log.info(logStr+"Orders use different strategies. Cannot build one picking order. strat1=" + strat.getName()+", strat2="+deliveryOrder.getOrderStrategy().getName());
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "User not found");
			}
			if( deliveryOrder.getState() >= State.FINISHED ) {
				log.error(logStr+"Customer order already finshed. id="+deliveryOrderId);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order already finished");
			}

			log.info(logStr+" orderNumber="+deliveryOrder.getOrderNumber());

			if( destination == null ) {
				destination = deliveryOrder.getDestination();
			}
		
		
			if( prio < 0 ) {
				prio = deliveryOrder.getPrio();
			}
		
			List<PickingOrderLine> pickListX;
			pickListX = pickingPosGenerator.generatePicks(deliveryOrder, true);
			pickList.addAll(pickListX);
		}
		
		if( pickList.size()<= 0 ) {
			log.warn(logStr+"No picks created. => No picking order");
			return;
		}
		
		Collection<PickingOrder> pickingOrderList ;
		pickingOrderList = pickingOrderGenerator.generatePickingOrders(pickList);
		
		if (pickingOrderList != null && pickingOrderList.size() > 0) {
			for (PickingOrder pickingOrder : pickingOrderList) {
				pickingOrder.setPrio(prio);
				pickingOrder.setAdditionalContent(comment);
				if (user != null) {
					pickingOrder.setOperator(user);
				}
				if (destination != null) {
					pickingOrder.setDestination(destination);
				}

				if (user != null) {
					orderBusiness.reservePickingOrder(pickingOrder, user, true);
				} else if (setProcessable) {
					orderBusiness.releasePickingOrder(pickingOrder);
				}
			}
		}
	}
	
	public void finishPickingUnitLoad( String label, String locationName ) throws FacadeException {
		String logStr = "finishPickingUnitLoad ";
		log.debug(logStr+"label="+label+", locationName="+locationName);
		
		PickingUnitLoad pickingUnitLoad = pickingUnitLoadService.readFirstByLabel(label);
		if( pickingUnitLoad == null ) {
			log.warn(logStr+"PickingUnitLoad not found. label="+label);
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, label); 
		}

		StorageLocation location = null;
		if( StringTools.isEmpty(locationName) ) {
			if (pickingUnitLoad.getPickingOrder() != null) {
				location = pickingUnitLoad.getPickingOrder().getDestination();
			}
			if( location == null ) {
				location = locationService.getClearing();
			}
		}
		else {
			location = locationService.read(locationName);
		}
		if( location == null ) {
			log.warn(logStr+"Location not found. name="+locationName);
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{locationName}); 
		}
		
		orderBusiness.confirmPickingUnitLoad( pickingUnitLoad, location, State.FINISHED );

	}
	

	public void confirmOrder( long orderId ) throws FacadeException {
		String logStr = "confirmOrder ";
		log.debug(logStr+"id="+orderId);

		PickingOrder order = manager.find(PickingOrder.class, orderId);
		
		// generate pick-to unit load
		UnitLoadType type = unitLoadTypeService.getDefault();
		
		StorageLocation destination = order.getDestination();
		if( destination == null ) {
			List<StorageLocation> slList = locationService.getForGoodsOut(null);
			if (slList.size() == 0) {
				log.warn(logStr+"No goods out location");
				throw new LOSLocationException( LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION, new Object[0] );
			}
			destination = slList.get(0);
		}
		
		UnitLoad ul = inventoryBusiness.createUnitLoad(order.getClient(), null, type, destination, StockState.PICKED, order.getOrderNumber(), null, null);
		PickingUnitLoad pul = pickingUnitLoadService.create(ul);
		pul.setPickingOrder(order);
		pul.setPositionIndex(-1);

		// confirm all picks
		for( PickingOrderLine pick : order.getLines() ) {
			orderBusiness.confirmPick(pick, pul, pick.getAmount(), null, null);
		}
		
		orderBusiness.confirmPickingUnitLoad(pul, destination, State.PICKED);
		orderBusiness.finishPickingOrder(order);

	}
}
