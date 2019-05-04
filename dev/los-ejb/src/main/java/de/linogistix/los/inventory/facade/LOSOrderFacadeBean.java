/*
 * Copyright (c) 2011-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.Document;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.ClientService;
import org.mywms.service.ItemDataService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.businessservice.LOSGoodsOutGenerator;
import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.businessservice.LOSOrderGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingOrderGenerator;
import de.linogistix.los.inventory.businessservice.LOSPickingPosGenerator;
import de.linogistix.los.inventory.businessservice.StorageBusiness;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.report.LOSOrderReceiptReport;
import de.linogistix.los.inventory.report.LOSUnitLoadReport;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.inventory.service.LOSPickingUnitLoadService;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;


// TODO i18n

/**
 * @author krane
 *
 */
@Stateless
public class LOSOrderFacadeBean implements LOSOrderFacade {
	private Logger log = Logger.getLogger(LOSOrderFacadeBean.class);
	
	@EJB
	private LOSStorageLocationService slService1;
	@EJB
	private QueryStorageLocationService slService2;
	@EJB
	private ClientService clientService;
	@EJB
	private LOSCustomerOrderService orderService;
	@EJB
	private LOSPickingPositionService pickingPositionService;
	@EJB
	private LOSPickingOrderService pickingOrderService;
	@EJB
	private LOSPickingUnitLoadService pickingUnitLoadService;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private LOSOrderGenerator orderGenerator;
	@EJB
	private ItemDataService itemService;
	@EJB
	private QueryLotService lotService;
	@EJB
	private LOSPickingPosGenerator pickingPosGenerator;
	@EJB
	private LOSPickingOrderGenerator pickingOrderGenerator;
	@EJB
	private ContextService contextService;
	@EJB
	private LOSOrderStrategyService orderStratService;
	@EJB
	private ManageOrderService manageOrderService;
	@EJB
	private LOSOrderReceiptReport receiptService;
	@EJB
	private LOSGoodsOutRequestPositionService outPosService;
	@EJB
	private LOSGoodsOutRequestService outService;
	@EJB
	private StorageBusiness storageBusiness;
	@EJB
	private LOSStorageRequestService storageService;
	@EJB
	private LOSGoodsOutGenerator goodsOutGenerator;
	@EJB
	private QueryUnitLoadService unitLoadService;
	@EJB
	private LOSUnitLoadReport unitLoadReport;
    @PersistenceContext(unitName = "myWMS")
    private  EntityManager manager;

