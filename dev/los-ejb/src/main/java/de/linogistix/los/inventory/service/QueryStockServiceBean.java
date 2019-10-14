/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

@Stateless
public class QueryStockServiceBean 
		implements QueryStockServiceRemote 
{
	
	@Inject
	private StockUnitEntityService stockUnitEntityService;
	@EJB
	private ContextService ctxService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	public List<StockUnit> getListByUnitLoad(UnitLoad ul) {
		return stockUnitEntityService.readByUnitLoad(ul);
	}

	public List<StockUnit> getListByStorageLocation(StorageLocation loc) {
		return stockUnitEntityService.readByLocation(loc);
	}

}
