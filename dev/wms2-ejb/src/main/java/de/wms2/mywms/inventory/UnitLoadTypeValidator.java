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
package de.wms2.mywms.inventory;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for UnitLoadType
 * 
 * @author krane
 *
 */
public class UnitLoadTypeValidator implements EntityValidator<UnitLoadType> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entityService;

	@Override
	public void validateCreate(UnitLoadType entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(UnitLoadType entityOld, UnitLoadType entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(UnitLoadType entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}

		if (entityService.exists(UnitLoadType.class, "name", entity.getName(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}
	}

	@Override
	public void validateDelete(UnitLoadType entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entityService.exists(UnitLoad.class, "unitLoadType", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to UnitLoad. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByUnitLoad");
		}
		if (entityService.exists(ItemData.class, "defaultUnitLoadType", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to Product. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemData");
		}
		if (entityService.exists(TypeCapacityConstraint.class, "unitLoadType", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to AllocationRule. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByTypeCapacity");
		}
	}
}
