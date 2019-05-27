/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.User;

import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.inventory.service.LOSStockUnitRecordService;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 * 
 * @author krane
 */
@Stateless
public class LOSOrderBusinessBean implements LOSOrderBusiness {
	private Logger log = Logger.getLogger(this.getClass());
	@EJB
	private LOSPickingPositionService pickingPositionService;
	@EJB
	private LOSGoodsOutGenerator goodsOutGenerator;
	@EJB
	private ContextService contextService;
	@EJB
	private LOSPickingOrderGenerator pickingOrderGeneratorService;
	@EJB
	private LOSPickingPosGenerator pickingPosGeneratorService;
	
	@EJB
	private LOSStorage storage;
	@EJB
	private LOSPickingUnitLoadService pickingUnitLoadService;
	@EJB
	private LOSCustomerOrderService customerOrderService;

	@EJB
	private LOSStorageLocationService locationService;
	@EJB
	private LOSStockUnitRecordService recordService;
	@EJB
	private LOSInventoryComponent invComponent;
	@EJB
	private ManageOrderService manageOrderService;
	
    public LOSCustomerOrder finishCustomerOrder(LOSCustomerOrder customerOrder) throws FacadeException {
		String logStr = "finishCustomerOrder ";
		log.debug(logStr+"orderNumber="+customerOrder.getNumber());
		
		if( customerOrder.getState()>State.FINISHED ) {
			log.error(logStr+"Finishing of already finished customer order. orderNumber="+customerOrder.getNumber()+", state="+customerOrder.getState());
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, "");
		}
		if( customerOrder.getState()==State.FINISHED ) {
			log.warn(logStr+"Finishing of already finished customer order. Ignore. orderNumber="+customerOrder.getNumber()+", state="+customerOrder.getState());
			return customerOrder;
		}

		int stateOld = customerOrder.getState();
		
		boolean hasOnlyCanceledPos = true;
		for( LOSCustomerOrderPosition pos : customerOrder.getPositions() ) {
			int posStateOld = pos.getState(); 
			if( posStateOld < State.FINISHED ) {
				if( BigDecimal.ZERO.compareTo(pos.getAmountPicked())<0 ) {
					pos.setState(State.FINISHED);
				}
				else {
					pos.setState(State.CANCELED);
				}
				manageOrderService.onCustomerOrderPositionStateChange(pos, posStateOld);
			}
			if( pos.getState() != State.CANCELED ) {
				hasOnlyCanceledPos = false;
			}
		}
		if( hasOnlyCanceledPos ) {
			customerOrder.setState(State.CANCELED);
		}
		else {
			customerOrder.setState(State.FINISHED);
		}

		if( customerOrder.getState() != stateOld ) {
			manageOrderService.onCustomerOrderStateChange(customerOrder, stateOld);
		}
		
