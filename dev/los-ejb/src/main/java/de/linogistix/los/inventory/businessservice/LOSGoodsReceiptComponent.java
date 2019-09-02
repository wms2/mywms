/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.location.exception.LOSLocationException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;

@Local
public interface LOSGoodsReceiptComponent {
    /**
     * Gets or creates a UnitLoad for the goods receipt process.
     * 
     * @param c the client for whom the process is done
     * @param sl the storage location where the process is done
     * @param type the type of the unit load
     * @param ref the labelID of the unit load
     * @return 
     * @throws LOSLocationException
     */
    public UnitLoad getOrCreateUnitLoad(Client c,
            StorageLocation sl, UnitLoadType type, String ref) throws FacadeException;
}
