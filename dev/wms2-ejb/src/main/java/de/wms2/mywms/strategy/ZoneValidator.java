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
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Constants;

/**
 * Validation service for Zone
 * 
 * @author krane
 *
 */
public class ZoneValidator implements EntityValidator<Zone> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(Zone entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(Zone oldEntity, Zone newEntity) throws BusinessException {
		validate(newEntity);
	}

	public void validate(Zone entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}

		if (StringUtils.equals(entity.getName(), Wms2Constants.UNDEFINED_ZONE_NAME)) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidName");
		}

		if (entitySerivce.exists(Zone.class, "name", entity.getName(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}
	}

	@Override
	public void validateDelete(Zone entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(StorageLocation.class, "zone", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to StorageLocation. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByStorageLocation");
		}
		if (entitySerivce.exists(ItemData.class, "zone", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemData");
		}
	}
}
