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

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.wms2.mywms.inventory.UnitLoad;

/**
 * @author krane
 *
 */
@Local
public interface LOSPickingUnitLoadService extends BasicService<LOSPickingUnitLoad>{
	
	public LOSPickingUnitLoad create(LOSPickingOrder pickingOrder, UnitLoad unitLoad, int index) throws FacadeException;
	public LOSPickingUnitLoad getByLabel(String label);
	public LOSPickingUnitLoad getByUnitLoad(UnitLoad unitLoad);
	public List<LOSPickingUnitLoad> getByPickingOrder(LOSPickingOrder pickingOrder);
	public List<LOSPickingUnitLoad> getByCustomerOrderNumber(String customerOrderNumber);
	public List<LOSPickingUnitLoad> getByCustomerOrder(LOSCustomerOrder customerOrder);
	public LOSCustomerOrder getCustomerOrder(LOSPickingUnitLoad pickingUnitLoad);

}