	public LOSCustomerOrder order(
			String clientNumber,
			String externalNumber,
			OrderPositionTO[] positions,
			String documentUrl,
			String labelUrl,
			String destinationName, 
			String orderStrategyName,
			Date deliveryDate, 
			int prio,
			boolean startPicking, boolean completeOnly,
			String comment) throws FacadeException {
		String logStr = "order ";
		log.debug(logStr);
		
		LOSCustomerOrder order;

		Client client = null;
		if( StringTools.isEmpty(clientNumber) ) {
			client = contextService.getCallersClient();
		}
		else {
			client = clientService.getByNumber(clientNumber);
		}
		if( client == null ){
			String msg = "Client does not exist. number="+clientNumber;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		if(deliveryDate == null){
			deliveryDate = new Date(System.currentTimeMillis() + (24 * 3600 * 1000));
		}

		LOSOrderStrategy strat = null;
		if( StringTools.isEmpty(orderStrategyName) ) {
			strat = orderStratService.getDefault(client);
		}
		else {
			strat = orderStratService.getByName(client, orderStrategyName);
		}
		if( strat == null ){
			String msg = "OrderStrategy does not exist. name="+orderStrategyName;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		
		LOSStorageLocation destination = null;
		if( destinationName != null && destinationName.length()>0 ) {
			destination = slService1.getByName(destinationName);
			if( destination == null ){
				String msg = "Location does not exist. name="+destination;
				log.error(logStr+msg);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
			}
		}
		
		order = orderGenerator.createCustomerOrder(client, strat);
		order.setPrio(prio);
		order.setAdditionalContent(comment);
		order.setExternalNumber(externalNumber);
		order.setDocumentUrl(documentUrl);
		order.setLabelUrl(labelUrl);
		order.setDestination(destination);
		order.setDelivery(deliveryDate);

		for( OrderPositionTO posTO : positions ) {
			ItemData item = itemService.getByItemNumber(client, posTO.articleRef);
			if( item == null ) {
				String msg = "Item data does not exist. number="+posTO.articleRef;
				log.error(logStr+msg);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
			}
			Lot lot = null;
			if( posTO.batchRef != null && posTO.batchRef.length()>0 ) {
				lot = lotService.getByNameAndItemData(posTO.batchRef, item);
				if( lot == null ) {
					String msg = "Lot data does not exist. name="+posTO.batchRef+", item="+posTO.articleRef;
					log.error(logStr+msg);
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
				}
			}
			BigDecimal amount = posTO.amount;
			if( BigDecimal.ZERO.compareTo(amount)>=0 ) {
				String msg = "Amount must not be <= 0. amount="+posTO.amount+", item="+posTO.articleRef;
				log.error(logStr+msg);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
			}
			
			orderGenerator.addCustomerOrderPos(order, item, lot, null, amount);
		}

		if( startPicking ) {
			List<LOSPickingPosition> pickList;
			pickList = pickingPosGenerator.generatePicks(order, completeOnly);
			if( pickList != null && pickList.size()>0 ) {
				List<LOSPickingOrder> pickingOrders = pickingOrderGenerator.createOrders(pickList);
				for( LOSPickingOrder pickingOrder : pickingOrders ) {
					pickingOrder.setDestination(destination);
					pickingOrder.setPrio(prio);
					orderBusiness.releasePickingOrder(pickingOrder);
				}
			}
		}
		
		return order;
	}


	public LOSCustomerOrder finishOrder(Long orderId) throws FacadeException {
		String logStr = "finishOrder ";
		
		LOSCustomerOrder order = manager.find(LOSCustomerOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getNumber());

		List<LOSPickingPosition> pickList = pickingPositionService.getByCustomerOrder(order);
		Set<LOSPickingOrder> pickingOrderSet = new HashSet<LOSPickingOrder>();
		for( LOSPickingPosition pick : pickList ) {
			LOSPickingOrder po = pick.getPickingOrder();
			if( po != null ) {
				pickingOrderSet.add(po);
			}
			orderBusiness.cancelPick(pick);
		}
		for( LOSPickingOrder po : pickingOrderSet ) {
			orderBusiness.recalculatePickingOrderState(po);
		}
		orderBusiness.finishCustomerOrder(order);
		
		return order;
	}
	
	public void removeOrder(Long orderId) throws FacadeException {
		String logStr = "removeOrder ";
		
		LOSCustomerOrder order = manager.find(LOSCustomerOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getNumber());
		
		List<LOSPickingPosition> pickList = pickingPositionService.getByCustomerOrder(order);
		Set<Long> pickingOrderSet1 = new HashSet<Long>();
		
		// 1. Remove all picks
		for( LOSPickingPosition pick : pickList ) {
			LOSPickingOrder po = pick.getPickingOrder();
			if( po != null ) {
				pickingOrderSet1.add(po.getId());
			}
			if( pick.getState()<State.PICKED ) {
				orderBusiness.cancelPick(pick);
			}
			manager.remove(pick);
		}
		
		// 2. remove all customer order positions
		for( LOSCustomerOrderPosition pos : order.getPositions() ) {
			manager.remove(pos);
		}

		manager.flush();
		
		// 3. Find all LOSPickingOrder without position 
		Set<LOSPickingOrder> pickingOrderSetRemovable = new HashSet<LOSPickingOrder>();
		Set<LOSPickingUnitLoad> pickingUnitLoadSetRemovable = new HashSet<LOSPickingUnitLoad>();
		Set<LOSGoodsOutRequest> goodsOutRequestSetRemovable = new HashSet<LOSGoodsOutRequest>();

		for( Long poId : pickingOrderSet1 ) {
			LOSPickingOrder po = manager.find(LOSPickingOrder.class, poId);
			if( po != null && po.getPositions().size()==0 ) {
				pickingOrderSetRemovable.add(po);
				pickingUnitLoadSetRemovable.addAll( pickingUnitLoadService.getByPickingOrder(po) );
			}
		}


		// 4. Remove all LOSPickingUnitLoad and LOSGoodsOutRequestPosition with removable LOSPickingOrder
		for( LOSPickingUnitLoad pul : pickingUnitLoadSetRemovable ) {
			LOSUnitLoad ul = pul.getUnitLoad();
			

			List<LOSStorageRequest> storageList = storageService.getListByUnitLoad(ul);
			for( LOSStorageRequest storageReq : storageList ) {
				storageBusiness.removeStorageRequest(storageReq);
			}
			
			List<LOSGoodsOutRequestPosition> outPosList = outPosService.getByUnitLoad(ul);
			for( LOSGoodsOutRequestPosition outPos : outPosList ) {
				goodsOutRequestSetRemovable.add(outPos.getGoodsOutRequest());
				manager.remove(outPos);
			}
			
			for( StockUnit su : ul.getStockUnitList() ) {
				manager.remove(su);
			}
			manager.remove(pul);
			manager.remove(ul);
		}

		// 5. Remove empty picking orders
		for( LOSPickingOrder po : pickingOrderSetRemovable ) {
			manager.remove(po);
		}
		
		manager.flush();
		
		// 6. Remove empty shipping orders
		for( LOSGoodsOutRequest outReq : goodsOutRequestSetRemovable ) {
			if( outReq != null && outReq.getPositions().size()<=0 ) {
				manager.remove(outReq);
			}
		}
		
		// 7. Remove directly addressed shipping orders
		List<LOSGoodsOutRequest> outList = outService.getByCustomerOrder(order);
		for( LOSGoodsOutRequest outReq : outList ) {
			if( outReq == null ) {
				continue;
			}
			for( LOSGoodsOutRequestPosition outPos : outReq.getPositions() ) {
				manager.remove(outPos);
			}
			manager.remove(outReq);
		}

		
		manager.flush();

		// 7. Remove order
		manager.remove(order);
	}

	
	public List<String> getGoodsOutLocations() throws FacadeException {
		String logStr = "getGoodsOutLocations ";
		log.debug(logStr);
		
		
		List<LOSStorageLocation> slList;
		slList = slService2.getListForGoodsOut();

		if (slList.size() == 0) {
			throw new LOSLocationException(
					// LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new
					// Object[0]);
					LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION,
					new Object[0]);
		}
		
		List<String> ret = new ArrayList<String>();

		for (LOSStorageLocation sl : slList) {
			ret.add(sl.getName());
		}
		return ret;
	}
	
	public List<BODTO<LOSStorageLocation>> getGoodsOutLocationsBO() throws FacadeException {
		String logStr = "getGoodsOutLocationsBO ";
		log.debug(logStr);
		
		
		List<LOSStorageLocation> slList;
		slList = slService2.getListForGoodsOut();

		if (slList.size() == 0) {
			throw new LOSLocationException(
					// LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new
					// Object[0]);
					LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION,
					new Object[0]);
		}
		
		List<BODTO<LOSStorageLocation>> ret = new ArrayList<BODTO<LOSStorageLocation>>();

		for (LOSStorageLocation sl : slList) {
			ret.add( new BODTO<LOSStorageLocation>(sl) );
		}
		return ret;
	}

	public void changeOrderPrio( Long orderId, int prio ) throws FacadeException {
		String logStr = "changeOrderPrio ";
		LOSCustomerOrder order = manager.find(LOSCustomerOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getNumber());

		order.setPrio(prio);
		
		List<LOSPickingOrder> poList = pickingOrderService.getByCustomerOrder(order);
		for( LOSPickingOrder po : poList ) {
			int prioOld = po.getPrio();
			if( prio != prioOld ) {
				po.setPrio(prio);
				manageOrderService.onPickingOrderPrioChange(po, prioOld);
			}
		}
	}
	
	
	public Document generateReceipt( Long orderId, boolean save ) throws FacadeException {
		String logStr = "generateReceipt ";
		LOSCustomerOrder order = manager.find(LOSCustomerOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getNumber());

		OrderReceipt receipt = null;
		receipt = receiptService.generateOrderReceipt(order);
		if( save && receipt != null ) {
			receiptService.storeOrderReceipt(receipt);
		}
		
		return receipt;
	}
	public Document generateUnitLoadLabel( String label, boolean save ) throws FacadeException {
		String logStr = "generateUnitLoadLabel ";
		log.debug(logStr+"label="+label);


		LOSUnitLoad unitLoad = null;
		try {
			unitLoad = unitLoadService.getByLabelId(label);
		} catch (UnAuthorizedException e) {
		}
		if( unitLoad == null ) {
			String msg = "Unit load does not exist. label="+label;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		PickReceipt doc = null;
		doc = unitLoadReport.generateUnitLoadReport(unitLoad);
		if( save && doc != null ) {
			unitLoadReport.storeUnitLoadReport(doc);
		}
		
		return doc;
	}
	
	public void processOrderPickedFinish(List<BODTO<LOSCustomerOrder>> orders) throws FacadeException {
		String logStr = "processOrderPickedFinish ";
		if (orders == null) {
			return;
		}
	
		for (BODTO<LOSCustomerOrder> order : orders) {
			LOSCustomerOrder customerOrder = manager.find(LOSCustomerOrder.class, order.getId());
			if( customerOrder==null ) {
				continue;
			}
			log.debug(logStr+"Order="+customerOrder.getNumber());
				
			List<LOSPickingOrder> pickOrderList = pickingOrderService.getByCustomerOrder(customerOrder);
			for( LOSPickingOrder pickingOrder : pickOrderList ) {
				if( pickingOrder.getState() >= State.FINISHED  ) {
					continue;
				}
				orderBusiness.finishPickingOrder(pickingOrder);
			}
				
			for (LOSCustomerOrderPosition pos : customerOrder.getPositions()) {
				pos = manager.find(LOSCustomerOrderPosition.class, pos.getId());
				log.debug(logStr+"Check pos="+pos.getNumber()+", state="+pos.getState());
			
				if( pos.getState() == State.PENDING ) {
					log.info("processOrderPicked: force closing pending position. pos=" + pos.getNumber());
					pos.setState(State.PICKED);
				}
			}

			int stateOld = customerOrder.getState();
			LOSOrderStrategy strat = customerOrder.getStrategy();
			if( strat != null && strat.isCreateGoodsOutOrder() ) {
				goodsOutGenerator.createOrder(customerOrder);
				customerOrder.setState(State.PICKED);
			}
			else {
				customerOrder.setState(State.FINISHED);
			}
			if( customerOrder.getState() != stateOld ) {
				manageOrderService.onCustomerOrderStateChange(customerOrder, stateOld);
			}
		}
	}

}
