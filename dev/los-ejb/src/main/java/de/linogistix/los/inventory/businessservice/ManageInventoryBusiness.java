/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;

import de.linogistix.los.inventory.exception.InventoryException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.PackagingUnit;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.service.EntityNotFoundException;

/**
 *
 * @author trautm
 */
@Remote
public interface ManageInventoryBusiness {

    /**
     * 
     * 
     * @param clientRef
     * @param slName
     * @param articleRef
     * @param lotRef
     * @param amount
     * @param unitLoadRef
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws org.mywms.facade.FacadeException
     */
	StockUnit createStockUnitOnStorageLocation(String clientRef, String slName, String articleRef, String lotRef,
			BigDecimal amount, PackagingUnit packagingUnit, String unitLoadRef, String activityCode,
			String serialNumber) throws EntityNotFoundException, InventoryException, FacadeException;
    
    void deleteStockUnitsFromStorageLocation(StorageLocation sl, String activityCode) throws FacadeException;

//    StockUnit createStockUnit(String clientRef, String suName, String articleRef,
//			String lotRef, int amount, String unitLoadRef,
//			String generateManageInventoryNumber) throws FacadeException;


}