    	return customerOrder;
    }
    
	public LOSCustomerOrderPosition confirmCustomerOrderPos(LOSCustomerOrderPosition customerOrderPos, BigDecimal amount) throws FacadeException {
		String logStr = "confirmCustomerOrderPos ";
		log.debug(logStr);

		if( amount == null || BigDecimal.ZERO.compareTo(amount)>0 ) {
			log.error(logStr+"No valid amount. Abort. amount="+amount+customerOrderPos.getNumber()+", state="+customerOrderPos.getState());
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_INVALID_AMOUNT, amount==null?"NULL":amount.toString());
		}

		int stateOld = customerOrderPos.getState();
		BigDecimal amountRequested = customerOrderPos.getAmount();
		BigDecimal amountPicked = customerOrderPos.getAmountPicked();
		amountPicked = amountPicked.add(amount);
		
		customerOrderPos.setAmountPicked(amountPicked);

		if( customerOrderPos.getState()>=State.PICKED ) {
			log.warn(logStr+"Added picked amount to already finished customer order position. position="+customerOrderPos.getNumber()+", state="+customerOrderPos.getState()+", added amount="+amount+", new picked amount="+amountPicked);
		}

		if( amountRequested.compareTo(amountPicked) <= 0 ) {
			if( customerOrderPos.getState()<State.PICKED ) {
				customerOrderPos.setState(State.PICKED);
			}
		}
		else {
			// Find open picks. If no more picks are available, the state is set to PENDING
			boolean hasOpenPicks = false;
			List<LOSPickingPosition> pickList = pickingPositionService.getByCustomerOrderPosition(customerOrderPos);
			for( LOSPickingPosition pick : pickList ) {
				if( pick.getState() < State.PICKED ) {
					hasOpenPicks = true;
					break;
				}
			}
			if( customerOrderPos.getState()<State.PICKED ) {
				if( hasOpenPicks ) {
					customerOrderPos.setState(State.STARTED);
				}
				else {
					customerOrderPos.setState(State.PENDING);
				}
			}
		}
		
		LOSCustomerOrder customerOrder = customerOrderPos.getOrder();
		int orderStateOld = customerOrder.getState();
		if( customerOrder.getState()<State.STARTED ) {
			customerOrder.setState(State.STARTED);

		}
		
		if( customerOrderPos.getState() != stateOld )  {
			manageOrderService.onCustomerOrderPositionStateChange(customerOrderPos, stateOld);
		}

		if( customerOrderPos.getState()>=State.PENDING && customerOrder.getState()<State.PICKED ) {
			// Check state of order
			boolean hasAllPicked = true;
			boolean hasPendingPicks = false;
			for( LOSCustomerOrderPosition cop : customerOrder.getPositions() ) {
				if( cop.getState() == State.PENDING ) {
					hasPendingPicks = true;
				}
				if( cop.getState()<State.PENDING ) {
					hasAllPicked = false;
					break;
				}
			}

			if( hasAllPicked ) {
				if( hasPendingPicks ) {
					log.info(logStr+"Found pending picks for order. number="+customerOrder.getNumber());
					
					customerOrder.setState(State.PENDING);
					
				}
				else {
					log.info(logStr+"Everything picked for order. Confirm order. number="+customerOrder.getNumber());
					customerOrder.setState(State.PICKED);

				}
			}
		}
		
		if( customerOrder.getState() != orderStateOld )  {
			manageOrderService.onCustomerOrderStateChange(customerOrder, stateOld);
		}

		return customerOrderPos;
	}
	
	public LOSPickingOrder releasePickingOrder(LOSPickingOrder pickingOrder) throws FacadeException {
		String logStr = "releasePickingOrder ";
		log.debug(logStr+"order="+pickingOrder);
		
		if( pickingOrder == null ) {
			log.error(logStr+"missing parameter order");
			return null;
		}
		
		int stateOld = pickingOrder.getState();
		
		if( stateOld>=State.PICKED ) {
			log.error(logStr+"Order is already picked. => Cannot release.");
			throw new InventoryException(InventoryExceptionKey.PICK_ALREADY_FINISHED, "");
		}
		
		if( stateOld>=State.PROCESSABLE ) {
			log.warn(logStr+"Order is already released. => Do nothing.");
			return pickingOrder;
		}

		if( !manageOrderService.isPickingOrderReleasable(pickingOrder) ) {
			log.info(logStr+"Order must not be released. => Do nothing.");
			return pickingOrder;
		}
		
		pickingOrder.setState(State.PROCESSABLE);
		
		manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);

		return pickingOrder;
	}
	
	public LOSPickingOrder haltPickingOrder(LOSPickingOrder pickingOrder) throws FacadeException {
		String logStr = "haltPickingOrder ";
		log.debug(logStr+"order="+pickingOrder);
		
		if( pickingOrder == null ) {
			log.error(logStr+"missing parameter order");
			return null;
		}
		
		int stateOld = pickingOrder.getState();

		if( pickingOrder.getState()>State.RESERVED ) {
			log.error(logStr+"Order is already in progress. Cannot halt");
			throw new InventoryException(InventoryExceptionKey.PICK_ALREADY_STARTED, "");
		}
		
		pickingOrder.setState(State.RAW);
		pickingOrder.setOperator(null);
		
		if( pickingOrder.getState() != stateOld )  {
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}

		return pickingOrder;
	}
	
	/**
	 * Reserves the picking order for the given user.<br>
	 * If reservation is not possible, an exception is thrown.
	 * 
	 * @param pickingOrder
	 * @param user
	 * @param force If TRUE, a reservation for a different user will be ignored
	 * @return
	 * @throws FacadeException
	 */
	public LOSPickingOrder reservePickingOrder(LOSPickingOrder pickingOrder, User user, boolean ignoreReservationGap) throws FacadeException {
		String logStr = "reservePickingOrder ";
		log.debug(logStr+"order="+pickingOrder+", ignoreReservationGap="+ignoreReservationGap);
		
		if( pickingOrder == null ) {
			log.error(logStr+"missing parameter order");
			return null;
		}
		
		int stateOld = pickingOrder.getState();

		if( stateOld>=State.PICKED ) {
			log.error(logStr+"Order is already picked. => Cannot reserve.");
			throw new InventoryException(InventoryExceptionKey.PICK_ALREADY_STARTED, pickingOrder.getNumber());
		}
		if( user == null ) {
			user = contextService.getCallersUser();
		}
		if( !ignoreReservationGap && (stateOld>=State.RESERVED) && (pickingOrder.getOperator() != null) && (!pickingOrder.getOperator().equals(user)) ) {
			log.error(logStr+"Order is already assigned to a different user. => Cannot reserve.");
			throw new InventoryException(InventoryExceptionKey.ORDER_RESERVED, "");
		}

		pickingOrder.setOperator(user);
		if( stateOld<State.PROCESSABLE ) {
			if( manageOrderService.isPickingOrderReleasable(pickingOrder) ) {
				pickingOrder.setState(State.RESERVED);
			}
			else {
				log.info(logStr+"Order must not be released.");
			}
		}
		else if( stateOld<State.RESERVED) {
			pickingOrder.setState(State.RESERVED);
		}

		if( pickingOrder.getState() != stateOld )  {
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}

		return pickingOrder;
	}

	public LOSPickingOrder startPickingOrder(LOSPickingOrder pickingOrder, boolean ignoreReservationGap) throws FacadeException {
		String logStr = "startPickingOrder ";
		log.debug(logStr+"order="+pickingOrder);

		if( pickingOrder == null ) {
			log.error(logStr+"missing parameter order");
			return null;
		}
		
		int stateOld = pickingOrder.getState();

		if( pickingOrder.getState()>=State.PICKED) {
			log.error(logStr+"Order is already picked. => Cannot start.");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, "");
		}
		User user = contextService.getCallersUser();
		if( !ignoreReservationGap && (pickingOrder.getState()>=State.RESERVED) && (pickingOrder.getOperator() != null) && (!pickingOrder.getOperator().equals(user)) ) {
			log.error(logStr+"Order is already assigned to a different user. => Cannot reserve.");
			throw new InventoryException(InventoryExceptionKey.ORDER_RESERVED, "");
		}
		pickingOrder.setOperator(user);
		if( pickingOrder.getState()<State.STARTED) {
			pickingOrder.setState(State.STARTED);
		}
		
		if( pickingOrder.getState() != stateOld )  {
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}

		return pickingOrder;
	}
	
	public LOSPickingOrder resetPickingOrder(LOSPickingOrder pickingOrder) throws FacadeException {
		String logStr = "resetPickingOrder ";
		log.debug(logStr+"order="+pickingOrder);
		
		if( pickingOrder == null ) {
			log.error(logStr+"missing parameter order");
			return null;
		}
		int stateOld = pickingOrder.getState();

		if( pickingOrder.getState()>State.PICKED ) {
			log.error(logStr+"Order is already picked. => Cannot reset.");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, "");
		}
		
		boolean hasFinishedPicks = false;
		boolean hasOpenPicks = false;
		for( LOSPickingPosition pick : pickingOrder.getPositions() ) {
			int pickStateOld = pick.getState(); 
			if( pickStateOld >= State.PICKED ) {
				hasFinishedPicks = true;
				// do not change already picked positions
				continue;
			}
			hasOpenPicks = true;
			if( pickStateOld <= State.PROCESSABLE) {
				// do not change already reseted positions
				continue;
			}
			
			pick.setState(State.PROCESSABLE);
			manageOrderService.onPickingPositionStateChange(pick, pickStateOld);
		}
		List<LOSPickingPosition> openPickList = new ArrayList<LOSPickingPosition>();
		
		if( hasFinishedPicks ) {
			if( hasOpenPicks ) {
				// Remove open picks from the order
				// Set them back to the pool to generate a new picking order
				for( LOSPickingPosition pick : pickingOrder.getPositions() ) {
					int pickStateOld = pick.getState(); 
					if( pickStateOld >= State.PICKED ) {
						continue;
					}
					pick.setPickingOrder(null);
					if( pickStateOld >= State.PROCESSABLE) {
						pick.setState(State.ASSIGNED);
						manageOrderService.onPickingPositionStateChange(pick, pickStateOld);
						openPickList.add(pick);
					}
				}
			}
			if( pickingOrder.getState()<State.FINISHED ) {
				pickingOrder.setState(State.FINISHED);
			}
		}
		else if( hasOpenPicks ) {
			if( pickingOrder.getState()>State.PROCESSABLE ) {
				pickingOrder.setState(State.PROCESSABLE);
				pickingOrder.setOperator(null);
			}
		}
		else {
			if( pickingOrder.getState()<State.FINISHED ) {
				pickingOrder.setState(State.CANCELED);
			}
		}
		
		if( openPickList.size()>0 ) {
			log.debug(logStr+"Create new picking order");
			
			// Take prefix of number to the new generated picking order.
			// This only works for basic, not configurable picking order numbers
			String numberOld = pickingOrder.getNumber();
			String prefix = "";
			int idx = numberOld.lastIndexOf("_PICK ");
			if( idx>0 && idx<numberOld.length() ) {
				prefix=numberOld.substring(0, idx)+"_";
			}
			
			List<LOSPickingOrder> newOrderList = pickingOrderGeneratorService.createOrders(openPickList);
			for( LOSPickingOrder newOrder : newOrderList ) {
				newOrder.setNumber(prefix+newOrder.getNumber());
				releasePickingOrder(newOrder);
			}
		}
		
		if( pickingOrder.getState() != stateOld )  {
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}

		checkCreateGoodsOutOrder(pickingOrder);
		
		return pickingOrder;
	}
	/**
	 * Finishes a picking order in the current state.<br>
	 * Not finished unit loads of the picking order are moved to the clearing location.<br> 
	 * All not picked positions are moved back to the pool. 
	 * 
	 * @param pickingOrder
	 * @throws FacadeException
	 */
	public LOSPickingOrder finishPickingOrder(LOSPickingOrder pickingOrder) throws FacadeException {
		String logStr = "finishPickingOrder ";
		log.debug(logStr+"order="+pickingOrder);

		if( pickingOrder == null ) {
			log.error(logStr+"missing parameter order");
			return null;
		}
		int stateOld = pickingOrder.getState();

		if( pickingOrder.getState() >= State.FINISHED ) {
			log.error(logStr+"Order is already finished. => Cannot finish.");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, "");
		}
		
		int orderState = State.CANCELED;
		for( LOSPickingPosition pick : pickingOrder.getPositions() ) {
			if( pick.getState()<State.PICKED ) {
				cancelPick(pick);
			}
			if( pick.getState() >= State.PICKED && pick.getState() != State.CANCELED ) {
				orderState = State.FINISHED;
			}
		}
		
		pickingOrder.setState(orderState);

		// cleanup unit loads
		// Not finished unit loads on the users location are moved to CLEARING
		User user = pickingOrder.getOperator();
		if( user != null ) {
			List<LOSPickingUnitLoad> ulList = pickingUnitLoadService.getByPickingOrder(pickingOrder);
			if( ulList != null && ulList.size()>0 ) {
				log.debug(logStr+"Cleanup unit loads on users location. userName="+user.getName());
				String userName = pickingOrder.getOperator() == null ? null : pickingOrder.getOperator().getName();
				StorageLocation usersLocation = locationService.getCurrentUsersLocation();
				for( LOSPickingUnitLoad unitLoad : ulList ) {
					if( unitLoad.getUnitLoad().getStorageLocation().equals(usersLocation) ) {
						StorageLocation clearing = locationService.getClearing();
						storage.transferUnitLoad(userName, clearing, unitLoad.getUnitLoad(), -1, true, "", "");
					}
				}
			}
		}		

		if( pickingOrder.getState() != stateOld )  {
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}
		
		checkCreateGoodsOutOrder(pickingOrder);

		return pickingOrder;
	}
	
	private void checkCreateGoodsOutOrder( LOSPickingOrder pickingOrder ) throws FacadeException {
		String logStr = "checkCreateGoodsOutOrder ";
		log.debug(logStr+"order="+pickingOrder);
		
		String customerOrderNumber = pickingOrder.getCustomerOrderNumber();
		if( StringTools.isEmpty(customerOrderNumber) ) {
			log.debug(logStr+"No customer order set picking order. => No goods out order. picking order="+pickingOrder.getNumber());
			return;
		}

		LOSCustomerOrder customerOrder = customerOrderService.getByNumber(customerOrderNumber);
		if( customerOrder == null ) {
			log.warn(logStr+"Cannot create goods out order without customer order. PickingOrder="+pickingOrder.getNumber());
			return;
		}

		int customerOrderStateOld = customerOrder.getState(); 
		boolean goodsOutOrderCreated = false;
		if( pickingOrder.getStrategy().isCreateGoodsOutOrder() && customerOrderStateOld >= State.PICKED ) {
			log.debug(logStr+"Create goods out order for customer order="+customerOrderNumber+", strategy="+pickingOrder.getStrategy().getName());
			goodsOutOrderCreated = (goodsOutGenerator.createOrder(customerOrder) != null);
		}

		if( goodsOutOrderCreated ) {
			return;
		}
		
		log.debug(logStr+"No goods out order for customer order="+customerOrderNumber+", strategy="+pickingOrder.getStrategy().getName());
		if( customerOrderStateOld >= State.PICKED && customerOrderStateOld<State.FINISHED ) {
			customerOrder.setState(State.FINISHED);
			manageOrderService.onCustomerOrderStateChange(customerOrder, customerOrderStateOld);
		}
		
	}
	
	
	public void confirmPick(LOSPickingPosition pick, LOSPickingUnitLoad pickToUnitLoad, BigDecimal amountPicked, BigDecimal amountRemain, List<String> serialNoList) throws FacadeException {
		confirmPick(pick, pickToUnitLoad, amountPicked, amountRemain, serialNoList, false);
	}
	
	public void confirmPick(LOSPickingPosition pick, LOSPickingUnitLoad pickToUnitLoad, BigDecimal amountPicked, BigDecimal amountRemain, List<String> serialNoList, boolean counted) throws FacadeException {
		String logStr = "confirmPick ";
		if( pick == null ) {
			log.error(logStr+"missing parameter pick");
			return;
		}
		log.debug(logStr+"pick.id="+pick.getId()+", item="+pick.getItemData().getNumber()+", amount="+amountPicked+", remain="+amountRemain+", unitLoad="+pickToUnitLoad);
		int stateOld = pick.getState();

		if( pick.getState()>=State.PICKED ) {
			log.error(logStr+"Pick already done. => Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_ALREADY_FINISHED, "");
		}
		if( pick.getPickFromStockUnit() == null ) {
			log.error(logStr+"Pick has no assigned stock. => Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_NO_STOCK, "");
		}
		if( amountPicked == null ) {
			amountPicked = pick.getAmount();
		}
		if( BigDecimal.ZERO.compareTo(amountPicked) > 0 ) {
			log.error(logStr+"Impossible to pick negative amount. Abort");
			throw new InventoryException(InventoryExceptionKey.AMOUNT_MUST_BE_GREATER_THAN_ZERO, "");
		}
		if( BigDecimal.ZERO.compareTo(amountPicked) >= 0 && amountRemain == null ) {
			log.error(logStr+"Nothing picked and nothing counted. Impossible. Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_NOT_PICKED_COUNTED, "");
		}

		LOSPickingOrder pickingOrder = pick.getPickingOrder();
		if( pickingOrder == null ) {
			log.error(logStr+"Cannot confirm without order. Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_MISSING_ORDER, "");
		}
		
		if( pickToUnitLoad == null && BigDecimal.ZERO.compareTo(amountPicked)<0 ) {
			log.error(logStr+"Cannot confirm without unit load. Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_MISSING_UNITLOALD, "");
		}

		if( pickToUnitLoad != null ) {
			if( !pickToUnitLoad.getPickingOrder().equals(pickingOrder) ) {
				log.error(logStr+"Wrong unit load picking order number. Abort. Actual="+pickToUnitLoad.getPickingOrder().getNumber()+", Requested="+pickingOrder.getNumber());
				throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_WRONG_UNITLOAD, new Object[]{pickToUnitLoad.getPickingOrder().getNumber(), pickingOrder.getNumber()});
			}
			if( pick.getCustomerOrderPosition() != null ) {
				String customerOrderNumber = pick.getCustomerOrderPosition().getOrder().getNumber();
				if( StringTools.isEmpty(pickToUnitLoad.getCustomerOrderNumber()) ) {
					pickToUnitLoad.setCustomerOrderNumber(customerOrderNumber);
				}
				else if( !pickToUnitLoad.getCustomerOrderNumber().equals(customerOrderNumber) ) {
					pickToUnitLoad.setCustomerOrderNumber("-");
				}
			}
		}

		String activityCode = pickingOrder.getNumber();
		StockUnit pickFromStock = pick.getPickFromStockUnit();
		BigDecimal amountPickFrom = pickFromStock.getAmount();
		Lot lotPicked = pickFromStock.getLot();
		
		// Release the reservation on pick from stock unit. 
		// Maybe that some services would fail on the original reservations
		pickFromStock.releaseReservedAmount(pick.getAmount());

		// Check amount differences
		if( amountRemain == null ) {
			if( amountPicked.compareTo(amountPickFrom) > 0 ) {
				log.info(logStr+"More stock picked as available. Adjust amount. Picked="+amountPicked+", Available="+amountPickFrom);
				changeAmount(pickFromStock, amountPicked, activityCode);
			}
		}
		else {
			
			UnitLoad pickFromUnitLoad = pickFromStock.getUnitLoad();
			StorageLocation pickFromLocation = pickFromUnitLoad.getStorageLocation();
			
			if( BigDecimal.ZERO.compareTo(amountRemain) > 0 ) {
				amountRemain = BigDecimal.ZERO;
			}
			BigDecimal amountStart = amountRemain.add(amountPicked);
			log.info(logStr+"Stock has been counted. Remaining="+amountRemain+", Start="+amountStart);
			if( amountStart.compareTo(amountPickFrom) > 0 ) {
				// There is more stock than expected on the pick from stock unit
				// Adjust it to the correct value
				log.info(logStr+"Adjust amount before picking. Desired="+amountPickFrom+", Counted="+amountStart);
				changeAmount(pickFromStock, amountStart, activityCode);
				amountPickFrom = amountStart;
			}
			else if( amountStart.compareTo(amountPickFrom) < 0 ) {
				// Some amount on pick from stock unit is missing
				// Adjust it to the correct value
				log.info(logStr+"Missing amout. Adjust before picking. Desired="+amountPickFrom+", Counted="+amountStart);
				
				// Remove temporal from follow up pick generation
				pickFromStock.setReservedAmount(pickFromStock.getAmount());
				
				// Maybe other picking positions are affected
				// If the pick from stock is going to zero, other affected picks are reseted
				if( BigDecimal.ZERO.compareTo(amountRemain) == 0 ) {
					List<LOSPickingPosition> affectedList = pickingPositionService.getByPickFromStockUnit(pickFromStock);
					for( LOSPickingPosition affected : affectedList ) {
						if( affected.equals(pick) ) {
							continue;
						}
						cancelPick(affected);

						// Remove temporal from follow up pick generation
						pickFromStock.setReservedAmount(pickFromStock.getAmount());
						
						LOSPickingOrder affectedOrder = affected.getPickingOrder();
						if( affectedOrder != null && !affectedOrder.isManualCreation() ) {
							LOSCustomerOrderPosition orderPos = affected.getCustomerOrderPosition();
							if( orderPos != null ) {
								List<LOSPickingPosition> pickListNew = pickingPosGeneratorService.generatePicks( orderPos, pickingOrder.getStrategy(), affected.getAmount() );
								pickingOrderGeneratorService.addToOrder( affectedOrder, pickListNew );
							}
						}
					}
				}

				pickFromStock.setReservedAmount(BigDecimal.ZERO);
				changeAmount(pickFromStock, amountStart, activityCode);
				amountPickFrom = amountStart;
			}
			
			if( counted ) {
				if( pickFromLocation.getUnitLoads().size()<2 ) {
					pickFromLocation.setStockTakingDate(new Date());
				}
				recordService.recordCounting(pickFromStock, pickFromUnitLoad, pickFromLocation, activityCode, null,null);
			}
		}

		BigDecimal amountPosted = BigDecimal.ZERO;

		if( BigDecimal.ZERO.compareTo(amountPicked)<0 ) {
			// move stock to pick-to unit load
			
			// separate serial numbers 
			if( pick.getItemData().getSerialNoRecordType() == SerialNoRecordType.GOODS_OUT_RECORD ) {
				int numSerialRequired = amountPicked.intValue();
				if( serialNoList == null || serialNoList.size()!=numSerialRequired ) {
					log.warn(logStr+"There are not enough serialnumbers");
					throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_MISSING_SERIAL, "");
				}
				
				for( String serial : serialNoList ) {
					StockUnit stock = moveStock(pickFromStock, BigDecimal.ONE, pickToUnitLoad.getUnitLoad(), activityCode);
					stock.setSerialNumber(serial);
					amountPosted = amountPosted.add(BigDecimal.ONE);
				}
				
			}
			else {
				moveStock(pickFromStock, amountPicked, pickToUnitLoad.getUnitLoad(), activityCode);
				amountPosted = amountPosted.add(amountPicked);
			}
			pick.setState(State.PICKED);
			pick.setPickToUnitLoad(pickToUnitLoad);
		}
		else {
			pick.setState(State.CANCELED);
			pick.setPickToUnitLoad(null);
		}

		pick.setAmountPicked(amountPosted);
		pick.setLotPicked(lotPicked);
		pick.setPickFromStockUnit(null);
		
		LOSCustomerOrderPosition customerOrderPos = pick.getCustomerOrderPosition();
		if( customerOrderPos != null ) {
			if( pickingOrder.getStrategy().isCreateFollowUpPicks() && !pickingOrder.isManualCreation()) {
				BigDecimal amountMissing = pick.getAmount().subtract(amountPicked);
				if( BigDecimal.ZERO.compareTo(amountMissing)<0 ) {
					List<LOSPickingPosition> pickListNew = pickingPosGeneratorService.generatePicks(customerOrderPos, pickingOrder.getStrategy(), amountMissing );
					
					if( pickListNew != null && pickListNew.size()>0 ) {
						pickingOrderGeneratorService.addToOrder( pickingOrder, pickListNew );
					}
				}
			}		
			confirmCustomerOrderPos(customerOrderPos, amountPicked);
		}

		if( pickingOrder.getState()<State.STARTED ) {
			startPickingOrder(pickingOrder, true);
		}
		
		if( pick.getState() != stateOld )  {
			manageOrderService.onPickingPositionStateChange(pick, stateOld);
		}

		
		stateOld = pickToUnitLoad.getState();
		if( stateOld < State.STARTED ) {
			pickToUnitLoad.setState(State.STARTED);
			manageOrderService.onPickingUnitLoadStateChange(pickToUnitLoad, stateOld);
		}
		
		// Check picking order state
		boolean allPicksDone = true;
		for( LOSPickingPosition p : pickingOrder.getPositions() ) {
			if( p.getState() < State.PICKED ) {
				allPicksDone = false;
				break;
			}
		}
		stateOld = pickingOrder.getState();
		if( allPicksDone && stateOld < State.PICKED ) {
			pickingOrder.setState(State.PICKED);
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}
		

	}

	public void cancelPickFrom(LOSPickingPosition pick, boolean generateNewPick) throws FacadeException {
		String logStr = "cancelPickFrom ";
		if( pick == null ) {
			log.error(logStr+"missing parameter pick");
			return;
		}
		log.debug(logStr+"pick.id="+pick.getId()+", item="+pick.getItemData().getNumber());
		int stateOld = pick.getState();

		if( pick.getState()>=State.PICKED ) {
			log.error(logStr+"Pick already done. => Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_ALREADY_FINISHED, "");
		}
		if( pick.getPickFromStockUnit() == null ) {
			log.error(logStr+"Pick has no assigned stock. => Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_NO_STOCK, "");
		}
		
		LOSPickingOrder pickingOrder = pick.getPickingOrder();
		if( pickingOrder == null ) {
			log.error(logStr+"Cannot confirm without order. Abort");
			throw new InventoryException(InventoryExceptionKey.PICK_CONFIRM_MISSING_ORDER, "");
		}
		
		StockUnit pickFromStock = pick.getPickFromStockUnit();
		
		// Release the reservation on pick from stock unit. 
		// Maybe that some services would fail on the original reservations
		pickFromStock.releaseReservedAmount(pick.getAmount());
		
		
		pick.setState(State.CANCELED);
		pick.setAmountPicked(BigDecimal.ZERO);
		pick.setPickFromStockUnit(null);
		pick.setPickToUnitLoad(null);
		
		LOSCustomerOrderPosition customerOrderPos = pick.getCustomerOrderPosition();
		if( customerOrderPos != null ) {
			if( generateNewPick && pickingOrder.getStrategy().isCreateFollowUpPicks() ) {
				BigDecimal amountMissing = pick.getAmount();
					
				List<LOSPickingPosition> pickListNew = pickingPosGeneratorService.generatePicks(customerOrderPos, pickingOrder.getStrategy(), amountMissing );
				
				if( pickListNew != null && pickListNew.size()>0 ) {
					List<LOSPickingOrder> orderList = pickingOrderGeneratorService.addToOrder( pickingOrder, pickListNew );
					for( LOSPickingOrder pickingOrderNew : orderList ) {
						if( pickingOrderNew.getState() < State.PROCESSABLE ) {
							releasePickingOrder(pickingOrderNew);
						}
					}
				}
			}
			
			confirmCustomerOrderPos(customerOrderPos, BigDecimal.ZERO);
		}
		
		if( pickingOrder.getState()<State.STARTED ) {
			startPickingOrder(pickingOrder, true);
		}
		
		if( pick.getState() != stateOld )  {
			manageOrderService.onPickingPositionStateChange(pick, stateOld);
		}

		
		// Check picking order state
		boolean allPicksDone = true;
		for( LOSPickingPosition p : pickingOrder.getPositions() ) {
			if( p.getState() < State.PICKED ) {
				allPicksDone = false;
				break;
			}
		}
		stateOld = pickingOrder.getState();
		if( allPicksDone && stateOld < State.PICKED ) {
			pickingOrder.setState(State.PICKED);
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}
	}

	/**
	 * Confirm one picking position.
	 * 
	 * @param pick
	 * @param amountPicked
	 * @param amountRemain
	 * @throws FacadeException
	 */
	public void haltPickingPosition(LOSPickingPosition pick) throws FacadeException {
		String logStr = "haltPickingPosition ";
		if( pick == null ) {
			log.error(logStr+"missing parameter pick");
			return;
		}
		log.debug(logStr+"pick.id="+pick.getId()+", item="+pick.getItemData().getNumber());
		int stateOld = pick.getState();

		if( pick.getState()>=State.PICKED ) {
			log.warn(logStr+"Pick already done. => Ignore");
			return;
		}
		if( pick.getState()<=State.ASSIGNED ) {
			log.warn(logStr+"Pick already postponed. => Ignore");
			return;
		}
		
		pick.setState(State.ASSIGNED);
		
		manageOrderService.onPickingPositionStateChange(pick, stateOld);
	}

	/**
	 * Cancellation of a single picking position.<br>
	 * The picking order is not affected or recalculated!
	 * 
	 * @param pick
	 * @throws FacadeException
	 */
	public LOSPickingPosition cancelPick(LOSPickingPosition pick) throws FacadeException {
		String logStr = "cancelPick ";
		if( pick == null ) {
			log.error(logStr+"missing parameter order");
			return pick;
		}
		if( pick.getState()>=State.PICKED ) {
			log.info(logStr+"Cannot cancel already picked pick. id="+pick.getId());
			return pick;
		}
		int stateOld = pick.getState();

		StockUnit pickFromStock = pick.getPickFromStockUnit();
		if( pickFromStock != null ) {
			releaseReservation( pickFromStock, pick.getAmount());
		}
		pick.setState(State.CANCELED);
		pick.setPickFromStockUnit(null);
		
		if( pick.getState() != stateOld )  {
			manageOrderService.onPickingPositionStateChange(pick, stateOld);
		}
		
		if( pick.getCustomerOrderPosition() != null ) {
			confirmCustomerOrderPos(pick.getCustomerOrderPosition(), BigDecimal.ZERO);
		}

		return pick;
	}
	/**
	 * Changes the pick-from stock unit.
	 * 
	 * @param pick
	 * @param pickFromStockUnit
	 * @throws FacadeException
	 */
	public LOSPickingPosition changePickFromStockUnit(LOSPickingPosition pick, StockUnit pickFromStockNew) throws FacadeException {
		String logStr = "changePickFromStockUnit ";
		
		if( pick == null ) {
			log.error(logStr+"missing parameter pick. => Abort");
			throw new InventoryException(InventoryExceptionKey.MISSING_PARAMETER, "pick");
		}
		if( pickFromStockNew == null ) {
			log.error(logStr+"missing parameter pickFromStockUnit. => Abort");
			throw new InventoryException(InventoryExceptionKey.MISSING_PARAMETER, "pickFromStockNew");
		}

		StockUnit pickFromStockOld = pick.getPickFromStockUnit();
		if( pickFromStockOld == null ) {
			log.error(logStr+"pick has no pickFromStockUnit. => Abort");
			throw new InventoryException(InventoryExceptionKey.MISSING_PARAMETER, "pickFromStockOld");
		}

		UnitLoad pickFromUnitLoadOld = pick.getPickFromStockUnit().getUnitLoad();
		UnitLoad pickFromUnitLoadNew = pickFromStockNew.getUnitLoad();
		
		BigDecimal amountPick = pick.getAmount();
		BigDecimal amountStockNew = pickFromStockNew.getAmount();
		BigDecimal amountReservedNew = pickFromStockNew.getReservedAmount();
		if( amountReservedNew==null )
			amountReservedNew = BigDecimal.ZERO;
		BigDecimal amountStockOld = pickFromStockOld.getAmount();
		BigDecimal amountReservedOld = pickFromStockOld.getReservedAmount();
		if( amountReservedOld==null )
			amountReservedOld = BigDecimal.ZERO;
		amountReservedOld = amountReservedOld.subtract(amountPick);
		BigDecimal amountAvailableOld = amountStockOld.subtract(amountReservedOld);
		
		
		// calculate the maximum of allowed reserved amount on the new stock
		BigDecimal amountReservedMax = BigDecimal.ZERO;
		if( pick.getPickingType() == LOSPickingPosition.PICKING_TYPE_COMPLETE ) {
			amountReservedMax = null;
		}
		else {
			amountReservedMax = amountStockNew.subtract(amountPick);
		}
		if( amountReservedMax!=null && BigDecimal.ZERO.compareTo(amountReservedMax) > 0 ) {
			log.error(logStr+"Not enough material on new stock");
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, new Object[]{amountStockNew, pick.getItemData().getNumber()});
		}
		
		if( amountReservedMax==null || amountReservedNew.compareTo(amountReservedMax) > 0 ) {
			// Try to change reservations

			List<LOSPickingPosition> reserverList = pickingPositionService.getByPickFromStockUnit(pickFromStockNew);
			if( reserverList == null || reserverList.size() == 0 ) {
				log.warn(logStr+"seems to be a phantom reservation. kill it. stock="+pickFromStockNew.toDescriptiveString());
				// seems to be a phantom reservation. kill it.
				pickFromStockNew.setReservedAmount(BigDecimal.ZERO);
				amountReservedNew = BigDecimal.ZERO;
			}
			else {
				log.debug(logStr+"check reservers. size="+reserverList.size());
				for( LOSPickingPosition reserver : reserverList ) {
					BigDecimal amountReserver = reserver.getAmount();
					if( amountReserver.compareTo(amountAvailableOld)<= 0 ) {
						// Pick can be switched
						reserver.setPickFromStockUnit(pickFromStockOld);
						reserver.setPickFromLocationName(pickFromUnitLoadOld.getStorageLocation().getName());
						reserver.setPickFromUnitLoadLabel(pickFromUnitLoadOld.getLabelId());
						
						amountReservedOld = amountReservedOld.add(amountReserver);
						amountAvailableOld = amountAvailableOld.subtract(amountReserver);
						amountReservedNew = amountReservedNew.subtract(amountReserver);
					}
					if( amountReservedMax!=null && amountReservedNew.compareTo(amountReservedMax) <= 0 ) {
						// It has been remove enough reserved amount
						break;
					}
				}
			}
		}
		
		if( amountReservedMax!=null && amountReservedNew.compareTo(amountReservedMax) > 0 ) {
			// It was not possible to remove enough reserved amount
			log.error(logStr+"Cannot remove enough reserved amount. => Abort. amount-stock="+amountStockNew+", amount-pick="+amountPick+", amount-reserved-max="+amountReservedMax+", amount-reserved-new="+amountReservedNew);
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, new Object[]{amountStockNew, pick.getItemData().getNumber()});
		}

		amountReservedNew = amountReservedNew.add(amountPick);
		pickFromStockNew.setReservedAmount(amountReservedNew);
		pickFromStockOld.setReservedAmount(amountReservedOld);
		
		pick.setPickFromStockUnit(pickFromStockNew);
		pick.setPickFromLocationName(pickFromUnitLoadNew.getStorageLocation().getName());
		pick.setPickFromUnitLoadLabel(pickFromUnitLoadNew.getLabelId());
		
		return pick;
	}

	
	public LOSPickingUnitLoad confirmPickingUnitLoad( LOSPickingUnitLoad pickingUnitLoad, StorageLocation destination, int state ) throws FacadeException {
		
		UnitLoad unitLoad = pickingUnitLoad.getUnitLoad();
		if( ! unitLoad.getStorageLocation().equals(destination) ) {
			// Transfer posting only necessary if location is changed
			storage.transferUnitLoad(contextService.getCallerUserName(), destination, pickingUnitLoad.getUnitLoad(), 0, true, null, null);
		}
		
		pickingUnitLoad.getUnitLoad().setLock(StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
		int stateOld = pickingUnitLoad.getState();
		if( state>0 && stateOld<state ) {
			pickingUnitLoad.setState(state);
			manageOrderService.onPickingUnitLoadStateChange(pickingUnitLoad, stateOld);
		}
		
		else if( state < 0 ) {
			// automatically handling
			LOSOrderStrategy strat = pickingUnitLoad.getPickingOrder().getStrategy();
			if( strat != null ) {
				if( strat.isCreateGoodsOutOrder() ) {
					pickingUnitLoad.setState(State.PICKED);
				}
				else {
					pickingUnitLoad.setState(State.FINISHED);
				}
				if( pickingUnitLoad.getState() != stateOld ) {
					manageOrderService.onPickingUnitLoadStateChange(pickingUnitLoad, stateOld);
				}
			}
		}
		
		return pickingUnitLoad;
	}
	
	
	public LOSPickingOrder recalculatePickingOrderState( LOSPickingOrder pickingOrder ) throws FacadeException {
		String logStr = "recalculatePickingOrderState ";
		log.debug(logStr);
		
		if( pickingOrder == null ) {
			log.warn(logStr+"No order given. cannot calculate state");
			return null;
		}

		if( pickingOrder.getPositions().size()==0 ) {
			log.debug(logStr+"do not force calculation on orders with no positions");
			return pickingOrder;
		}

		int stateOld = pickingOrder.getState();
		
		boolean hasOnlyCanceled = true;
		boolean hasOnlyFinished = true;
		boolean hasProcessed = false;
		for( LOSPickingPosition pick : pickingOrder.getPositions() ) {
			if( pick.getState() < State.CANCELED ) {
				hasOnlyCanceled = false;
			}
			
			if( pick.getState() < State.PICKED ) {
				hasOnlyFinished = false;
			}
			
			if( pick.getState() >= State.STARTED && pick.getState() != State.CANCELED ) {
				hasProcessed = true;
			}
		}
		
		int stateNew = pickingOrder.getState();
		if( hasOnlyCanceled ) {
			if( stateNew < State.CANCELED ) {
				stateNew = State.CANCELED;
			}
		}
		else if( hasOnlyFinished ) {
			if( stateNew < State.FINISHED ) {
				stateNew = State.FINISHED;
			}
		}
		else if( hasProcessed ) {
			if( stateNew < State.STARTED ) {
				stateNew = State.STARTED;
			}
		}
			
		if( stateNew!=pickingOrder.getState() ) {
			log.info(logStr+"Change state of order: old="+pickingOrder.getState()+", new="+stateNew);
			pickingOrder.setState(stateNew);
		}
		
		if( pickingOrder.getState() != stateOld )  {
			manageOrderService.onPickingOrderStateChange(pickingOrder, stateOld);
		}
		return pickingOrder;
	}
	
	
	
	private void releaseReservation( StockUnit stock, BigDecimal amount) throws FacadeException {
		BigDecimal amountReservedNew = stock.getReservedAmount().subtract(amount);
		if( BigDecimal.ZERO.compareTo(amountReservedNew) > 0 ) {
			amountReservedNew = BigDecimal.ZERO;
		}
		invComponent.changeReservedAmount(stock, amountReservedNew, null);
	}
	
	private StockUnit moveStock(StockUnit pickFromStock, BigDecimal amountPicked, UnitLoad pickToUnitLoad, String activityCode) throws FacadeException {
		// Do not create new stocks in complete unit load operations
		boolean completeOperation = false;
		StockUnit pickToStock = pickFromStock;
		if( amountPicked==null || amountPicked.compareTo(pickFromStock.getAmount())==0 ) {
			completeOperation=true;
			invComponent.transferStockUnit(pickFromStock, pickToUnitLoad, activityCode);
		}
		else {
			pickToStock = invComponent.splitStock(pickFromStock, pickToUnitLoad, amountPicked, activityCode);
		}
		pickToStock.setLock(StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
		
		return pickToStock;
	}

	private void changeAmount( StockUnit stock, BigDecimal amount, String activityCode ) throws FacadeException {
		UnitLoad unitLoad = stock.getUnitLoad();
		invComponent.changeAmount(stock, amount, true, activityCode, null, contextService.getCallerUserName(), true);
		if( BigDecimal.ZERO.equals(amount) ) {
			invComponent.sendStockUnitsToNirwana(stock, activityCode);
		}
		invComponent.sendUnitLoadToNirwanaIfEmpty(unitLoad);
	}

}	