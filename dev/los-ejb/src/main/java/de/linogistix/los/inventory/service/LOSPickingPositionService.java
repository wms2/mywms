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

import org.mywms.model.StockUnit;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
@Local
public interface LOSPickingPositionService extends BasicService<LOSPickingPosition>{
	
	public List<LOSPickingPosition> getByPickFromStockUnit(StockUnit pickFromStockUnit);
	public List<LOSPickingPosition> getByPickFromUnitLoad(LOSUnitLoad pickFromUnitLoad);
	public List<LOSPickingPosition> getByPickFromUnitLoadLabel(String label);
	public List<LOSPickingPosition> getByCustomerOrderPosition(LOSCustomerOrderPosition customerOrderPos);
	public List<LOSPickingPosition> getByCustomerOrderNumber(String orderNumber);
	public List<LOSPickingPosition> getByCustomerOrder(LOSCustomerOrder order);
	public List<LOSPickingPosition> getByPickToUnitLoad(LOSPickingUnitLoad pickToUnitLoad);
}
