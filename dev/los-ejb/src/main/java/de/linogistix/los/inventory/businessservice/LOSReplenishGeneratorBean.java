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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSReplenishOrderService;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.model.State;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishGeneratorBean implements LOSReplenishGenerator {
	private final static Logger log = Logger.getLogger(LOSReplenishGeneratorBean.class);

	@EJB
	private InventoryGeneratorService sequenceService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSInventoryComponent invBusiness;
	@EJB
	private LOSReplenishOrderService orderService;
	@EJB
	private LOSReplenishStockService stockService;
	@EJB
	private LocationReserver locationReserver;


	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;



	// TODO krane: Implement an intelligent strategy, that does not scann all - even full - locations on every run
	@SuppressWarnings("unchecked")
	public List<LOSReplenishOrder> refillFixedLocations() throws FacadeException {
		String logStr = "refillFixedLocations ";
		
		List<LOSReplenishOrder> ret = new ArrayList<LOSReplenishOrder>();

		// find locations with UnitLoads on and free places left
		StringBuffer sb = new StringBuffer("SELECT ass FROM ");
		sb.append(LOSFixedLocationAssignment.class.getSimpleName() + " ass ");
		sb.append(" WHERE ass.desiredAmount>0 ");
		Query query = manager.createQuery(sb.toString());

		List<LOSFixedLocationAssignment> fixed = query.getResultList();

		for (LOSFixedLocationAssignment ass : fixed) {
			log.info("Check location " + ass.getAssignedLocation().getName());
			
			BigDecimal amount = invBusiness.getAmountOfStorageLocation(ass.getItemData(), ass.getAssignedLocation());
			BigDecimal d = ass.getDesiredAmount().multiply(new BigDecimal(0.25));
			if( amount.compareTo(d) >= 0 ) {
				log.info("There is still enough material on location " + ass.getAssignedLocation().getName());
				continue;
			}

			List<LOSReplenishOrder> active = orderService.getActive(ass.getItemData(), null, ass.getAssignedLocation(), null);
			if (active != null && active.size() > 0) {
				log.info(logStr+"There is already replenishment ordered for location " + ass.getAssignedLocation().getName());
				continue;
			}
			
			LOSReplenishOrder order = calculateOrder(ass.getItemData(), null, null, ass.getAssignedLocation(), null);
			ret.add(order);
		}

		return ret;
	}

	public void calculateReleasedOrders() throws FacadeException {
		List<LOSReplenishOrder> replList = orderService.getActive(null, null, null, null);
		for( LOSReplenishOrder order : replList ) {
			if( order.getState() == State.RELEASED ) {
				calculateOrder( order.getItemData(), order.getLot(), order.getRequestedAmount(), order.getRequestedLocation(), order.getRequestedRack() );
			}
		}
	}


	public LOSReplenishOrder calculateOrder( ItemData itemData, Lot lot, BigDecimal amount, StorageLocation requestedLocation, String requestedRack ) throws FacadeException {
		String logStr = "generateOrder ";

		if( itemData == null ) {
			log.warn(logStr+"No item data, no replenish");
			return null;
		}
		
		log.info(logStr+"ItemData="+itemData.getNumber());
		
		List<StorageLocation> vetoList = new ArrayList<StorageLocation>();
		if( requestedLocation != null ) {
			vetoList.add(requestedLocation);
		}
		
		StockUnit sourceStock = stockService.findReplenishStock(itemData, lot, amount, vetoList);
		if( sourceStock == null ) {
			log.info(logStr+"No replenish stock available. location="+requestedLocation+", ItemData=" + itemData.getNumber());
			return null;
		}

		LOSReplenishOrder order = null;
		
		List<LOSReplenishOrder> replList = orderService.getActive(itemData, lot, requestedLocation, requestedRack);
		for( LOSReplenishOrder o : replList ) {
			if( o.getState() <= State.RELEASED ) {
				order = o;
				break;
			}
		}
	
		if( order == null ) {
			order = entityGenerator.generateEntity(LOSReplenishOrder.class);
			String orderNumber = sequenceService.generateReplenishNumber(null);
			order.setClient(itemData.getClient());
			order.setNumber(orderNumber);
			order.setItemData(itemData);
			order.setLot(lot);
			order.setRequestedLocation(requestedLocation);
			order.setRequestedRack(requestedRack);
			order.setState(State.RELEASED);
			manager.persist(order);
		}
		
		order.setDestination(requestedLocation);
		order.setRequestedAmount(amount);
		order.setClient(itemData.getClient());
		order.setStockUnit(sourceStock);
		order.setState(State.PROCESSABLE);

		invBusiness.changeReservedAmount(sourceStock, sourceStock.getAmount(), null);

		locationReserver.allocateLocation(requestedLocation, sourceStock.getUnitLoad());

		return order;
	}
	
	
	@TransactionAttribute( TransactionAttributeType.REQUIRES_NEW )
	public void generateRequest( ItemData itemData, Lot lot, BigDecimal amount, StorageLocation requestedLocation, String requestedRack ) {
		String logStr = "generateRequest ";
		List<LOSReplenishOrder> replList = orderService.getActive( itemData, lot, requestedLocation, requestedRack );
		if( replList.size() > 0 ) {
			log.info(logStr+"Replenish already requested");
			return;
		}
	
		LOSReplenishOrder order = entityGenerator.generateEntity(LOSReplenishOrder.class);
		String orderNumber = sequenceService.generateReplenishNumber(null);

		order.setClient(itemData.getClient());
		order.setNumber(orderNumber);
		order.setItemData(itemData);
		order.setLot(lot);
		order.setRequestedLocation(requestedLocation);
		order.setRequestedRack(requestedRack);
		order.setRequestedAmount(amount);
		order.setState(State.RELEASED);
		
		manager.persist(order);

		log.info(logStr+"Replenish requested for itemData="+itemData.getNumber());
	}
}
