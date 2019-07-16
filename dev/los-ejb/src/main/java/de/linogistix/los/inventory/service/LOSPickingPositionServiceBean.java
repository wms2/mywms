/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingUnitLoad;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingPositionServiceBean extends BasicServiceBean<PickingOrderLine> implements LOSPickingPositionService{
	Logger log = Logger.getLogger(LOSPickingPositionServiceBean.class);


	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByPickFromStockUnit(StockUnit pickFromStockUnit) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(PickingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromStockUnit=:su");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("su", pickFromStockUnit);
		
		return query.getResultList();
		
	}

	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByPickFromUnitLoad(UnitLoad pickFromUnitLoad) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(PickingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromStockUnit.unitLoad=:unitLoad");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("unitLoad", pickFromUnitLoad);
		

		List<PickingOrderLine> ret = query.getResultList();
		if( ret.size()>0 ) {
			return ret;
		}
		
		b = new StringBuffer();
		b.append(" SELECT pos FROM ");
		b.append(PickingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromUnitLoadLabel=:label");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("label", pickFromUnitLoad.getLabelId());
		
		return query.getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByPickFromUnitLoadLabel(String label) {

		StringBuffer b = new StringBuffer();
		Query query;

		b = new StringBuffer();
		b.append(" SELECT pos FROM ");
		b.append(PickingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromUnitLoadLabel=:label");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("label", label);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByPickToUnitLoad(PickingUnitLoad pickToUnitLoad) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(PickingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickToUnitLoad=:pickToUnitLoad");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("pickToUnitLoad", pickToUnitLoad);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByDeliveryOrderLine(DeliveryOrderLine deliveryOrderLine) {
		Query query = manager.createQuery("SELECT pos FROM " + PickingOrderLine.class.getSimpleName()
				+ " pos WHERE pos.deliveryOrderLine=:deliveryOrderLine");
		query.setParameter("deliveryOrderLine", deliveryOrderLine);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByDeliveryOrderNumber(String orderNumber) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(PickingOrderLine.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.deliveryOrderLine.deliveryOrder.orderNumber=:orderNumber");
		b.append(" ORDER BY pos.id ");
		
		query = manager.createQuery(new String(b));
		query.setParameter("orderNumber", orderNumber);
		
		return query.getResultList();
		
	}

	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> getByDeliveryOrder(DeliveryOrder order) {
		Query query = manager.createQuery("SELECT pos FROM " + PickingOrderLine.class.getSimpleName()
				+ " pos WHERE pos.deliveryOrderLine.deliveryOrder=:deliveryOrder");
		query.setParameter("deliveryOrder", order);
		return query.getResultList();
	}

}
