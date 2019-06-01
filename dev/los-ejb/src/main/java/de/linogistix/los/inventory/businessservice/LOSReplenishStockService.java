/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.Collection;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishStockService {

	public StockUnit findReplenishStock( ItemData itemData, Lot lot, BigDecimal amount, Collection<StorageLocation> vetoLocations ) throws FacadeException;

}
