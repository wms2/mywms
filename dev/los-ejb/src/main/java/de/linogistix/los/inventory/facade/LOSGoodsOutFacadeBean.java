/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.businessservice.LOSGoodsOutBusiness;
import de.linogistix.los.inventory.businessservice.LOSGoodsOutGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingUnitLoad;
@Stateless
public class LOSGoodsOutFacadeBean implements LOSGoodsOutFacade {

	private static final Logger log = Logger.getLogger(LOSGoodsOutFacadeBean.class);

	@EJB
	private QueryUnitLoadService ulQuery;
	@EJB
	private LOSGoodsOutBusiness outBusiness;
	@EJB
	private LOSGoodsOutGenerator outGenerator;
	@EJB
	private LOSGoodsOutRequestQueryRemote outQuery;
	@EJB
	private InventoryGeneratorService genService;
	@EJB
	private LOSCustomerOrderService orderService;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@Override
	public LOSGoodsOutRequest confirm(Long goodsOutId) throws FacadeException {
		LOSGoodsOutRequest out = manager.find(LOSGoodsOutRequest.class, goodsOutId);
		if( out == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[]{});
		}
		out = outBusiness.finish(out,true);
		return out;
	}

	@Override
	public LOSGoodsOutRequest finish(Long orderId) throws FacadeException{	 	
		LOSGoodsOutRequest out  = manager.find(LOSGoodsOutRequest.class, orderId);
		out = outBusiness.finish(out);
		return (LOSGoodsOutRequest)BusinessObjectHelper.eagerRead(out);
	}

	@Override
	public LOSGoodsOutRequest finishOrder(Long goodsOutId) throws FacadeException {
		LOSGoodsOutRequest out = manager.find(LOSGoodsOutRequest.class, goodsOutId);
		if( out == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[]{});
		}
		out = outBusiness.finishOrder(out);
		return out;
	}

	@Override
	public LOSGoodsOutRequestPosition finishPosition(String labelId, Long orderId) throws FacadeException {
		LOSGoodsOutRequestPosition ret;
		UnitLoad ul = null;
		
		try {
			ul = ulQuery.getByLabelId(labelId);
		} catch (Exception e) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, labelId);
		}
		
		LOSGoodsOutRequest req = manager.find(LOSGoodsOutRequest.class, orderId);
		ret = outBusiness.finishPosition(req, ul);
		
		return ret;
	}


	@Override
	public List<LOSGoodsOutRequestTO> getRaw() {
		List<LOSGoodsOutRequestTO> out;
		out = outBusiness.getRaw();
		return out; 
	}

	@Override
	public LOSGoodsOutRequest start(Long orderId) throws FacadeException {
		LOSGoodsOutRequest req = manager.find(LOSGoodsOutRequest.class, orderId);
		
		req = outBusiness.accept(req);
		
		if (req.getPositions().size() < 1) { 
			log.warn("No positions found: "  +req.toDescriptiveString() );
		}
		
		if (!req.getOutState().equals(LOSGoodsOutRequestState.PROCESSING)){
			log.error("Unexpected state: " + req.toDescriptiveString());
		}
		
		return req;
	}


	@Override
	public void cancel(Long orderId) throws FacadeException{
		
		LOSGoodsOutRequest req = manager.find(LOSGoodsOutRequest.class, orderId);
		req = outBusiness.cancel(req);
		
		return;
	}
	
