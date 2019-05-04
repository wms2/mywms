/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UserService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.businessservice.LOSPickingOrderGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingPosGenerator;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;

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
	@EJB
	private UserService userService;
	@EJB
	private LOSStorageLocationService locationService;
	@EJB
	private LOSPickingPosGenerator pickingPosGenerator;
	@EJB
	private LOSPickingOrderGenerator pickingOrderGenerator;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private LOSPickingUnitLoadService pickingUnitLoadService;
	@EJB
	private InventoryGeneratorService sequenceService;
	@EJB
	private LOSUnitLoadService ulService1;
	@EJB
	private QueryUnitLoadService ulService2;
	@EJB
	private QueryUnitLoadTypeService ultService;
	@EJB
	private ContextService contextService;
	@EJB
	private QueryStorageLocationService slService2;
	
	public void changePickingOrderPrio( long orderId, int prio ) throws FacadeException {
		String logStr = "changePickingOrderPrio ";
		log.debug(logStr+"order="+orderId);

		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);

		int prioOld = order.getPrio();
		if( prio != prioOld ) {
			order.setPrio(prio);
			manageOrderService.onPickingOrderPrioChange(order, prioOld);
		}
	}

	public void changePickingOrderDestination( long orderId, String destinationName ) throws FacadeException {
		String logStr = "changePickingOrderDestination ";
		log.debug(logStr+"order="+orderId);

		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);

		if( !StringTools.isEmpty(destinationName) ) {
			LOSStorageLocation loc = locationService.getByName(destinationName);
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

		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		int stateOld = order.getState();
		
		if( order.getState() >= State.STARTED ) {
			log.warn(logStr+"Will not change user of started order. number="+order.getNumber());
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order is already in progress. => Cannot change user.");
		}
		
		if( !StringTools.isEmpty(userName) ) {
			User user = null;
			try {
				user = userService.getByUsername(userName);
				order.setOperator(user);
				if( order.getState()<State.RESERVED ) {
					order.setState(State.RESERVED);
				}
			} catch (EntityNotFoundException e) {
				log.warn(logStr+"User not found. name="+userName);
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
		
		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
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
		
		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		
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
		
		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		if( order.getState()>=State.STARTED ) {
			log.error(logStr+"Order is already in progress. => Cannot reserve.");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order is already in progress. => Cannot reserve.");
		}
		
		int stateOld = order.getState();

		User user = null;
		if( userName != null && userName.length()>0 ) {
			try {
				user = userService.getByUsername(userName);
			} catch (EntityNotFoundException e) {
				log.info(logStr+"User not found. name="+userName);
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
		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		orderBusiness.resetPickingOrder(order);
	}

	public void finishOrder(long orderId) throws FacadeException {
		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		orderBusiness.finishPickingOrder(order);
	}

	public void removeOrder(long orderId) throws FacadeException {
		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		
		for( LOSPickingPosition pick : order.getPositions() ) {
			orderBusiness.cancelPick(pick);
			manager.remove(pick);
		}
		for( LOSPickingUnitLoad ul : order.getUnitLoads() ) {
			manager.remove(ul);
		}
		manager.remove(order);
	}
	
	
	
	public void createOrders( long customerOrderId, boolean completeOnly, boolean useSingleOrderService, boolean useStratOrderService, int prio, String destinationName, boolean setProcessable, String userName, String comment ) throws FacadeException {
		String logStr = "createOrders ";
		log.debug(logStr);

		LOSCustomerOrder customerOrder = manager.find(LOSCustomerOrder.class, customerOrderId);

		if( customerOrder == null ) {
			log.error(logStr+"Customer order not found. id="+customerOrderId);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order not found");
		}

		if( customerOrder.getState() >= State.FINISHED ) {
			log.error(logStr+"Customer order already finshed. id="+customerOrderId);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order already finished");
		}
		log.info(logStr+" orderNumber="+customerOrder.getNumber()+", useSingleOrderService="+useSingleOrderService+", useStratOrderService="+useStratOrderService);

		LOSStorageLocation destination = null;
		if( destinationName != null && destinationName.length()>0 ) {
			destination = locationService.getByName(destinationName);
			if( destination == null ) {
				log.info(logStr+"Location not found. name="+destinationName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Location not found");
			}
		}
		if( destination == null ) {
			destination = customerOrder.getDestination();
		}
		
		User user = null;
		if( userName != null && userName.length()>0 ) {
			try {
				user = userService.getByUsername(userName);
			} catch (EntityNotFoundException e) {
				log.info(logStr+"User not found. name="+userName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "User not found");
			}
		}
		
		if( prio < 0 ) {
			prio = customerOrder.getPrio();
		}
		
		List<LOSPickingPosition> pickList = pickingPosGenerator.generatePicks(customerOrder, completeOnly);

		if( pickList.size()<= 0 ) {
			log.warn(logStr+"No picks created. => No picking order");
			return;
		}
		
		List<LOSPickingOrder> pickingOrderList = null;
		
		// Assign pick to picking order
		if( useSingleOrderService ) {
			LOSPickingOrder pickingOrder = pickingOrderGenerator.createSingleOrder(pickList);
			pickingOrderList = new ArrayList<LOSPickingOrder>();
			pickingOrderList.add(pickingOrder);
		}
		else if( useStratOrderService ) {
			pickingOrderList = pickingOrderGenerator.createOrders(pickList);
		}
		
		if( pickingOrderList != null && pickingOrderList.size()>0 ) {
			for( LOSPickingOrder pickingOrder : pickingOrderList ) {
				pickingOrder.setManualCreation(false);
				pickingOrder.setPrio(prio);
				pickingOrder.setAdditionalContent(comment);
				if( user != null ) {
					pickingOrder.setOperator(user);
				}
				if( destination != null ) {
					pickingOrder.setDestination(destination);
				}
				
				if( user != null ) {
					orderBusiness.reservePickingOrder(pickingOrder, user, true);
				}
				else if ( setProcessable ) {
					orderBusiness.releasePickingOrder(pickingOrder);
				}
				
			}
		}
			
	}
	public void createOrders( List<Long> customerOrderIdList, boolean completeOnly, int prio, String destinationName, boolean setProcessable, String userName, String comment ) throws FacadeException {
		String logStr = "createOrders ";
		log.debug(logStr);

		LOSStorageLocation destination = null;
		if( destinationName != null && destinationName.length()>0 ) {
			destination = locationService.getByName(destinationName);
			if( destination == null ) {
				log.info(logStr+"Location not found. name="+destinationName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Location not found");
			}
		}

		User user = null;
		if( userName != null && userName.length()>0 ) {
			try {
				user = userService.getByUsername(userName);
			} catch (EntityNotFoundException e) {
				log.info(logStr+"User not found. name="+userName);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "User not found");
			}
		}

		List<LOSPickingPosition> pickList = new ArrayList<LOSPickingPosition>();
		LOSOrderStrategy strat = null;
		
		for( Long customerOrderId : customerOrderIdList ) {
			LOSCustomerOrder customerOrder = manager.find(LOSCustomerOrder.class, customerOrderId);
			if( customerOrder == null && completeOnly ) {
				log.error(logStr+"Customer order not found. id="+customerOrderId);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order not found");
			}
			if( customerOrder == null ) {
				continue;
			}
			if( strat == null ) {
				strat = customerOrder.getStrategy();
			}
			if( !strat.equals(customerOrder.getStrategy()) ) {
				log.info(logStr+"Orders use different strategies. Cannot build one picking order. strat1=" + strat.getName()+", strat2="+customerOrder.getStrategy().getName());
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "User not found");
			}
			if( customerOrder.getState() >= State.FINISHED ) {
				log.error(logStr+"Customer order already finshed. id="+customerOrderId);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "Order already finished");
			}

			log.info(logStr+" orderNumber="+customerOrder.getNumber());

			if( destination == null ) {
				destination = customerOrder.getDestination();
			}
		
		
			if( prio < 0 ) {
				prio = customerOrder.getPrio();
			}
		
			List<LOSPickingPosition> pickListX = pickingPosGenerator.generatePicks(customerOrder, completeOnly);
			pickList.addAll(pickListX);
		}
		
		if( pickList.size()<= 0 ) {
			log.warn(logStr+"No picks created. => No picking order");
			return;
		}
		
		LOSPickingOrder pickingOrder = pickingOrderGenerator.createSingleOrder(pickList);
		
		if( pickingOrder != null ) {
			pickingOrder.setManualCreation(false);
			pickingOrder.setPrio(prio);
			pickingOrder.setAdditionalContent(comment);
			if( user != null ) {
				pickingOrder.setOperator(user);
			}
			if( destination != null ) {
				pickingOrder.setDestination(destination);
			}
			
			if( user != null ) {
				orderBusiness.reservePickingOrder(pickingOrder, user, true);
			}
			else if ( setProcessable ) {
				orderBusiness.releasePickingOrder(pickingOrder);
			}
		}
		
	}
	
	public void finishPickingUnitLoad( String label, String locationName ) throws FacadeException {
		String logStr = "finishPickingUnitLoad ";
		log.debug(logStr+"label="+label+", locationName="+locationName);
		
		LOSPickingUnitLoad pickingUnitLoad = pickingUnitLoadService.getByLabel(label);
		if( pickingUnitLoad == null ) {
			log.warn(logStr+"PickingUnitLoad not found. label="+label);
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, label); 
		}

		LOSStorageLocation location = null;
		if( StringTools.isEmpty(locationName) ) {
			location = pickingUnitLoad.getPickingOrder().getDestination();
			if( location == null ) {
				location = locationService.getClearing();
			}
		}
		else {
			location = locationService.getByName(locationName);
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

		LOSPickingOrder order = manager.find(LOSPickingOrder.class, orderId);
		
		// generate pick-to unit load
		LOSUnitLoad ul = null;
		String label = null;
		for( int i = 0; i < 1000; i++ ) {
			label = sequenceService.generateUnitLoadLabelId(null, null);
			try {
				ul = ulService2.getByLabelId(label);
			} catch (UnAuthorizedException e) {
				continue;
			}
			if( ul == null ) {
				break;
			}
			log.warn(logStr+"Label already exists. Try next. label="+label);
		}
		if( ul != null ) {
			log.warn(logStr+"Cannot create pick-to unit load");
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new Object[]{""}); 
		}
		UnitLoadType type = ultService.getDefaultUnitLoadType();
		Client client = contextService.getCallersClient();
		
		LOSStorageLocation destination = order.getDestination();
		if( destination == null ) {
			List<LOSStorageLocation> slList = slService2.getListForGoodsOut();
			if (slList.size() == 0) {
				log.warn(logStr+"No goods out location");
				throw new LOSLocationException( LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION, new Object[0] );
			}
			destination = slList.get(0);
		}
		
		ul = ulService1.createLOSUnitLoad(client, label, type, destination);
		LOSPickingUnitLoad pul = pickingUnitLoadService.create(order, ul, -1);
		
		
		// confirm all picks
		for( LOSPickingPosition pick : order.getPositions() ) {
			orderBusiness.confirmPick(pick, pul, pick.getAmount(), null, null);
		}
		
		orderBusiness.confirmPickingUnitLoad(pul, destination, State.PICKED);
		orderBusiness.finishPickingOrder(order);

	}
}
