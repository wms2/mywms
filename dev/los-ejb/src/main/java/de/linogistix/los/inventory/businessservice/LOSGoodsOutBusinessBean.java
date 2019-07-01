/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 * 
 * @author trautm
 * 
 */
@Stateless
public class LOSGoodsOutBusinessBean implements LOSGoodsOutBusiness {

	private static final Logger log = Logger
			.getLogger(LOSGoodsOutBusinessBean.class);


	@EJB
	private LOSGoodsOutRequestPositionService posService;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	@EJB
	private ManageOrderService manageOrderService;
	@EJB
	private LOSPickingUnitLoadService pickingUnitLoadService;
	@EJB
	private LOSUnitLoadService unitLoadService;
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private LOSStorageLocationService locationService;
	@EJB
	private LOSStorage storageService;
	@EJB
	private ContextService contextService;
	
	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out) throws FacadeException {
		return finish(out, false);
	}

	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out, boolean force) throws FacadeException {
		LOSGoodsOutRequestState stateOld = out.getOutState();
		for (LOSGoodsOutRequestPosition pos : out.getPositions()) {
			switch (pos.getOutState()) {
			case RAW:
				if (force) {
					finishPosition( out, pos.getSource() );
				} else {
					throw new InventoryException(
							InventoryExceptionKey.ORDER_NOT_FINIHED, "");
				}
			case FINISHED:
				// OK
				break;
			default:
				throw new RuntimeException("Unknown state: "
						+ pos.getOutState().toString());
			}
		}

		out.setOutState(LOSGoodsOutRequestState.FINISHED);
		LOSCustomerOrder customerOrder = out.getCustomerOrder();
		if( customerOrder != null ) {
			int orderStateOld = customerOrder.getState();
			if( orderStateOld >= State.PICKED && orderStateOld < State.FINISHED ) {
				for( LOSCustomerOrderPosition customerOrderPos : customerOrder.getPositions() ) {
					if( customerOrderPos.getState() == State.PICKED ) {
						customerOrderPos.setState(State.FINISHED);
						manageOrderService.onCustomerOrderPositionStateChange(customerOrderPos, State.PICKED);
						
					}
				}
				customerOrder.setState(State.FINISHED);
				manageOrderService.onCustomerOrderStateChange(customerOrder, orderStateOld);
			}
		}

		if( out.getOutState() != stateOld ) {
			manageOrderService.onGoodsOutOrderStateChange(out, stateOld);
		}
		return out;
	}
	
	public LOSGoodsOutRequest finishOrder(LOSGoodsOutRequest out) throws FacadeException {
		log.info("finishOrder LOSGoodsOutRequest: " + out.getNumber());
		if( out.getOutState() == LOSGoodsOutRequestState.FINISHED ) {
			log.warn("finishOrder LOSGoodsOutRequest: " + out.getNumber() + ". Order is already finished! Do nothing");
			return out;
		}
		LOSGoodsOutRequestState stateOld = out.getOutState();

		boolean hasCanceled = false;
		
		for(LOSGoodsOutRequestPosition pos : out.getPositions()) {
			if( pos.getOutState().equals(LOSGoodsOutRequestPositionState.FINISHED)) {
				continue;
			}
			hasCanceled = true;
			pos.setOutState(LOSGoodsOutRequestPositionState.FINISHED);
			manageOrderService.onGoodsOutPositionStateChange(pos, LOSGoodsOutRequestPositionState.RAW);
		}
		
		out.setOutState(LOSGoodsOutRequestState.FINISHED);
		
		
		LOSCustomerOrder customerOrder = out.getCustomerOrder();
		if( customerOrder != null && !hasCanceled ) {
			int orderStateOld = customerOrder.getState();
			if( orderStateOld >= State.PICKED && orderStateOld < State.FINISHED ) {
				customerOrder.setState(State.FINISHED);
				manageOrderService.onCustomerOrderStateChange(customerOrder, orderStateOld);
			}
		}

		manageOrderService.onGoodsOutOrderStateChange(out, stateOld);
		return out;
		
	}

	public LOSGoodsOutRequestPosition finishPosition(LOSGoodsOutRequest out, UnitLoad ul) throws FacadeException {
		String logStr = "finishPosition ";
		
		if( ul == null ) {
			log.warn(logStr+"Cannot finish without unit load");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, "");
		}

		LOSGoodsOutRequestPosition pos = posService.getByUnitLoad(out, ul);
		if( pos == null ) {
			log.warn(logStr+"Cannot find out-position for unit load. label="+ul.getLabelId());
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ORDERPOSITION, ul.getLabelId());
		}
		LOSGoodsOutRequestPositionState stateOld = pos.getOutState();

		pos.setOutState(LOSGoodsOutRequestPositionState.FINISHED);
		
		LOSPickingUnitLoad pickingUnitLoad = pickingUnitLoadService.getByLabel(ul.getLabelId());
		if( pickingUnitLoad != null ) {
			int unitLoadStateOld = pickingUnitLoad.getState();
			if( pickingUnitLoad.getState()<State.FINISHED ) {
				pickingUnitLoad.setState(State.FINISHED);
				manageOrderService.onPickingUnitLoadStateChange(pickingUnitLoad, unitLoadStateOld);
			}
		}
		
		List<UnitLoad> childList = unitLoadService.getChilds(ul); //ul.getUnitLoadList();
		for( UnitLoad child : childList ) {
			child.setLock(LOSUnitLoadLockState.SHIPPED.getLock());
			child.setState(StockState.SHIPPED);
			for(StockUnit stock:child.getStockUnitList()) {
				stock.setLock(LOSUnitLoadLockState.SHIPPED.getLock());
				int stockState = stock.getState();
				if (stockState < StockState.SHIPPED) {
					stock.setState(StockState.SHIPPED);
				}
			}
		}

		ul.setLock(LOSUnitLoadLockState.SHIPPED.getLock());
		ul.setState(StockState.SHIPPED);
		for(StockUnit stock:ul.getStockUnitList()) {
			stock.setLock(LOSUnitLoadLockState.SHIPPED.getLock());
			int stockState = stock.getState();
			if (stockState < StockState.SHIPPED) {
				stock.setState(StockState.SHIPPED);
			}
		}

		boolean rename = propertyService.getBooleanDefault(out.getClient(), null, LOSInventoryPropertyKey.SHIPPING_RENAME_UNITLOAD, false);
		if( rename ) {
			String addOn = "-X-"+ul.getId();
			if( !ul.getLabelId().endsWith(addOn)) {
				ul.setLabelId(ul.getLabelId()+addOn);
			}
		}
		
		String targetLocationName = propertyService.getStringDefault(out.getClient(), null, LOSInventoryPropertyKey.SHIPPING_LOCATION, null);
		if( targetLocationName!=null && targetLocationName.trim().length()>0 ) {
			StorageLocation targetLocation = locationService.getByName(targetLocationName);
			if( targetLocation == null ) {
				log.warn(logStr+"Cannot find configured location. name="+targetLocationName);
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, targetLocationName);
			}
			storageService.transferUnitLoad(contextService.getCallerUserName(), targetLocation, ul, -1, true, null, out.getNumber());
		}

		if( pos.getOutState() != stateOld ) {
			manageOrderService.onGoodsOutPositionStateChange(pos, stateOld);
		}

		return pos;

	}

	@SuppressWarnings("unchecked")
	public List<LOSGoodsOutRequestTO> getRaw() {
		User user = contextService.getCallersUser();
		Client c = user.getClient();

		StringBuffer b = new StringBuffer();
		Query query;
		
		b.append(" SELECT new "+LOSGoodsOutRequestTO.class.getName()+"(out.id, out.version, out.number, out.number, out.outState, out.client.number, cu.number, cu.externalNumber, cu.externalId, cu.customerNumber, cu.customerName, out.shippingDate) FROM ");
		b.append(LOSGoodsOutRequest.class.getSimpleName() + " out ");
		b.append("left outer join out.customerOrder as cu ");
		b.append(" WHERE ( out.outState=:raw ");
		b.append(" or (out.outState=:processing and out.operator=:user) )");
		if (!c.isSystemClient()) {
			b.append(" AND out.client=:client ");
		}
		b.append(" ORDER BY out.number ");
		
		query = manager.createQuery(b.toString());
		
		query.setParameter("raw", LOSGoodsOutRequestState.RAW);
		query.setParameter("processing", LOSGoodsOutRequestState.PROCESSING);
		query.setParameter("user", user);
		if (!c.isSystemClient()) {
			query.setParameter("client", c);
		}

		return query.getResultList();
	}

	public LOSGoodsOutRequest accept(LOSGoodsOutRequest req) throws FacadeException {
		log.info("start LOSGoodsOutRequest: " + req.getNumber());

		if( req.getOutState() == LOSGoodsOutRequestState.FINISHED ) {
			log.error("cancel LOSGoodsOutRequest: " + req.getNumber() + ". Order is already finished! Cannot cancel");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, new Object[]{});
		}

		LOSGoodsOutRequestState stateOld = req.getOutState();
		
		req.setOperator(contextService.getCallersUser());
		req.setOutState(LOSGoodsOutRequestState.PROCESSING);

		if( req.getOutState() != stateOld ) {
			manageOrderService.onGoodsOutOrderStateChange(req, stateOld);
		}

		return req;
	}

	public LOSGoodsOutRequest cancel(LOSGoodsOutRequest req) throws FacadeException {
		log.info("cancel LOSGoodsOutRequest: " + req.getNumber());
		if( req.getOutState() == LOSGoodsOutRequestState.FINISHED ) {
			log.error("cancel LOSGoodsOutRequest: " + req.getNumber() + ". Order is already finished! Cannot cancel");
			throw new InventoryException(InventoryExceptionKey.ORDER_ALREADY_FINISHED, new Object[]{});
		}
		LOSGoodsOutRequestState stateOld = req.getOutState();

		boolean hasOpen = false;
		boolean hasClosed = false;
		for(LOSGoodsOutRequestPosition pos : req.getPositions()) {
			if( pos.getOutState().equals(LOSGoodsOutRequestPositionState.RAW)) {
				hasOpen = true;
			}
			else if( pos.getOutState().equals(LOSGoodsOutRequestPositionState.FINISHED)) {
				hasClosed = true;
			}
		}
		
		if( hasOpen == false ) {
			log.warn("No Open positions found for LOSGoodsOutRequest " + req.getNumber() + ". => It will be finished");
			req.setOutState(LOSGoodsOutRequestState.FINISHED);
		}
		else {
			if( hasClosed == false ) {
				req.setOperator(null);
			}
			req.setOutState(LOSGoodsOutRequestState.RAW);
		}
		
		if( req.getOutState() != stateOld ) {
			manageOrderService.onGoodsOutOrderStateChange(req, stateOld);
		}
		
		return req;
	}

	public void remove(LOSGoodsOutRequest req) throws FacadeException {
		log.info("remove LOSGoodsOutRequest: " + req.getNumber());
		if( req.getOutState() != LOSGoodsOutRequestState.FINISHED ) {
			log.error("cancel LOSGoodsOutRequest: " + req.getNumber() + ". Order is not finished! Cannot remove");
			throw new InventoryException(InventoryExceptionKey.ORDER_NOT_FINISHED, req.getNumber());
		}
		
		for(LOSGoodsOutRequestPosition pos : req.getPositions()) {
			manager.remove(pos);
		}
		manager.remove(req);
		
	}

}
