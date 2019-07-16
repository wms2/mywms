/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.customization.EntityGenerator;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadPackageType;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingUnitLoad;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingUnitLoadServiceBean extends BasicServiceBean<PickingUnitLoad> implements LOSPickingUnitLoadService{
	Logger log = Logger.getLogger(LOSPickingUnitLoadServiceBean.class);

	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSCustomerOrderService customerOrderService;
	
	public PickingUnitLoad create(PickingOrder pickingOrder, UnitLoad unitLoad, int index) throws FacadeException {
		
		PickingUnitLoad pickingUnitLoad = entityGenerator.generateEntity(PickingUnitLoad.class);

		pickingUnitLoad.setClient(pickingOrder.getClient());
		pickingUnitLoad.setPickingOrder(pickingOrder);
		pickingUnitLoad.setPositionIndex(index);
		pickingUnitLoad.setUnitLoad(unitLoad);
		
		// 02.06.17 krane. No limitation of material on the unitLoad
		unitLoad.setPackageType(UnitLoadPackageType.MIXED_CONSOLIDATE);
		
		manager.persist(pickingUnitLoad);
		
		return pickingUnitLoad;
	}

	public PickingUnitLoad getByLabel(String label) {
		Query query = manager.createQuery(
				"SELECT ul FROM " + PickingUnitLoad.class.getSimpleName() + " ul WHERE ul.unitLoad.labelId=:label");
		query.setParameter("label", label);
		
		try {
			return (PickingUnitLoad)query.getSingleResult();
		}
		catch (NoResultException ex) {}
		return null;
	}
	
	public PickingUnitLoad getByUnitLoad(UnitLoad unitLoad) {
		Query query = manager.createQuery(
				"SELECT ul FROM " + PickingUnitLoad.class.getSimpleName() + " ul WHERE ul.unitLoad=:unitLoad");
		query.setParameter("unitLoad", unitLoad);
		
		try {
			return (PickingUnitLoad)query.getSingleResult();
		}
		catch (NoResultException ex) {}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<PickingUnitLoad> getByPickingOrder(PickingOrder pickingOrder) {
		Query query = manager.createQuery(
				"SELECT ul FROM " + PickingUnitLoad.class.getSimpleName() + " ul WHERE ul.pickingOrder=:pickingOrder");
		query.setParameter("pickingOrder", pickingOrder);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<PickingUnitLoad> getByDeliveryOrderNumber(String orderNumber) {
		Query query = manager.createQuery("SELECT ul FROM " + PickingUnitLoad.class.getSimpleName()
				+ " ul WHERE ul.deliveryOrderNumber=:deliveryOrderNumber");
		query.setParameter("deliveryOrderNumber", orderNumber);

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<PickingUnitLoad> getByDeliveryOrder(DeliveryOrder order) {
		Query query = manager.createQuery("SELECT ul FROM " + PickingUnitLoad.class.getSimpleName()
				+ " ul WHERE ul.deliveryOrderNumber=:deliveryOrderNumber");
		query.setParameter("deliveryOrderNumber", order.getOrderNumber());

		return query.getResultList();
	}
	public DeliveryOrder getDeliveryOrder(PickingUnitLoad pickingUnitLoad) {
		if( pickingUnitLoad == null || pickingUnitLoad.getDeliveryOrderNumber() == null || pickingUnitLoad.getDeliveryOrderNumber().equals("-") ) {
			return null;
		}
		
		return customerOrderService.getByNumber(pickingUnitLoad.getDeliveryOrderNumber());
	}

}
