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

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingPositionServiceBean extends BasicServiceBean<LOSPickingPosition> implements LOSPickingPositionService{
	Logger log = Logger.getLogger(LOSPickingPositionServiceBean.class);


	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByPickFromStockUnit(StockUnit pickFromStockUnit) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(LOSPickingPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromStockUnit=:su");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("su", pickFromStockUnit);
		
		return query.getResultList();
		
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByPickFromUnitLoad(UnitLoad pickFromUnitLoad) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(LOSPickingPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromStockUnit.unitLoad=:unitLoad");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("unitLoad", pickFromUnitLoad);
		

		List<LOSPickingPosition> ret = query.getResultList();
		if( ret.size()>0 ) {
			return ret;
		}
		
		b = new StringBuffer();
		b.append(" SELECT pos FROM ");
		b.append(LOSPickingPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromUnitLoadLabel=:label");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("label", pickFromUnitLoad.getLabelId());
		
		return query.getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByPickFromUnitLoadLabel(String label) {

		StringBuffer b = new StringBuffer();
		Query query;

		b = new StringBuffer();
		b.append(" SELECT pos FROM ");
		b.append(LOSPickingPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickFromUnitLoadLabel=:label");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("label", label);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByPickToUnitLoad(LOSPickingUnitLoad pickToUnitLoad) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(LOSPickingPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.pickToUnitLoad=:pickToUnitLoad");
		b.append(" ORDER BY pos.id ");

		query = manager.createQuery(new String(b));
		query.setParameter("pickToUnitLoad", pickToUnitLoad);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByCustomerOrderPosition(LOSCustomerOrderPosition customerOrderPos) {
		Query query = manager.createNamedQuery("LOSPickingPosition.queryByCustomerOrderPos");
		query.setParameter("customerOrderPos", customerOrderPos);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByCustomerOrderNumber(String orderNumber) {

		StringBuffer b = new StringBuffer();
		Query query;
		 
		b.append(" SELECT pos FROM ");
		b.append(LOSPickingPosition.class.getName());
		b.append(" pos ");
		b.append(" WHERE pos.customerOrderPosition.order.number=:orderNumber");
		b.append(" ORDER BY pos.customerOrderPosition.index, pos.id ");
		
		query = manager.createQuery(new String(b));
		query.setParameter("orderNumber", orderNumber);
		
		return query.getResultList();
		
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickingPosition> getByCustomerOrder(LOSCustomerOrder order) {
		Query query = manager.createNamedQuery("LOSPickingPosition.queryByCustomerOrder");
		query.setParameter("customerOrder", order);
		return query.getResultList();
	}

}
