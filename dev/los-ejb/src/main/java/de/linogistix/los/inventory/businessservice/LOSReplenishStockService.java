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
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishStockService {

	public StockUnit findReplenishStock( ItemData itemData, Lot lot, BigDecimal amount, Collection<LOSStorageLocation> vetoLocations ) throws FacadeException;

}
