/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.LOSExceptionRB;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.businessservice.LOSPickingOrderGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingPosGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSExtinguishFacadeBean implements LOSExtinguishFacade {
	private static Logger log = Logger.getLogger(LOSExtinguishFacade.class);

	@EJB
	private ContextService contextService;
	@EJB
	private LOSLotService lotService;
	@EJB
	private ClientService clientService;
	@EJB
	private LOSStorageLocationService locationService;
	@EJB
	private LOSPickingPosGenerator pickPosGenerator;
	@EJB
	private LOSPickingOrderGenerator pickOrderGenerator;
	@EJB
	private LOSOrderStrategyService strategyService;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private InventoryGeneratorService sequenceService;
	@EJB
	private LOSPickingOrderService pickingOrderService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	
	public void generateOrder( String clientNumber, String lotName, String itemDataNumber ) throws FacadeException {
		String logStr = "generateOrder ";
		log.info(logStr+"clientNumber="+clientNumber+", lotName="+lotName+", itemNumber="+itemDataNumber);
		

		Client client = null;
		if( clientNumber != null ) {
			client = clientService.getByNumber(clientNumber);
			if( client == null ) {
				log.warn(logStr+"Client does not exist");
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientNumber);
			}
		}
		if( client == null ) {
			client = contextService.getCallersClient();
		}
		
		Lot lot = null;
		try {
			lot = lotService.getByNameAndItemData(client, lotName, itemDataNumber);
		} catch (EntityNotFoundException e) {}
		if( lot == null ) {
			log.warn(logStr+"Lot does not exist");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_LOT, lotName+" / "+itemDataNumber);
		}
		
		List<StockUnit> stockList = getListByLot(lot, false);
		
		generateOrder( client, stockList );
		
		lot.setLock(LotLockState.LOT_EXPIRED.getLock());

	}
	
	public void generateOrder( List<Long> lotIds ) throws FacadeException {
		String logStr = "generateOrder ";
		log.info(logStr);

		Client client = contextService.getCallersClient();

		if( lotIds==null || lotIds.size()==0 ) {
			log.warn(logStr+"No lot, no extinguish order");
			return;
		}
		
		for( long id : lotIds ) {
			Lot lot = null;
			try {
				lot = lotService.get(id);
			} catch (EntityNotFoundException e) {
			}
			if( lot == null ) {
				continue;
			}
			
			List<StockUnit> stockList = getListByLot(lot, false);
			if( stockList == null || stockList.size()==0 ) {
				log.warn(logStr+"No Stock for lot. name="+lot.getName());
				throw new InventoryException(InventoryExceptionKey.NO_INVENTORY_FOR_LOT, "");
			}
			
			generateOrder(client, stockList);
			
			lot.setLock(LotLockState.LOT_EXPIRED.getLock());
		}
		
	}

	public void generateOrder( Client client, List<StockUnit> stockList ) throws FacadeException {
		String logStr = "generateOrder ";
		LOSStorageLocation nirwana = locationService.getNirwana();
		List<LOSPickingPosition> pickList = new ArrayList<LOSPickingPosition>();
		LOSOrderStrategy strat = strategyService.getExtinguish(client);
		
		for( StockUnit stock : stockList ) {
			LOSUnitLoad ul = (LOSUnitLoad) stock.getUnitLoad();
			if( ul.getStorageLocation().equals(nirwana) ){
				continue;
			}
			if( BigDecimal.ZERO.compareTo(stock.getAvailableAmount()) >= 0 ) {
				continue;
			}
			if( stock.getLock() == BusinessObjectLockState.GOING_TO_DELETE.getLock() ) {
				continue;
			}
			if( stock.getLock() == StockUnitLockState.PICKED_FOR_GOODSOUT.getLock() ) {
				continue;
			}
			LOSPickingPosition pick = pickPosGenerator.generatePick( stock.getAvailableAmount(), stock, strat, null);
			pickList.add(pick);
		}
		List<LOSPickingOrder> pickingOrderList = null;
		if( pickList.size()>0 ) {
			pickingOrderList = pickOrderGenerator.createOrders(pickList, "EX");
		}
		
		if( pickingOrderList != null && pickingOrderList.size()>0 ) {
			for( LOSPickingOrder pickingOrder : pickingOrderList ) {
				pickingOrder.setManualCreation(false);
				orderBusiness.releasePickingOrder(pickingOrder);
			}
		}
	}

	
	public void calculateLotLocks() throws FacadeException {
		String logStr = "calculateLotLocks ";
		
		Client client = contextService.getCallersClient();
		
		log.info(logStr+"client="+client.getNumber());

		
		List<Lot> lots = lotService.getTooOld(client);
		for (Lot l : lots) {
			if( l.getLock() != BusinessObjectLockState.NOT_LOCKED.getLock() && l.getLock() != LotLockState.LOT_TOO_YOUNG.getLock() ) {
				log.warn(logStr+"Lot is locked. Will not set to EXPIRED. lock="+l.getLock()+", lot="+ l.toUniqueString());
				continue;
			}
			log.info(logStr+"Lot will be locked with LotLockState.LOT_EXPIRED. lot=" + l.toUniqueString());
			l.setLock(LotLockState.LOT_EXPIRED.getLock());
		}

		lots = lotService.getNotToUse(client);
		for (Lot l : lots) {
			if( l.getLock() != BusinessObjectLockState.NOT_LOCKED.getLock() && l.getLock() != LotLockState.LOT_EXPIRED.getLock() ) {
				log.warn(logStr+"Lot is locked. Will not set to LOT_TOO_YOUNG. lock="+l.getLock()+", lot="+ l.toUniqueString());
				continue;
			}
			log.info(logStr+"Lot will be locked with LotLockState.LOT_TOO_YOUNG. lot="+ l.toUniqueString());
			l.setLock(LotLockState.LOT_TOO_YOUNG.getLock());
		}

		lots = lotService.getToUseFromNow(client);
		for (Lot l : lots) {
			if (l.getLock() != LotLockState.LOT_TOO_YOUNG.getLock() && l.getLock() != LotLockState.LOT_EXPIRED.getLock() ) {
				log.warn(logStr+"Lot is locked. Will not set to NOT_LOCKED. lock="+l.getLock()+", lot="+ l.toUniqueString());
				continue;
			}
			log.info(logStr+"Lot will be unlocked from LotLockState.LOT_TOO_YOUNG. lot="+ l.toUniqueString());
			l.setLock(BusinessObjectLockState.NOT_LOCKED.getLock());
		}	
	}

	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByLot(Lot lot, boolean checkAvailable) {
		
		LOSStorageLocation nirwana = locationService.getNirwana();

		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT su FROM " + StockUnit.class.getSimpleName()+ " su, ");
        qstr.append(LOSUnitLoad.class.getSimpleName()+ " ul ");
		qstr.append("WHERE su.unitLoad = ul AND su.lot = :lot ");
    	qstr.append(" AND su.lock != "+StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());;
    	qstr.append(" AND su.lock != "+BusinessObjectLockState.GOING_TO_DELETE.getLock());;
    	qstr.append(" AND (su.amount-su.reservedAmount) > 0");
    	qstr.append(" AND ul.storageLocation != :nirwana");
		Query query = manager.createQuery(qstr.toString());

		query.setParameter("lot", lot);
		query.setParameter("nirwana", nirwana);

        return (List<StockUnit>) query.getResultList();
	}
}
