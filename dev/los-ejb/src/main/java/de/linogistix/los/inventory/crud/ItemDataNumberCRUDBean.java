/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.crud;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;
import de.wms2.mywms.product.PackagingUnit;

/**
 * @author krane
 *
 */
@Stateless
public class ItemDataNumberCRUDBean extends BusinessObjectCRUDBean<ItemDataNumber> implements ItemDataNumberCRUDRemote {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@EJB
	ItemDataNumberService service;

	@Override
	protected BasicService<ItemDataNumber> getBasicService() {
		return service;
	}

	@Override
	public void update(ItemDataNumber entity) throws BusinessObjectNotFoundException, BusinessObjectModifiedException,
			BusinessObjectMergeException, BusinessObjectSecurityException, FacadeException {
		if (entity.getItemData() != null && entity.getPackagingUnit() != null
				&& entity.getPackagingUnit().getItemData() != null) {
			if (!checkPackaging(entity, entity.getPackagingUnit())) {
				throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[] {
						entity.getItemData().getNumber(), entity.getPackagingUnit().getItemData().getNumber() });
			}
		}
		super.update(entity);
	}

	@Override
	public ItemDataNumber create(ItemDataNumber entity)
			throws BusinessObjectExistsException, BusinessObjectCreationException, BusinessObjectSecurityException {
		if (entity.getItemData() != null && entity.getPackagingUnit() != null
				&& entity.getPackagingUnit().getItemData() != null) {
			if (!checkPackaging(entity, entity.getPackagingUnit())) {
				throw new BusinessObjectCreationException();
			}
		}
		return super.create(entity);
	}

	private boolean checkPackaging(ItemDataNumber stockUnit, PackagingUnit packaging) {
		if (stockUnit == null || packaging == null) {
			return true;
		}
		ItemData stockUnitItemData = stockUnit.getItemData();
		ItemData packagingItemData = packaging.getItemData();

		if (!Objects.equals(stockUnitItemData, packagingItemData)) {
			logger.log(Level.INFO, "ItemData missmatch. stockUnit.itemData=" + stockUnitItemData
					+ ", packaging.itemData=" + packagingItemData);
			return false;
		}

		return true;
	}
}
