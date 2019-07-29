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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.Document;
import org.mywms.service.ClientService;

import de.linogistix.los.inventory.businessservice.LOSGoodsOutGenerator;
import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.businessservice.LOSOrderGenerator;
import de.linogistix.los.inventory.businessservice.StorageBusiness;
import de.linogistix.los.inventory.customization.ManageOrderService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.report.LOSOrderReceiptReport;
import de.linogistix.los.inventory.report.LOSUnitLoadReport;
import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.address.Address;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderEntityService;
import de.wms2.mywms.picking.PickingOrderGenerator;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingOrderLineGenerator;
import de.wms2.mywms.picking.PickingUnitLoad;
import de.wms2.mywms.picking.PickingUnitLoadEntityService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;


// TODO i18n

/**
 * @author krane
 *
 */
@Stateless
public class LOSOrderFacadeBean implements LOSOrderFacade {
	private Logger log = Logger.getLogger(LOSOrderFacadeBean.class);
	
	@EJB
	private ClientService clientService;
	@EJB
	private LOSCustomerOrderService orderService;
	@EJB
	private LOSPickingPositionService pickingPositionService;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@EJB
	private LOSOrderGenerator orderGenerator;
	@EJB
	private ItemDataService itemService;
	@EJB
	private QueryLotService lotService;
	@Inject
	private PickingOrderLineGenerator pickingPosGenerator;
	@Inject
	private PickingOrderGenerator pickingOrderGenerator;
	@EJB
	private ContextService contextService;
	@EJB
	private OrderStrategyEntityService orderStratService;
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
	private LOSUnitLoadReport unitLoadReport;
    @PersistenceContext(unitName = "myWMS")
    private  EntityManager manager;

    @Inject
    private PickingOrderEntityService pickingOrderEntityService;
	@Inject
	private PickingUnitLoadEntityService pickingUnitLoadService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private GenericEntityService entityService;

	public DeliveryOrder order(
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
		
		DeliveryOrder order;

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

		OrderStrategy strat = null;
		if( StringTools.isEmpty(orderStrategyName) ) {
			strat = orderStratService.getDefault(client);
		}
		else {
			strat = orderStratService.read(client, orderStrategyName);
		}
		if( strat == null ){
			String msg = "OrderStrategy does not exist. name="+orderStrategyName;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		
		StorageLocation destination = null;
		if( destinationName != null && destinationName.length()>0 ) {
			destination = locationService.read(destinationName);
			if( destination == null ){
				String msg = "Location does not exist. name="+destination;
				log.error(logStr+msg);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
			}
		}
		
		order = orderGenerator.createDeliveryOrder(client, strat);
		order.setPrio(prio);
		order.setAdditionalContent(comment);
		order.setExternalNumber(externalNumber);
		order.setDocumentUrl(documentUrl);
		order.setLabelUrl(labelUrl);
		order.setDestination(destination);
		order.setDeliveryDate(deliveryDate);

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
			
			orderGenerator.addDeliveryOrderLine(order, item, lot, null, amount);
		}

		if( startPicking ) {
			try {
				List<PickingOrderLine> pickList;
				pickList = pickingPosGenerator.generatePicks(order, completeOnly);
				if (pickList != null && pickList.size() > 0) {
					Collection<PickingOrder> pickingOrders = pickingOrderGenerator.generatePickingOrders(pickList);
					for (PickingOrder pickingOrder : pickingOrders) {
						pickingOrder.setDestination(destination);
						pickingOrder.setPrio(prio);
						orderBusiness.releasePickingOrder(pickingOrder);
					}
				}
			} catch (BusinessException e) {
				throw e.toFacadeException();
			}
		}
		
		return order;
	}


	public DeliveryOrder finishOrder(Long orderId) throws FacadeException {
		String logStr = "finishOrder ";
		
		DeliveryOrder order = manager.find(DeliveryOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getOrderNumber());

		List<PickingOrderLine> pickList = pickingPositionService.getByDeliveryOrder(order);
		Set<PickingOrder> pickingOrderSet = new HashSet<PickingOrder>();
		for( PickingOrderLine pick : pickList ) {
			PickingOrder po = pick.getPickingOrder();
			if( po != null ) {
				pickingOrderSet.add(po);
			}
			orderBusiness.cancelPick(pick);
		}
		for( PickingOrder po : pickingOrderSet ) {
			orderBusiness.recalculatePickingOrderState(po);
		}
		orderBusiness.finishDeliveryOrder(order);
		
		return order;
	}
	
