/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;

@Stateless
public class LOSGoodsReceiptComponentBean implements LOSGoodsReceiptComponent {

	private final Logger logger = Logger
			.getLogger(LOSGoodsReceiptComponentBean.class);
	@Inject
	private ClientBusiness clientService;
	@EJB
	private InventoryGeneratorService genService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private InventoryBusiness inventoryBusiness;


	public UnitLoad getOrCreateUnitLoad(Client c, StorageLocation sl,
			UnitLoadType type, String ref) throws FacadeException {

		UnitLoad ul;
		Client cl;
		if (c != null) {
			cl=c;
		} else {
			cl = clientService.getSystemClient();

		}

		if (sl == null) {
			throw new NullPointerException("StorageLocation must not be null");
		}
		if (type == null) {
			throw new NullPointerException("UnitLoadType must not be null");
		}
		if (ref == null) {
			ref = genService.generateUnitLoadLabelId(cl, type);
		}

		if (ref != null && ref.length() != 0) {
			ul = unitLoadService.readByLabel(ref);
			if (ul != null) {
				if( !sl.equals(ul.getStorageLocation()) ) {
					logger.warn("UnitLoad not on location. label="+ul.getLabelId()+", location="+ul.getStorageLocation().getName()+", check location="+sl.getName());
					throw new LOSLocationException(
							LOSLocationExceptionKey.UNITLOAD_NOT_ON_LOCATION,
							new String[] { ul.getLabelId(), sl.getName() });
				}
			} else {
				ul = inventoryBusiness.createUnitLoad(cl, ref, type, sl, StockState.INCOMING, null, null, null);
			}
		} else {
			throw new IllegalArgumentException("Missing labelId");
		}

		return ul;
	}

}
