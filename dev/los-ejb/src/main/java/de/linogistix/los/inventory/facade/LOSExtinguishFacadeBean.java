/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.ExtinguishOrderGenerator;
import de.wms2.mywms.picking.PickingOrder;

/**
 * @author krane
 *
 */
@Stateless
public class LOSExtinguishFacadeBean implements LOSExtinguishFacade {
	private static Logger log = Logger.getLogger(LOSExtinguishFacade.class);

	@EJB
	private ContextService contextService;
	@EJB
	private LOSOrderBusiness orderBusiness;
	@Inject
	private ExtinguishOrderGenerator extinguishOrderGenerator;
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;

	
	@Override
	public void generateStockUnitOrder( List<Long> stockUnitIds ) throws FacadeException {
		String logStr = "generateStockUnitOrder ";
		log.info(logStr);

		if( stockUnitIds==null || stockUnitIds.size()==0 ) {
			log.warn(logStr+"No stock unit, no extinguish order");
			return;
		}

		List<StockUnit> stockUnits = new ArrayList<>();
		for( long id : stockUnitIds ) {
			StockUnit stockUnit = manager.find(StockUnit.class, id);
			if( stockUnit!=null) {
				stockUnits.add(stockUnit);
			}
		}

		List<PickingOrder> extinguishOrders = extinguishOrderGenerator.generateStockUnitExtinguishOrders(stockUnits);
		for( PickingOrder pickingOrder : extinguishOrders ) {
			orderBusiness.releasePickingOrder(pickingOrder);
		}
	}

	@Override
	public void generateUnitLoadOrder( List<Long> unitLoadIds ) throws FacadeException {
		String logStr = "generateUnitLoadOrder ";
		log.info(logStr);

		if( unitLoadIds==null || unitLoadIds.size()==0 ) {
			log.warn(logStr+"No unit load, no extinguish order");
			return;
		}

		List<UnitLoad> unitLoads = new ArrayList<>();
		for( long id : unitLoadIds ) {
			UnitLoad unitLoad = manager.find(UnitLoad.class, id);
			if( unitLoad!=null) {
				unitLoads.add(unitLoad);
			}
		}

		List<PickingOrder> extinguishOrders = extinguishOrderGenerator.generateUnitLoadExtinguishOrders(unitLoads);
		for( PickingOrder pickingOrder : extinguishOrders ) {
			orderBusiness.releasePickingOrder(pickingOrder);
		}
	}

}
