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
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.address.Address;
import de.wms2.mywms.address.AddressEntityService;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderEntityService;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.delivery.DeliveryOrderLineEntityService;
import de.wms2.mywms.delivery.DeliveryOrderStateChangeEvent;
import de.wms2.mywms.delivery.DeliveryReportGenerator;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderEntityService;
import de.wms2.mywms.picking.PickingOrderGenerator;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingOrderLineEntityService;
import de.wms2.mywms.picking.PickingOrderLineGenerator;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataEntityService;
import de.wms2.mywms.shipping.ShippingOrder;
import de.wms2.mywms.shipping.ShippingOrderEntityService;
import de.wms2.mywms.shipping.ShippingOrderLine;
import de.wms2.mywms.shipping.ShippingOrderLineEntityService;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.transport.TransportBusiness;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.transport.TransportOrderEntityService;


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
	@Inject
	private PickingOrderLineEntityService pickingPositionService;

	@EJB
	private LOSOrderBusiness orderBusiness;
	@Inject
	private ItemDataEntityService itemService;
	@Inject
	private PickingOrderLineGenerator pickingPosGenerator;
	@Inject
	private PickingOrderGenerator pickingOrderGenerator;
	@EJB
	private ContextService contextService;
	@Inject
	private OrderStrategyEntityService orderStratService;
	@Inject
	private DeliveryReportGenerator reportGenerator;
    @PersistenceContext(unitName = "myWMS")
    private  EntityManager manager;

    @Inject
    private PickingOrderEntityService pickingOrderEntityService;
	@Inject
	private PacketEntityService pickingUnitLoadService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private TransportOrderEntityService transportOrderService;
	@Inject
	private TransportBusiness transportBusiness;
	@Inject
	private ShippingOrderLineEntityService shippingOrderLineService;
	@Inject
	private ShippingOrderEntityService shippingOrderService;
	@Inject
	private AddressEntityService addressEntityService;
	@Inject
	private DeliveryOrderEntityService deliveryOrderService;
	@Inject
	private DeliveryOrderLineEntityService deliveryOrderLineService;
	@Inject
	private Event<DeliveryOrderStateChangeEvent> deliveryOrderStateChangeEvent;

	public DeliveryOrder order(String clientNumber, String externalNumber, OrderPositionTO[] positions,
			String documentUrl, String labelUrl, String destinationName, String orderStrategyName, Date deliveryDate,
			int prio, boolean startPicking, boolean completeOnly, String comment) throws FacadeException {
		return order(clientNumber, externalNumber, positions, documentUrl, labelUrl, destinationName, orderStrategyName,
				deliveryDate, prio, null, startPicking, completeOnly, comment);
	}

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
			Address address,
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
			strat = orderStratService.read(orderStrategyName);
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
		
		order = deliveryOrderService.create(client, strat);
		order.setPrio(prio);
		order.setAdditionalContent(comment);
		order.setExternalNumber(externalNumber);
		order.setDocumentUrl(documentUrl);
		order.setLabelUrl(labelUrl);
		order.setDestination(destination);
		order.setDeliveryDate(deliveryDate);
		order.setAddress(address == null ? new Address() : address);
		int line = 0;
		for( OrderPositionTO posTO : positions ) {
			ItemData item = itemService.readByNumber(posTO.articleRef);
			if( item == null ) {
				String msg = "Item data does not exist. number="+posTO.articleRef;
				log.error(logStr+msg);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
			}
			BigDecimal amount = posTO.amount;
			if( BigDecimal.ZERO.compareTo(amount)>=0 ) {
				String msg = "Amount must not be <= 0. amount="+posTO.amount+", item="+posTO.articleRef;
				log.error(logStr+msg);
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
			}
			
			deliveryOrderLineService.create(order, item, posTO.batchRef, amount, ++line);
		}

		if( startPicking ) {

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

		List<PickingOrderLine> pickList = pickingPositionService.readByDeliveryOrder(order);
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
		
		List<PickingOrderLine> pickList = pickingPositionService.readByDeliveryOrder(order);
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
		Set<Packet> pickingUnitLoadSetRemovable = new HashSet<Packet>();
		Set<ShippingOrder> goodsOutRequestSetRemovable = new HashSet<>();

		for( Long poId : pickingOrderSet1 ) {
			PickingOrder po = manager.find(PickingOrder.class, poId);
			if( po != null && po.getLines().size()==0 ) {
				pickingOrderSetRemovable.add(po);
				pickingUnitLoadSetRemovable.addAll( pickingUnitLoadService.readByPickingOrder(po) );
			}
		}


		// 4. Remove all LOSPickingUnitLoad and LOSGoodsOutRequestPosition with removable LOSPickingOrder
		for( Packet pul : pickingUnitLoadSetRemovable ) {
			UnitLoad ul = pul.getUnitLoad();
			

			List<TransportOrder> storageList = transportOrderService.readList(ul, null, null, null);
			for( TransportOrder storageReq : storageList ) {
				transportBusiness.removeUnitLoadReference(storageReq);
			}
			
			List<ShippingOrderLine> outPosList = shippingOrderLineService.readListByPacket(pul);
			for( ShippingOrderLine outPos : outPosList ) {
				goodsOutRequestSetRemovable.add(outPos.getShippingOrder());
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
		for( ShippingOrder outReq : goodsOutRequestSetRemovable ) {
			if( outReq != null && outReq.getLines().size()<=0 ) {
				manager.remove(outReq);
			}
		}
		
		// 7. Remove directly addressed shipping orders
		List<ShippingOrder> outList = shippingOrderService.readByDeliveryOrder(order);
		for( ShippingOrder outReq : outList ) {
			if( outReq == null ) {
				continue;
			}
			for( ShippingOrderLine outPos : outReq.getLines() ) {
				manager.remove(outPos);
			}
			manager.remove(outReq);
		}

		
		manager.flush();

		// 8. Remove address
		Address address = order.getAddress();
		if (address != null) {
			order.setAddress(null);
			addressEntityService.removeIfUnused(address);
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
	
	public Document generateReceipt( Long orderId ) throws FacadeException {
		String logStr = "generateReceipt ";
		DeliveryOrder order = manager.find(DeliveryOrder.class, orderId);
		if( order == null ) {
			String msg = "Customer order does not exist. id="+orderId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
		log.debug(logStr+"order number="+order.getOrderNumber());

		return  reportGenerator.generateDeliverynote(order);
	}
	
	public Document generateUnitLoadLabel( String label ) throws FacadeException {
		String logStr = "generateUnitLoadLabel ";
		log.debug(logStr+"label="+label);


		UnitLoad unitLoad = unitLoadService.readByLabel(label);
		if( unitLoad == null ) {
			String msg = "Unit load does not exist. label="+label;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		Document doc = reportGenerator.generateContentList(unitLoad);

		return doc;
	}

	public Document generatePacketLabel( Long packetId ) throws FacadeException {
		String logStr = "generatePacketLabel ";
		log.debug(logStr+"packetId="+packetId);

		Packet packet = manager.find(Packet.class, packetId);

		if( packet == null ) {
			String msg = "Packet does not exist. id="+packetId;
			log.error(logStr+msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}

		Document doc = reportGenerator.generateContentList(packet);
		
		return doc;
	}

	public Document generatePacketList(Long orderId) throws FacadeException {
		String logStr = "generatePacketList ";
		log.debug(logStr + "orderId=" + orderId);

		PickingOrder pickingOrder = null;
		DeliveryOrder deliveryOrder = null;
		ShippingOrder shippingOrder = manager.find(ShippingOrder.class, orderId);
		if (shippingOrder == null) {
			pickingOrder = manager.find(PickingOrder.class, orderId);
		}
		if (shippingOrder == null && pickingOrder == null) {
			deliveryOrder = manager.find(DeliveryOrder.class, orderId);
		}

		if (shippingOrder != null) {
			return reportGenerator.generatePacketList(shippingOrder);
		} else if (pickingOrder != null) {
			return reportGenerator.generatePacketList(pickingOrder);
		} else if (deliveryOrder != null) {
			return reportGenerator.generatePacketList(deliveryOrder);
		} else {
			String msg = "Order does not exist. id=" + orderId;
			log.error(logStr + msg);
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, msg);
		}
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
				
			List<PickingOrder> pickOrderList = pickingOrderEntityService.readByDeliveryOrder(deliveryOrder);
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
			if( stateOld<OrderState.PICKED) {
				deliveryOrder.setState(OrderState.PICKED);
			}
			if( deliveryOrder.getState() != stateOld ) {
				fireDeliveryOrderStateChangeEvent(deliveryOrder, stateOld);
			}
		}
	}

	private void fireDeliveryOrderStateChangeEvent(DeliveryOrder entity, int oldState) throws BusinessException {
		try {
			log.debug("Fire DeliveryOrderStateChangeEvent. entity=" + entity + ", state=" + entity.getState()
					+ ", oldState=" + oldState);
			deliveryOrderStateChangeEvent.fire(new DeliveryOrderStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
