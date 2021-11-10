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
package de.wms2.mywms.strategy;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for StorageStrategy
 * 
 * @author krane
 *
 */
public class StorageStrategyValidator implements EntityValidator<StorageStrategy> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;
	@Inject
	private StorageAreaEntityService storageAreaService;

	@Override
	public void validateCreate(StorageStrategy entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(StorageStrategy entityOld, StorageStrategy entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(StorageStrategy entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}

		if (entitySerivce.exists(StorageStrategy.class, "name", entity.getName(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}

		if (entity.isNearPickingLocation() && entity.isUseAreaStrategyDate()) {
			logger.log(Level.INFO,
					logStr + "Cannot combine feature 'near picking location' and 'use area strategy date'. name="
							+ entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidNearPickingLocationArea");
		}
	}

	@Override
	public void validateDelete(StorageStrategy entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(ItemData.class, "defaultStorageStrategy", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemData");
		}

		storageAreaService.removeForStorageStrategy(entity);
	}

}
