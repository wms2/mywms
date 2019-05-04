/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingOrderGeneratorBean implements LOSPickingOrderGenerator {
	Logger log = Logger.getLogger(LOSPickingOrderGeneratorBean.class);

	@EJB
	private LOSPickingOrderService pickingOrderService;
	@EJB
	private ManageOrderService manageOrderService;


	public  List<LOSPickingOrder> addToOrder( LOSPickingOrder order, List<LOSPickingPosition> pickList ) throws FacadeException {
		String logStr = "addToOrder ";
		log.debug(logStr);
		
		List<LOSPickingOrder> ret = new ArrayList<LOSPickingOrder>();
		ret.add(order);
		
		if( pickList == null || pickList.size()==0 ) {
			return ret;
		}
		
		for( LOSPickingPosition pick : pickList ) {
			int pickStateOld = pick.getState();
			
			if( ! pick.getStrategy().equals(order.getStrategy()) ) {
				log.error(logStr+"Different strategies of picking order and picking position. Cannot handle.");
				throw new InventoryException(InventoryExceptionKey.PICK_GEN_DIFF_STRAT, "");
			}
			pick.setPickingOrder(order);
			order.getPositions().add(pick);
			if( pickStateOld>=State.ASSIGNED && pickStateOld<State.PICKED ) {
				pick.setState(State.PROCESSABLE);
				manageOrderService.onPickingPositionStateChange(pick, pickStateOld);
			}
		}
		
		return ret;
	}

	public List<LOSPickingOrder> createOrders( List<LOSPickingPosition> pickList ) throws FacadeException {
		return createOrders( pickList, null ); 
	}
	public List<LOSPickingOrder> createOrders( List<LOSPickingPosition> pickList, String sequenceName ) throws FacadeException {
		String logStr = "createOrders ";
		log.debug(logStr);

		Client client = null;
		LOSOrderStrategy strat = null;
		for( LOSPickingPosition pick : pickList ) {
			if( strat == null ) {
				strat = pick.getStrategy();
			}
			else if( strat != null && !strat.equals(pick.getStrategy()) ) {
				log.error(logStr+"Different strategies of picking order and picking position. Cannot handle.");
				throw new InventoryException(InventoryExceptionKey.PICK_GEN_DIFF_STRAT, "");
			}
			
			if( client == null ) {
				client = pick.getClient();
			}
			if( ! client.equals(pick.getClient()) ) {
				log.error(logStr+"Cannot generate picking order for mixed clients");
				throw new InventoryException(InventoryExceptionKey.PICK_GEN_CLIENT_MIX, "");
			}
		}
		
		List<LOSPickingOrder> ret = new ArrayList<LOSPickingOrder>();
		Map<String,List<LOSPickingPosition>> pickMap = new HashMap<String, List<LOSPickingPosition>>();
		Set<LOSCustomerOrder> orderSet = new HashSet<LOSCustomerOrder>();
		
		// Use only processable positions
		for( LOSPickingPosition pick : pickList ) {
			if( pick.getPickingOrder() != null ) {
				log.debug(logStr+"Pick has already picking order. number="+pick.getId());
				continue;
			}
			if( pick.getState() >= State.PICKED ) {
				log.debug(logStr+"Pick is already picked. number="+pick.getId());
				continue;
			}
			if( pick.getPickFromStockUnit() == null ) {
				log.debug(logStr+"Pick has no pick from stock. number="+pick.getId());
				continue;
			}
			
			String key = "-";
			
			List<LOSPickingPosition> pickList2 = pickMap.get(key);
			if( pickList2 == null ) {
				pickList2 = new ArrayList<LOSPickingPosition>();
				pickMap.put(key, pickList2);
			}
			pickList2.add(pick);
			
			if( pick.getCustomerOrderPosition() != null ) {
				orderSet.add(pick.getCustomerOrderPosition().getOrder());
			}
		}
		
		// Find the highest priority (lowest value)
		// Find location of customer order
		Integer prioObject = null;
		LOSStorageLocation location = null;
		boolean locationSelected = false;
		for( LOSCustomerOrder customerOrder : orderSet ) {
			if( customerOrder != null ) {
				if( prioObject == null ) {
					prioObject = customerOrder.getPrio();
				}
				else if( prioObject.intValue() > customerOrder.getPrio() ) {
					prioObject = customerOrder.getPrio();
				}
				
				
				if( !locationSelected ) {
					location = customerOrder.getDestination();
					locationSelected = true;
				}
				else if( location ==null || customerOrder.getDestination() == null || !location.equals(customerOrder.getDestination()) ) {
					location = null;
				}
				
				int customerOrderState = customerOrder.getState(); 
				if( customerOrderState < State.PROCESSABLE ) {
					customerOrder.setState(State.PROCESSABLE);
					manageOrderService.onCustomerOrderStateChange(customerOrder, customerOrderState);
				}
				else if( customerOrderState == State.PENDING ) {
					customerOrder.setState(State.STARTED);
					manageOrderService.onCustomerOrderStateChange(customerOrder, customerOrderState);
				}

			}
		}
		if( !locationSelected && location == null && strat != null ) {
			location = strat.getDefaultDestination();
		}
		
		String orders = "";
		for( List<LOSPickingPosition> pickList2 : pickMap.values() ) {
			if( pickList2.size() == 0 ) {
				continue;
			}
			
			LOSPickingOrder pickingOrder = pickingOrderService.create(client, strat, sequenceName);
			
			String customerOrderNumber = null;
			boolean customerOrderValid = true;
			
			for( LOSPickingPosition pick : pickList2 ) {
				int pickStateOld = pick.getState();
				pick.setPickingOrder(pickingOrder);
				if( pickStateOld != State.PROCESSABLE ) {
					pick.setState(State.PROCESSABLE);
					manageOrderService.onPickingPositionStateChange(pick, pickStateOld);
				}
				
				if( pick.getCustomerOrderPosition() == null ) {
					customerOrderNumber = null;
					customerOrderValid = false;
				}
				else if( customerOrderValid ) {
					if( customerOrderNumber == null ) {
						customerOrderNumber = pick.getCustomerOrderPosition().getOrder().getNumber();
					}
					else if( !customerOrderNumber.equals(pick.getCustomerOrderPosition().getOrder().getNumber()) ) {
						customerOrderNumber = null;
						customerOrderValid = false;
					}
				}
			}
				
			pickingOrder.getPositions().addAll(pickList2);
			pickingOrder.setState(State.RAW);
			pickingOrder.setPrio(prioObject==null?50:prioObject.intValue());
			pickingOrder.setDestination(location);
			pickingOrder.setCustomerOrderNumber(customerOrderNumber);
			
			ret.add(pickingOrder);
			orders = orders + pickingOrder.getNumber()+", ";
		}
		
		log.debug(logStr+"Generated "+ret.size()+" picking orders ("+orders+")");
		return ret;
	}

	public LOSPickingOrder createSingleOrder( List<LOSPickingPosition> pickList) throws FacadeException {
		return createSingleOrder( pickList, null );
	}
	public LOSPickingOrder createSingleOrder( List<LOSPickingPosition> pickList, String sequenceName ) throws FacadeException {
		String logStr = "createSingleOrder ";
		log.debug(logStr);
		
		Client client = null;
		String customerOrderNumber = null; 
		LOSOrderStrategy strat = null;

		for( LOSPickingPosition pick : pickList ) {
			if( strat == null ) {
				strat = pick.getStrategy();
			}
			else if( !strat.equals(pick.getStrategy()) ) {
				log.error(logStr+"Different strategies of picking order and picking position. Cannot handle.");
				throw new InventoryException(InventoryExceptionKey.PICK_GEN_DIFF_STRAT, "");
			}
			
			if( client == null ) {
				client = pick.getClient();
			}
			if( ! client.equals(pick.getClient()) ) {
				log.error(logStr+"Cannot generate picking order for mixed clients");
				return null;
			}
		}
		List<LOSPickingPosition> pickListNew = new ArrayList<LOSPickingPosition>();
		Set<LOSCustomerOrder> customerOrderSet = new HashSet<LOSCustomerOrder>();

		
		// Use only processable positions
		for( LOSPickingPosition pick : pickList ) {
			if( pick.getPickingOrder() != null ) {
				log.debug(logStr+"Pick has already picking order. number="+pick.getId());
				continue;
			}
			if( pick.getState() >= State.PICKED ) {
				log.debug(logStr+"Pick is already picked. number="+pick.getId());
				continue;
			}
			if( pick.getPickFromStockUnit() == null ) {
				log.debug(logStr+"Pick has no pick from stock. number="+pick.getId());
				continue;
			}

			pickListNew.add(pick);
			
			if( pick.getCustomerOrderPosition() != null ) {
				customerOrderSet.add(pick.getCustomerOrderPosition().getOrder());
			}

		}
		
		if( pickListNew.size() == 0 ) {
			log.debug(logStr+"Nothing to do");
			return null;
		}

		// Find the highest priority (lowest value)
		// Find location of customer order
		Integer prioObject = null;
		LOSStorageLocation location = null;
		boolean locationSelected = false;
		for( LOSCustomerOrder customerOrder : customerOrderSet ) {
			if( customerOrder != null ) {
				if( prioObject == null ) {
					prioObject = customerOrder.getPrio();
				}
				else if( prioObject.intValue() > customerOrder.getPrio() ) {
					prioObject = customerOrder.getPrio();
				}
				
				
				if( !locationSelected ) {
					location = customerOrder.getDestination();
					locationSelected = true;
				}
				else if( location ==null || customerOrder.getDestination() == null || !location.equals(customerOrder.getDestination()) ) {
					location = null;
				}
				
				int customerOrderState = customerOrder.getState(); 
				if( customerOrder.getState() < State.PROCESSABLE ) {
					customerOrder.setState(State.PROCESSABLE);
					manageOrderService.onCustomerOrderStateChange(customerOrder, customerOrderState);
				}
				else if( customerOrder.getState() == State.PENDING ) {
					customerOrder.setState(State.STARTED);
					manageOrderService.onCustomerOrderStateChange(customerOrder, customerOrderState);
				}
				
				customerOrderNumber = customerOrder.getNumber();
			}
		}
		if( customerOrderSet.size() > 1 ) {
			customerOrderNumber = null;
		}
		
		if( !locationSelected && location == null && strat != null ) {
			location = strat.getDefaultDestination();
		}
		
		LOSPickingOrder pickingOrder = null;
		for( LOSPickingPosition pick : pickListNew ) {
			if( pickingOrder == null ) {
				pickingOrder = pickingOrderService.create(pick.getClient(), strat, sequenceName);
				pickingOrder.setState(State.RAW);
				pickingOrder.setCustomerOrderNumber(customerOrderNumber);
				pickingOrder.setPrio(prioObject==null?50:prioObject.intValue());
				pickingOrder.setDestination(location);

			}
			pick.setPickingOrder(pickingOrder);
			int pickStateOld = pick.getState();
			if( pickStateOld != State.PROCESSABLE ) {
				pick.setState(State.PROCESSABLE);
				manageOrderService.onPickingPositionStateChange(pick, pickStateOld);
			}
		}
		pickingOrder.getPositions().addAll(pickListNew);
			
		
		log.debug(logStr+"Generated new picking order number="+pickingOrder.getNumber());
		return pickingOrder;
	}

	

}