	public void removeOrder(Long orderId) throws FacadeException {
		String logStr = "removeOrder ";
		
		DeliveryOrder order = manager.find(DeliveryOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getOrderNumber());
		
		List<PickingOrderLine> pickList = pickingPositionService.getByDeliveryOrder(order);
		Set<Long> pickingOrderSet1 = new HashSet<Long>();
		
		// 1. Remove all picks
		for( PickingOrderLine pick : pickList ) {
			PickingOrder po = pick.getPickingOrder();
			if( po != null ) {
				pickingOrderSet1.add(po.getId());
			}
			if( pick.getState()<State.PICKED ) {
				orderBusiness.cancelPick(pick);
			}
			manager.remove(pick);
		}
		
		// 2. remove all customer order positions
		for( DeliveryOrderLine pos : order.getLines() ) {
			manager.remove(pos);
		}

		manager.flush();
		
		// 3. Find all LOSPickingOrder without position 
		Set<PickingOrder> pickingOrderSetRemovable = new HashSet<PickingOrder>();
		Set<PickingUnitLoad> pickingUnitLoadSetRemovable = new HashSet<PickingUnitLoad>();
		Set<LOSGoodsOutRequest> goodsOutRequestSetRemovable = new HashSet<LOSGoodsOutRequest>();

		for( Long poId : pickingOrderSet1 ) {
			PickingOrder po = manager.find(PickingOrder.class, poId);
			if( po != null && po.getLines().size()==0 ) {
				pickingOrderSetRemovable.add(po);
				pickingUnitLoadSetRemovable.addAll( pickingUnitLoadService.getByPickingOrder(po) );
			}
		}


		// 4. Remove all LOSPickingUnitLoad and LOSGoodsOutRequestPosition with removable LOSPickingOrder
		for( PickingUnitLoad pul : pickingUnitLoadSetRemovable ) {
			UnitLoad ul = pul.getUnitLoad();
			

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
		for( PickingOrder po : pickingOrderSetRemovable ) {
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
		List<LOSGoodsOutRequest> outList = outService.getByDeliveryOrder(order);
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

		// 8. Remove address
		Address address = order.getAddress();
		if( address!=null) {
			if(entityService.exists(DeliveryOrder.class, "address", address, order.getId())) {
				order.setAddress(null);
			}
		}
		// 9. Remove order
		manager.remove(order);
	}

	
	public List<String> getGoodsOutLocations() throws FacadeException {
		String logStr = "getGoodsOutLocations ";
		log.debug(logStr);
		
		
		List<StorageLocation> slList;
		slList = locationService.getForGoodsOut(null);

		if (slList.size() == 0) {
			throw new LOSLocationException(
					// LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new
					// Object[0]);
					LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION,
					new Object[0]);
		}
		
		List<String> ret = new ArrayList<String>();

		for (StorageLocation sl : slList) {
			ret.add(sl.getName());
		}
		return ret;
	}
	
	public List<BODTO<StorageLocation>> getGoodsOutLocationsBO() throws FacadeException {
		String logStr = "getGoodsOutLocationsBO ";
		log.debug(logStr);
		
		
		List<StorageLocation> slList;
		slList = locationService.getForGoodsOut(null);

		if (slList.size() == 0) {
			throw new LOSLocationException(
					// LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new
					// Object[0]);
					LOSLocationExceptionKey.NO_GOODS_OUT_LOCATION,
					new Object[0]);
		}
		
		List<BODTO<StorageLocation>> ret = new ArrayList<BODTO<StorageLocation>>();

		for (StorageLocation sl : slList) {
			ret.add( new BODTO<StorageLocation>(sl) );
		}
		return ret;
	}

	public void changeOrderPrio( Long orderId, int prio ) throws FacadeException {
		String logStr = "changeOrderPrio ";
		DeliveryOrder order = manager.find(DeliveryOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getOrderNumber());

		order.setPrio(prio);
		
		List<PickingOrder> poList = pickingOrderEntityService.readAllByDeliveryOrder(order);
		for( PickingOrder po : poList ) {
			int prioOld = po.getPrio();
			if( prio != prioOld ) {
				po.setPrio(prio);
				manageOrderService.onPickingOrderPrioChange(po, prioOld);
			}
		}
	}
	
	
	public Document generateReceipt( Long orderId, boolean save ) throws FacadeException {
		String logStr = "generateReceipt ";
		DeliveryOrder order = manager.find(DeliveryOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getOrderNumber());

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


		UnitLoad unitLoad = unitLoadService.read(label);
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
	
	public void processOrderPickedFinish(List<BODTO<DeliveryOrder>> orders) throws FacadeException {
		String logStr = "processOrderPickedFinish ";
		if (orders == null) {
			return;
		}
	
		for (BODTO<DeliveryOrder> order : orders) {
			DeliveryOrder deliveryOrder = manager.find(DeliveryOrder.class, order.getId());
			if( deliveryOrder==null ) {
				continue;
			}
			log.debug(logStr+"Order="+deliveryOrder.getOrderNumber());
				
			List<PickingOrder> pickOrderList = pickingOrderEntityService.readAllByDeliveryOrder(deliveryOrder);
			for( PickingOrder pickingOrder : pickOrderList ) {
				if( pickingOrder.getState() >= State.FINISHED  ) {
					continue;
				}
				orderBusiness.finishPickingOrder(pickingOrder);
			}
				
			for (DeliveryOrderLine pos : deliveryOrder.getLines()) {
				pos = manager.find(DeliveryOrderLine.class, pos.getId());
				log.debug(logStr+"Check pos="+pos.getLineNumber()+", state="+pos.getState());
			
				if( pos.getState() == State.PENDING ) {
					log.info("processOrderPicked: force closing pending position. pos=" + pos.getLineNumber());
					pos.setState(State.PICKED);
				}
			}

			int stateOld = deliveryOrder.getState();
			OrderStrategy strat = deliveryOrder.getOrderStrategy();
			if( strat != null && strat.isCreateShippingOrder() ) {
				goodsOutGenerator.createOrder(deliveryOrder);
				deliveryOrder.setState(State.PICKED);
			}
			else {
				deliveryOrder.setState(State.FINISHED);
			}
			if( deliveryOrder.getState() != stateOld ) {
				manageOrderService.onDeliveryOrderStateChange(deliveryOrder, stateOld);
			}
		}
	}

}
