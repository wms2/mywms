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

import org.mywms.service.BasicService;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.Packet;

/**
 * @author krane
 *
 */
@Local
public interface LOSPickingPositionService extends BasicService<PickingOrderLine>{
	
	public List<PickingOrderLine> getByPickFromStockUnit(StockUnit pickFromStockUnit);
	public List<PickingOrderLine> getByPickFromUnitLoad(UnitLoad pickFromUnitLoad);
	public List<PickingOrderLine> getByPickFromUnitLoadLabel(String label);
	public List<PickingOrderLine> getByDeliveryOrderLine(DeliveryOrderLine deliveryOrderLine);
	public List<PickingOrderLine> getByDeliveryOrderNumber(String orderNumber);
	public List<PickingOrderLine> getByDeliveryOrder(DeliveryOrder order);
	public List<PickingOrderLine> getByPickToUnitLoad(Packet pickToUnitLoad);
}
