/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.model.State;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingPosGeneratorBean implements LOSPickingPosGenerator {
	Logger log = Logger.getLogger(LOSPickingPosGeneratorBean.class);

	@EJB
	private LOSPickingPositionService pickingPositionService;
	@EJB
	private LOSPickingStockService pickingStockService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSOrderStrategyService orderStratService;
	@EJB
	private ManageOrderService manageOrderService;
	@EJB
	private QueryUnitLoadTypeService unitLoadTypeService;

    @PersistenceContext(unitName = "myWMS")
    private  EntityManager manager;
	
	public List<LOSPickingPosition> generatePicks( LOSCustomerOrder order, boolean onlyComplete ) throws FacadeException {
		String logStr = "generatePicks ";
		log.debug(logStr+"order="+order.getNumber());
		List<LOSPickingPosition> pickList = new ArrayList<LOSPickingPosition>();

		int customerOrderStateOld = order.getState();
		
		LOSOrderStrategy strategy = order.getStrategy();
		if( strategy == null ) {
			strategy = orderStratService.getDefault(order.getClient());
		}
		
		for( LOSCustomerOrderPosition customerOrderPos : order.getPositions() ) {
			int posStateOld = customerOrderPos.getState();
			if( posStateOld >= State.PICKED ) {
				continue;
			}
			List<LOSPickingPosition> pickList1 = pickingPositionService.getByCustomerOrderPosition(customerOrderPos);
			BigDecimal amountPicked = BigDecimal.ZERO;
			for( LOSPickingPosition pick : pickList1 ) {
				if( pick.getState()==State.CANCELED ) {
					// do not register
				}
				else if( pick.getState()>=State.PICKED ) {
					amountPicked = amountPicked.add(pick.getAmountPicked());
				}
				else if( pick.getState()<State.ASSIGNED ) {
					pick.setState(State.CANCELED);
				}
				else {
					amountPicked = amountPicked.add(pick.getAmount());
				}
			}
			BigDecimal amountMissing = customerOrderPos.getAmount().subtract(amountPicked);
			if( BigDecimal.ZERO.compareTo(amountMissing) >= 0 ) {
				log.info(logStr+"No amount left for order position number="+customerOrderPos.getNumber());
				continue;
			}
			List<LOSPickingPosition> pickListNew = generatePicks( customerOrderPos, strategy, amountMissing );

			// Check picks for return value
			if( onlyComplete ) {
				BigDecimal amountAssigned = BigDecimal.ZERO;
				for( LOSPickingPosition pick : pickListNew ) {
					amountAssigned = amountAssigned.add(pick.getAmount());
				}
				if( amountAssigned.compareTo(amountMissing)!=0 ) {
					log.error(logStr+"Not enough amount for picking. item="+customerOrderPos.getItemData().getNumber()+", amount="+amountMissing);
					throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, new Object[]{amountAssigned,customerOrderPos.getItemData().getNumber() });
				}
			}
			
			if( pickListNew.size()>0 ) {
				if( customerOrderPos.getState() < State.PROCESSABLE ) {
					customerOrderPos.setState(State.PROCESSABLE);
					if( order.getState() < State.PROCESSABLE ) {
						order.setState(State.PROCESSABLE);
					}
				}
				else if( customerOrderPos.getState() == State.PENDING ) {
					if( BigDecimal.ZERO.compareTo(customerOrderPos.getAmountPicked()) < 0 ) {
						customerOrderPos.setState(State.STARTED);
					}
					else {
						customerOrderPos.setState(State.PROCESSABLE);
					}
					if( order.getState() == State.PENDING ) {
						boolean hasPicked = false;
						for( LOSCustomerOrderPosition orderPos2 : order.getPositions() ) {
							if( BigDecimal.ZERO.compareTo(orderPos2.getAmountPicked()) < 0 ) {
								hasPicked = true;
								break;
							}
						}
						order.setState( hasPicked ? State.STARTED : State.PROCESSABLE );
					}
				}
			}
			
			if( customerOrderPos.getState() != posStateOld ) {
				manageOrderService.onCustomerOrderPositionStateChange(customerOrderPos, posStateOld);
			}
			
			pickList.addAll(pickListNew);
			
		}
		
		if( order.getState() != customerOrderStateOld ) {
			manageOrderService.onCustomerOrderStateChange(order, customerOrderStateOld);
		}
		
		return pickList;
	}

	public List<LOSPickingPosition> generatePicks( LOSCustomerOrderPosition customerOrderPos, LOSOrderStrategy strategy, BigDecimal amount ) throws FacadeException {
		String logStr ="generatePicks ";

		if( strategy == null ) {
			strategy = orderStratService.getDefault(customerOrderPos.getClient());
		}
		
		List<LOSPickingPosition> pickList = new ArrayList<LOSPickingPosition>();
		
		if( amount == null ) {
			log.error(logStr+"No amount given. Cannot generate picks");
			return pickList;
		}

		while( BigDecimal.ZERO.compareTo(amount)<0 ) {
			StockUnit stock = pickingStockService.findPickFromStock( customerOrderPos.getClient(), customerOrderPos.getItemData(), customerOrderPos.getLot(), amount, customerOrderPos.getSerialNumber(), null, strategy );
			if( stock == null ) {
				break;
			}

			BigDecimal amountPick = amount;
			BigDecimal amountStock = stock.getAvailableAmount();
			if( amountPick.compareTo(amountStock)>0 ) {
				amountPick = amountStock;
			}

			LOSPickingPosition pick = generatePick( amountPick, stock, strategy, customerOrderPos );

			pickList.add(pick);
			
			amount = amount.subtract(pick.getAmount());
		}

		return pickList;
	}
	
	public LOSPickingPosition generatePick( BigDecimal amount, StockUnit pickFromStock, LOSOrderStrategy strat, LOSCustomerOrderPosition customerOrderPos ) throws FacadeException {
		LOSUnitLoad pickFromUnitLoad = (LOSUnitLoad) pickFromStock.getUnitLoad();
		
		LOSPickingPosition pick = entityGenerator.generateEntity(LOSPickingPosition.class);

		pick.setClient( pickFromStock.getClient() );
		pick.setItemData( pickFromStock.getItemData() );
		pick.setAmount(amount);
		pick.setPickFromStockUnit(pickFromStock);
		pick.setPickFromLocationName(pickFromUnitLoad.getStorageLocation().getName());
		pick.setPickFromUnitLoadLabel(pickFromUnitLoad.getLabelId());
		pick.setStrategy(strat);
		pick.setCustomerOrderPosition( customerOrderPos );
		pick.setState(State.ASSIGNED);
		if( isCompleteType(amount, pickFromStock) ) {
			pick.setPickingType(LOSPickingPosition.PICKING_TYPE_COMPLETE);
		}
		else {
			pick.setPickingType(LOSPickingPosition.PICKING_TYPE_PICK);
		}
		
		manager.persist(pick);
		
		pickFromStock.addReservedAmount(amount);
		
		manageOrderService.onPickingPositionStateChange(pick, -1);
		
		return pick;
	}
	
	private boolean isCompleteType( BigDecimal amount, StockUnit pickFromStock ) {
		if( amount.compareTo(pickFromStock.getAmount())!=0 ) {
			return false;
		}
		LOSUnitLoad unitLoad = (LOSUnitLoad)pickFromStock.getUnitLoad();
		if( unitLoad.isOpened() ) {
			return false;
		}
		
		UnitLoadType unitLoadTypePick = unitLoadTypeService.getPickLocationUnitLoadType();
		if( unitLoad.getType().equals(unitLoadTypePick) ) {
			return false;
		}

		if( unitLoad.getStorageLocation().getArea() != null && !unitLoad.getStorageLocation().getArea().isUseForStorage() ) {
			return false;
		}
		
		return true;
	}
}
