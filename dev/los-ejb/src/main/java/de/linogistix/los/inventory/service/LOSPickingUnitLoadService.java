/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicService;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingUnitLoad;

/**
 * @author krane
 *
 */
@Local
public interface LOSPickingUnitLoadService extends BasicService<PickingUnitLoad>{
	
	public PickingUnitLoad create(PickingOrder pickingOrder, UnitLoad unitLoad, int index) throws FacadeException;
	public PickingUnitLoad getByLabel(String label);
	public PickingUnitLoad getByUnitLoad(UnitLoad unitLoad);
	public List<PickingUnitLoad> getByPickingOrder(PickingOrder pickingOrder);
	public List<PickingUnitLoad> getByDeliveryOrderNumber(String orderNumber);
	public List<PickingUnitLoad> getByDeliveryOrder(DeliveryOrder order);

}
