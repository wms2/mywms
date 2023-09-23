/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.crud;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.facade.FacadeException;
import org.mywms.model.User;
import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.StockUnitService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.PackagingUnit;

/**
 * @author trautm
 *
 */
@Stateless
public class StockUnitCRUDBean extends BusinessObjectCRUDBean<StockUnit> implements StockUnitCRUDRemote {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@EJB
	StockUnitService service;
	@EJB
	private ContextService context;

	@Inject
	private InventoryBusiness inventoryBusiness;

	@Override
	protected BasicService<StockUnit> getBasicService() {

		return service;
	}

	@Override
	public void lock(StockUnit entity, int lock, String lockCause) throws BusinessObjectSecurityException {
		User user = context.getCallersUser();
		if (!context.checkClient(entity)) {
			throw new BusinessObjectSecurityException(user);
		}

		try {
			entity = manager.find(entity.getClass(), entity.getId());
			inventoryBusiness.addStockUnitLock(entity, lock);
			entity.addAdditionalContent( "L  "+user.getName() + " : " + lockCause);
		} catch (FacadeException e) {
			// Sorry for special exceptions. The real cause will be hidden
			logger.log(Level.SEVERE, "EXCEPTION="+e.getClass().getSimpleName()+", "+e.getMessage(), e);
			throw new BusinessObjectSecurityException(null);
		}
	}

	@Override
	public void update(StockUnit entity) throws BusinessObjectNotFoundException, BusinessObjectModifiedException,
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
	public StockUnit create(StockUnit entity)
			throws BusinessObjectExistsException, BusinessObjectCreationException, BusinessObjectSecurityException {
		if (entity.getItemData() != null && entity.getPackagingUnit() != null
				&& entity.getPackagingUnit().getItemData() != null) {
			if (!checkPackaging(entity, entity.getPackagingUnit())) {
				throw new BusinessObjectCreationException();
			}
		}
		return super.create(entity);
	}

	private boolean checkPackaging(StockUnit stockUnit, PackagingUnit packaging) {
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
