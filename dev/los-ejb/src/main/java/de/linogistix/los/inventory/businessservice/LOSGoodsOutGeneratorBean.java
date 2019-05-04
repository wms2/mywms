/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.model.State;

/**
 * @author krane
 *
 */
@Stateless
public class LOSGoodsOutGeneratorBean implements LOSGoodsOutGenerator {
	private static final Logger log = Logger.getLogger(LOSGoodsOutGeneratorBean.class);

	@EJB
	private InventoryGeneratorService genService;

	@EJB
	private LOSGoodsOutRequestService outService;
	
	@EJB
	private LOSGoodsOutRequestPositionService posService;

	@EJB
	private EntityGenerator entityGenerator;
	
	@EJB
	private LOSPickingUnitLoadService pickingUnitLoadService;
	@EJB
	private ManageOrderService manageOrderService;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	public LOSGoodsOutRequest createOrder( LOSCustomerOrder customerOrder ) throws FacadeException {
		String logStr = "createOrder ";
		if( customerOrder == null ) {
			log.warn(logStr+"Missing parameter customerOrder");
			return null;
		}
		log.debug(logStr+"Create shipment for customer order number="+customerOrder.getNumber());
		
		LOSGoodsOutRequest shipment = null;
		
		List<LOSGoodsOutRequest> goodsOutList = outService.getByCustomerOrder(customerOrder);
		for( LOSGoodsOutRequest s : goodsOutList ) {
			if( s.getOutState() == LOSGoodsOutRequestState.RAW ) {
				shipment = s;
				break;
			}
		}
		
		List<LOSPickingUnitLoad> unitLoadList = pickingUnitLoadService.getByCustomerOrderNumber(customerOrder.getNumber());
		log.debug(logStr+"Found unit loads. num="+unitLoadList.size());

		for( LOSPickingUnitLoad unitLoad : unitLoadList ) {
			if( unitLoad.getState()<State.PICKED || unitLoad.getState()>=State.FINISHED ) {
				log.info(logStr+"Found unit load in invalid state. label="+unitLoad.getUnitLoad().getLabelId()+", state="+unitLoad.getState());
				continue;
			}
			
			boolean posExists = false;
			List<LOSGoodsOutRequestPosition> posList = posService.getByUnitLoad(unitLoad.getUnitLoad());
			for( LOSGoodsOutRequestPosition pos2 : posList ) {
				if( pos2.getOutState() == LOSGoodsOutRequestPositionState.RAW ) {
					posExists = true;
					break;
				}
			}
			if( !posExists ) {
				if( shipment == null ) {
					shipment = entityGenerator.generateEntity(LOSGoodsOutRequest.class);
					shipment.setClient(customerOrder.getClient());
					shipment.setCustomerOrder(customerOrder);
					shipment.setNumber(genService.generateGoodsOutNumber(customerOrder.getClient()));
					shipment.setExternalNumber(customerOrder.getExternalNumber());
					manager.persist(shipment);
					manager.flush();
					
					manageOrderService.onGoodsOutOrderStateChange(shipment, null);
				}
				LOSGoodsOutRequestPosition pos = null;
				pos = entityGenerator.generateEntity(LOSGoodsOutRequestPosition.class);
				pos.setSource(unitLoad.getUnitLoad());
				pos.setGoodsOutRequest(shipment);
				pos.setOutState(LOSGoodsOutRequestPositionState.RAW);
				manager.persist(pos);
				manager.flush();
				
				manageOrderService.onGoodsOutPositionStateChange(pos, null);
				
				if( shipment.getPositions() == null ) {
					shipment.setPositions(new ArrayList<LOSGoodsOutRequestPosition>());
				}
				shipment.getPositions().add(pos);
			}
		}

		return shipment;
	}

	public LOSGoodsOutRequest createOrder( Client client, LOSStorageLocation outLocation, String shipmentNumber, Date shippingDate, String courier, String additionalInfo) throws FacadeException {
		String logStr = "createOrder ";
		log.debug(logStr+"Create shipment for shipment number="+shipmentNumber);

		LOSGoodsOutRequest shipment = null;
		
		shipment = entityGenerator.generateEntity(LOSGoodsOutRequest.class);
		
		
		shipment.setClient(client);
		shipment.setNumber(genService.generateGoodsOutNumber(client));
		shipment.setShippingDate(shippingDate);
		shipment.setExternalNumber(shipmentNumber);
		shipment.setPositions(new ArrayList<LOSGoodsOutRequestPosition>());
		shipment.setOutLocation(outLocation);
		shipment.setCourier(courier);
		shipment.setAdditionalContent(additionalInfo);
		
		manager.persist(shipment);
		manager.flush();
		
		manageOrderService.onGoodsOutOrderStateChange(shipment, null);

		return shipment;
	}

	@Override
	public LOSGoodsOutRequestPosition addPosition(LOSGoodsOutRequest out, LOSUnitLoad unitLoad) throws FacadeException{
		String logStr = "addPosition ";
		List<LOSGoodsOutRequest> reqList = outService.getByUnitLoad(unitLoad);

		for(LOSGoodsOutRequest req : reqList) {
			if( req.getOutState()!=LOSGoodsOutRequestState.FINISHED ) {
				log.warn(logStr+"There is already a goods out for unitload. label="+unitLoad.getLabelId()+", number="+req.getNumber());
				throw new InventoryException(InventoryExceptionKey.GOODS_OUT_EXISTS_FOR_UNITLOAD, new String[]{unitLoad.getLabelId(), req.getNumber()});
			} 
		}		
		LOSGoodsOutRequestPosition pos = entityGenerator.generateEntity(LOSGoodsOutRequestPosition.class);
		pos.setSource(unitLoad);
		pos.setGoodsOutRequest(out);
		pos.setOutState(LOSGoodsOutRequestPositionState.RAW);
		
		manager.persist(pos);
		
		return pos;
	}
	
	@Override
	public void removePosition(LOSGoodsOutRequest out, LOSUnitLoad unitLoad) throws InventoryException {
		String logStr = "removePosition ";
		LOSGoodsOutRequestPosition pos = null;
		pos = posService.getByUnitLoad(out, unitLoad);
		if( pos == null ) {
			log.warn(logStr+"Position not found => Position not removed");
			return;
		}
		if( !pos.getGoodsOutRequest().equals(out) ) {
			log.warn(logStr+"The position is assigned to a different goods out request");
			throw new InventoryException(InventoryExceptionKey.POSITION_CANNOT_BE_REMOVED, new String[]{unitLoad.getLabelId(), out.getNumber()});
		}
			
		manager.remove(pos);
		manager.flush();
	}
	

}