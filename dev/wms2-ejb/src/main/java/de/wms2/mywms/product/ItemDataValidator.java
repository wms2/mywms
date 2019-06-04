/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.product;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for ItemData
 * 
 * @author krane
 * 
 */
public class ItemDataValidator implements EntityValidator<ItemData> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;
	@Inject
	private ItemDataEntityService productSerivce;

	@Override
	public void validateCreate(ItemData entity) throws BusinessException {
		validate(entity);

		if (productSerivce.existsIgnoreCase(entity.getNumber(), null)) {
			logger.log(Level.INFO, "Not unique. number=" + entity.getNumber());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}

	}

	@Override
	public void validateUpdate(ItemData entityOld, ItemData entityNew) throws BusinessException {
		validate(entityNew);

		if (entityOld != null && !StringUtils.equals(entityOld.getName(), entityNew.getName())) {
			if (productSerivce.existsIgnoreCase(entityNew.getNumber(), entityNew.getId())) {
				logger.log(Level.INFO, "Not unique. number=" + entityNew.getNumber());
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
			}
		}
	}

	public void validate(ItemData entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getClient() == null) {
			logger.log(Level.INFO, logStr + "missing client. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingClient");
		}

		if (StringUtils.isEmpty(entity.getNumber())) {
			logger.log(Level.INFO, logStr + "missing productNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}

		if (entity.getItemUnit() == null) {
			logger.log(Level.INFO, logStr + "missing productUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingUnit");
		}

		String[] attributes = { "client", "number" };
		Object[] values = { entity.getClient(), entity.getNumber() };
		if (entitySerivce.exists(ItemData.class, attributes, values, entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. entity=" + entity + ", client=" + entity.getClient()
					+ ", number=" + entity.getNumber());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}

	}

	@Override
	public void validateDelete(ItemData entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(ItemDataNumber.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemDataCode. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemDataNumber");
		}
		if (entitySerivce.exists(StockUnit.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to StockUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByStockUnit");
		}
		if (entitySerivce.exists(PackagingUnit.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to PackagingUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByPackingUnit");
		}
	}

}
