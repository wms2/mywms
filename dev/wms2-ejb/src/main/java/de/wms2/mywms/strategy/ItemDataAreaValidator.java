/* 
Copyright 2021 Matthias Krane

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
package de.wms2.mywms.strategy;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for ItemDataStorageArea
 * 
 * @author krane
 *
 */
public class ItemDataAreaValidator implements EntityValidator<ItemDataArea> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(ItemDataArea entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(ItemDataArea entityOld, ItemDataArea entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(ItemDataArea entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getItemData() == null) {
			logger.log(Level.INFO, logStr + "missing itemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}

		if (entity.getStorageArea() == null) {
			logger.log(Level.INFO, logStr + "missing storage area. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingStorageArea");
		}

		String[] attributes = { "itemData", "storageArea" };
		Object[] values = { entity.getItemData(), entity.getStorageArea() };
		if (entitySerivce.exists(ItemDataArea.class, attributes, values, entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. itemData/storageArea. entity=" + entity + ", itemData="
					+ entity.getItemData() + ", storageArea=" + entity.getStorageArea());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueArea");
		}
	}

	@Override
	public void validateDelete(ItemDataArea entity) throws BusinessException {
	}

}
