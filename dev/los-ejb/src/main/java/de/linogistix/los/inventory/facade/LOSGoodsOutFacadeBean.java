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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.wms2.mywms.address.Address;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.shipping.ShippingBusiness;
import de.wms2.mywms.shipping.ShippingOrder;
import de.wms2.mywms.shipping.ShippingOrderEntityService;
import de.wms2.mywms.shipping.ShippingOrderLine;
import de.wms2.mywms.shipping.ShippingOrderLineEntityService;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.user.UserBusiness;

@Stateless
public class LOSGoodsOutFacadeBean implements LOSGoodsOutFacade {

	private static final Logger log = Logger.getLogger(LOSGoodsOutFacadeBean.class);

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private ShippingBusiness shippingBusiness;
	@Inject
	private ShippingOrderEntityService shippingOrderEntityService;
	@Inject
	private ShippingOrderLineEntityService shippingOrderLineEntityService;
	@Inject
	private UserBusiness UserBusiness;
	@Inject
	private StorageLocationEntityService locationService;

	@Override
	public void confirm(Long goodsOutId) throws FacadeException {
		ShippingOrder order = manager.find(ShippingOrder.class, goodsOutId);
		if (order == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[] {});
		}
		for (ShippingOrderLine line : order.getLines()) {
			if (line.getState() < OrderState.FINISHED) {
				shippingBusiness.confirmLine(line, null);
			}
		}
		shippingBusiness.finishOrder(order);
	}

	@Override
	public void finish(Long orderId) throws FacadeException {
		ShippingOrder order = manager.find(ShippingOrder.class, orderId);
		if (order == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[] {});
		}
		shippingBusiness.finishOrder(order);
	}

	@Override
	public void finishOrder(Long goodsOutId) throws FacadeException {
		ShippingOrder order = manager.find(ShippingOrder.class, goodsOutId);
		if (order == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[] {});
		}
		shippingBusiness.finishOrder(order);
	}

	@Override
	public void finishPosition(String labelId, Long orderId, String destination) throws FacadeException {
		UnitLoad ul = null;

		try {
			ul = unitLoadService.readByLabel(labelId);
		} catch (Exception e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, labelId);
		}

		ShippingOrder req = manager.find(ShippingOrder.class, orderId);
		if (req == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[] {});
		}
		ShippingOrderLine orderLine = shippingOrderLineEntityService.readFirst(req, ul);
		if (orderLine == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, labelId);
		}

		StorageLocation location = null;
		if(!StringUtils.isBlank(destination)) {
			location = locationService.read(destination);
			if (location == null) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, labelId);
			}
		}

		shippingBusiness.confirmLine(orderLine, location);
	}

	@Override
	public List<LOSGoodsOutRequestTO> getRaw() {
		User user = UserBusiness.getCurrentUser();
		Client usersClient = user.getClient();

		String hql = " SELECT entity, entity.lines.size FROM " + ShippingOrder.class.getSimpleName() + " entity ";
		hql += " WHERE ( entity.state<=:processable";
		hql += " or (entity.state>:processable and entity.state<:finished and entity.operator=:user) )";
		if (!usersClient.isSystemClient()) {
			hql += " AND entity.client=:client ";
		}
		hql += " ORDER BY entity.state desc, entity.orderNumber ";

		Query query = manager.createQuery(hql);

		query.setParameter("processable", OrderState.PROCESSABLE);
		query.setParameter("finished", OrderState.FINISHED);
		query.setParameter("user", user);
		if (!usersClient.isSystemClient()) {
			query.setParameter("client", usersClient);
		}

		List<Object[]> results = query.getResultList();
		List<LOSGoodsOutRequestTO> dtos = new ArrayList<>(results.size());
		for (Object[] result : results) {
			ShippingOrder order = (ShippingOrder) result[0];
			Integer numPos = (Integer) result[1];

			LOSGoodsOutRequestTO orderDto = new LOSGoodsOutRequestTO();
			orderDto.setClientNumber(order.getClient().getNumber());
			orderDto.setNumber(order.getOrderNumber());
//			orderDto.setNumPos(numPos);
			orderDto.setShippingDate(order.getShippingDate());
			orderDto.setState(order.getState());

			DeliveryOrder deliveryOrder = order.getDeliveryOrder();
			if(deliveryOrder!=null) {
				if(!StringUtils.isBlank(deliveryOrder.getExternalNumber())) {
					orderDto.setCustomerOrderNumber(deliveryOrder.getExternalNumber());
				}
				else {
					orderDto.setCustomerOrderNumber(deliveryOrder.getOrderNumber());
				}
			}
			dtos.add(orderDto);
		}

		return dtos;
	}

	@Override
	public void start(Long orderId) throws FacadeException {
		ShippingOrder req = manager.find(ShippingOrder.class, orderId);

		req = shippingBusiness.startOperation(req, null);

		if (req.getLines().size() < 1) {
			log.warn("No positions found: " + req.toDescriptiveString());
		}

		if (req.getState() != OrderState.STARTED) {
			log.error("Unexpected state: " + req.toDescriptiveString());
		}
	}

	@Override
	public void cancel(Long orderId) throws FacadeException {
		ShippingOrder req = manager.find(ShippingOrder.class, orderId);
		req = shippingBusiness.cancelOperation(req);

		return;
	}

	@Override
	public void cancelLine(List<Long> lineIdList) throws FacadeException {
		for (Long id : lineIdList) {
			ShippingOrderLine line = manager.find(ShippingOrderLine.class, id);
			shippingBusiness.removeLine(line);
		}
	}

	@Override
	public LOSGoodsOutTO load(String number) throws FacadeException {
		ShippingOrder req = shippingOrderEntityService.readByOrderNumber(number);
		if (req != null) {
			return getOrderInfo(req);
		}
		return null;
	}

	@Override
	public LOSGoodsOutTO getOrderInfo(Long orderId) throws FacadeException {
		ShippingOrder req = manager.find(ShippingOrder.class, orderId);
		return getOrderInfo(req);
	}

	@SuppressWarnings("unchecked")
	private LOSGoodsOutTO getOrderInfo(ShippingOrder order) throws FacadeException {
		LOSGoodsOutTO to = new LOSGoodsOutTO(order);

		StringBuffer b = new StringBuffer();
		Query query;

		b.append(" SELECT pos FROM ");
		b.append(ShippingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.shippingOrder=:order ");
		b.append(" and pos.state<:state ");
		b.append(" ORDER BY pos.packet.unitLoad.storageLocation.name, pos.packet.unitLoad.labelId");

		query = manager.createQuery(b.toString());

		query.setParameter("order", order);
		query.setParameter("state", OrderState.FINISHED);

		try {
			List<ShippingOrderLine> posList = null;
			posList = query.getResultList();
			if (posList.size() > 0) {
				to.setNumPosOpen(posList.size());
				ShippingOrderLine next = posList.get(0);
				to.setNextLocationName(next.getPacket().getUnitLoad().getStorageLocation().getName());
				to.setNextUnitLoadLabelId(next.getPacket().getUnitLoad().getLabelId());
			} else {
				to.setFinished(true);
			}
		} catch (NoResultException e) {
		}

		b = new StringBuffer();
		b.append(" SELECT count(*) FROM ");
		b.append(ShippingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.shippingOrder=:order ");
		b.append(" and pos.state=:state ");

		query = manager.createQuery(b.toString());

		query.setParameter("order", order);
		query.setParameter("state", OrderState.FINISHED);

		try {
			Long numPosDone = (Long) query.getSingleResult();
			to.setNumPosDone(numPosDone);
		} catch (NoResultException e) {
		}

		return to;
	}

	@Override
	public void remove(Long goodsOutId) throws FacadeException {
		ShippingOrder out = manager.find(ShippingOrder.class, goodsOutId);
		if (out == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[] {});
		}
		shippingBusiness.removeOrder(out);
	}

	@Override
	public void createGoodsOutOrder(List<Long> pickingUnitLoadIdList) throws FacadeException {
		String logStr = "createGoodsOutOrder ";
		List<UnitLoad> ulList = new ArrayList<UnitLoad>();
		List<Packet> packetList = new ArrayList<>();
		DeliveryOrder deliveryOrder = null;
		Set<DeliveryOrder> orderSet = new HashSet<>();
		Set<Address> addressSet = new HashSet<>();
		Client orderClient = null;
		for (Long id : pickingUnitLoadIdList) {
			Client packetClient = null;
			Packet pul = manager.find(Packet.class, id);
			if (pul != null) {
				packetList.add(pul);
				packetClient = pul.getClient();
				if(pul.getDeliveryOrder()!=null) {
					orderSet.add(pul.getDeliveryOrder());
				}
				if(pul.getAddress()!=null) {
					addressSet.add(pul.getAddress());
				}
			} else {
				UnitLoad unitLoad = manager.find(UnitLoad.class, id);
				if (unitLoad != null) {
					for(StockUnit stockUnit:unitLoad.getStockUnitList()) {
						if(stockUnit.getReservedAmount()!=null && stockUnit.getReservedAmount().compareTo(BigDecimal.ZERO)>0) {
							log.warn(logStr + "There is reserved amount on a unitLoad. Cannot create shipping order. unitLoad="+unitLoad);
							throw new InventoryException(InventoryExceptionKey.STOCKUNIT_HAS_RESERVATION, "");
						}
					}
					ulList.add(unitLoad);
					packetClient = unitLoad.getClient();
				}
				else {
					log.warn(logStr + "Did not find unit load. id=" + id);
				}
			}
			if (packetClient != null && orderClient != null && !packetClient.equals(orderClient)) {
				log.warn(logStr + "Cannot mix clients. 1=" + orderClient + ", 2=" + packetClient);
				throw new InventoryException(InventoryExceptionKey.PICK_GEN_CLIENT_MIX, "");
			}
			if (packetClient != null) {
				orderClient = packetClient;
			}
		}
		if (orderSet.size() == 1) {
			for (DeliveryOrder s : orderSet) {
				deliveryOrder = s;
			}
		}
		if (orderClient == null) {
			log.warn(logStr + "No positions, no order");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, "");
		}

		ShippingOrder shippingOrder = shippingBusiness.createOrder(orderClient);
		shippingOrder.setDeliveryOrder(deliveryOrder);
		if(deliveryOrder!=null) {
			shippingOrder.setAddress(deliveryOrder.getAddress());
			shippingOrder.setCarrierName(deliveryOrder.getCarrierName());
			shippingOrder.setCarrierService(deliveryOrder.getCarrierService());
		}
		shippingOrder.setState(OrderState.PROCESSABLE);
		for(Packet packet : packetList) {
			shippingBusiness.addLine(shippingOrder, packet);
		}
		for(UnitLoad unitLoad : ulList) {
			shippingBusiness.addLine(shippingOrder, unitLoad);
		}
	}
}
