/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.wms2.mywms.inventory.UnitLoad;

@Local
public interface LOSGoodsOutRequestPositionService extends
		BasicService<LOSGoodsOutRequestPosition> {

//	public LOSGoodsOutRequestPosition getByUnitLoad(UnitLoad ul) throws EntityNotFoundException;
	public List<LOSGoodsOutRequestPosition> getByUnitLoad(UnitLoad ul);
	public LOSGoodsOutRequestPosition getByUnitLoad(LOSGoodsOutRequest out, UnitLoad ul);

}