//	@Override
//	public LOSGoodsOutRequestPosition getNextPosition(LOSGoodsOutRequest currentOrder) throws FacadeException {
//		StringBuffer b = new StringBuffer();
//		Query query;
//
//		b.append(" SELECT pos FROM ");
//		b.append(LOSGoodsOutRequestPosition.class.getName());
//		b.append(" pos ");
//		b.append(" WHERE pos.goodsOutRequest=:order ");
//		b.append(" and pos.outState=:state ");
//		b.append(" ORDER BY pos.source.storageLocation.name, pos.source.labelId");
//
//		query = manager.createQuery(b.toString());
//		
//		query.setParameter("order", currentOrder);
//		query.setParameter("state", LOSGoodsOutRequestPositionState.RAW);
//		query.setMaxResults(1);
//		
//		LOSGoodsOutRequestPosition pos = null;
//		try {
//			pos = (LOSGoodsOutRequestPosition)query.getSingleResult();
//		}
//		catch( NoResultException e ) {
//			log.info("NOTHING FOUND for order " + currentOrder.getNumber());
//		}
//		
//		if( pos != null ) {
//			pos.getSource().getStorageLocation().getName();
//		}
//		return pos;
//	}

	
	
	@Override
	public LOSGoodsOutTO load(String number) throws FacadeException {
		LOSGoodsOutRequest req = null; 
		try {
			req = outQuery.queryByIdentity(number);
		}
		catch( BusinessObjectNotFoundException e) {}
		if( req != null ) {
			return getOrderInfo(req);
		}
		return null;
	}

	@Override
	public LOSGoodsOutTO getOrderInfo(Long orderId) throws FacadeException {
		LOSGoodsOutRequest req = manager.find(LOSGoodsOutRequest.class, orderId);
		return getOrderInfo(req);
	}
	
	@SuppressWarnings("unchecked")
	private LOSGoodsOutTO getOrderInfo(LOSGoodsOutRequest order) throws FacadeException {

		LOSGoodsOutTO to = new LOSGoodsOutTO( order );
		
		StringBuffer b = new StringBuffer();
		Query query;

		b.append(" SELECT pos FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.goodsOutRequest=:order ");
		b.append(" and pos.outState=:state ");
		b.append(" ORDER BY pos.source.storageLocation.name, pos.source.labelId");

		query = manager.createQuery(b.toString());
		
		query.setParameter("order", order);
		query.setParameter("state", LOSGoodsOutRequestPositionState.RAW);

		try {
			List<LOSGoodsOutRequestPosition> posList = null;
			posList = query.getResultList();
			if( posList.size()>0 ) {
				to.setNumPosOpen( posList.size() );
				LOSGoodsOutRequestPosition next = posList.get(0);
				to.setNextLocationName( next.getSource().getStorageLocation().getName() );
				to.setNextUnitLoadLabelId( next.getSource().getLabelId() );
			}
			else {
				to.setFinished(true);
			}
		}
		catch( NoResultException e ) {}

		
		b = new StringBuffer();
		b.append(" SELECT count(*) FROM ");
		b.append(LOSGoodsOutRequestPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.goodsOutRequest=:order ");
		b.append(" and pos.outState=:state ");

		query = manager.createQuery(b.toString());
		
		query.setParameter("order", order);
		query.setParameter("state", LOSGoodsOutRequestPositionState.FINISHED);
		
		try {
			Long numPosDone = (Long)query.getSingleResult();
			to.setNumPosDone(numPosDone);
		}
		catch( NoResultException e ) {}

		return to;
	}

	@Override
	public LOSGoodsOutRequest update(Long orderId, String comment) throws FacadeException {
		LOSGoodsOutRequest req = manager.find(LOSGoodsOutRequest.class, orderId);
		req.setAdditionalContent(comment);
		return req;
	}

	@Override
	public void remove(Long goodsOutId) throws FacadeException {
		LOSGoodsOutRequest out = manager.find(LOSGoodsOutRequest.class, goodsOutId);
		if( out == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_GOODS_OUT, new Object[]{});
		}
		outBusiness.remove(out);
	}

	@Override
	public void createGoodsOutOrder(List<Long> pickingUnitLoadIdList) throws FacadeException {
		String logStr = "createGoodsOutOrder ";
		List<UnitLoad> ulList = new ArrayList<UnitLoad>();
		DeliveryOrder deliveryOrder = null;
		Set<String> orderSet = new HashSet<String>();
		Client client = null;
		for( Long id : pickingUnitLoadIdList ) {
			PickingUnitLoad pul = manager.find(PickingUnitLoad.class, id);
			if( pul != null ) {
				ulList.add(pul.getUnitLoad());
				client = pul.getClient();
				if( pul.getDeliveryOrderNumber() != null ) {
					orderSet.add(pul.getDeliveryOrderNumber());
				}
			}
			else {
				log.warn(logStr+"Did not find unit load. id="+id);
			}
		}
		if( orderSet.size()==1 ) {
			for( String s : orderSet ) {
				deliveryOrder = orderService.getByNumber(s);
			}				
		}
		if( client == null ) {
			log.warn(logStr+"No positions, no order");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, "");
		}

		String shipmentNumber = genService.generateGoodsOutNumber(client);

		LOSGoodsOutRequest out = outGenerator.createOrder(client, null, shipmentNumber, new Date(), null, null);
		out.setCustomerOrder(deliveryOrder);
		
		for( UnitLoad unitLoad : ulList ) {
			outGenerator.addPosition(out, unitLoad);
		}
	}
}
