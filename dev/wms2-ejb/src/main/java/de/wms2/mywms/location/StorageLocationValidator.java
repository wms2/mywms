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
package de.wms2.mywms.location;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for StorageLocation
 * 
 * @author krane
 *
 */
public class StorageLocationValidator implements EntityValidator<StorageLocation> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(StorageLocation entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(StorageLocation entityOld, StorageLocation entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(StorageLocation entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}
		if (entity.getClient() == null) {
			logger.log(Level.INFO, logStr + "missing client. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingClient");
		}
		if (entity.getLocationType() == null) {
			logger.log(Level.INFO, logStr + "missing locationType. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocationType");
		}
		if (entity.getArea() == null) {
			logger.log(Level.INFO, logStr + "missing area. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingArea");
		}
		if (entity.getLocationCluster() == null) {
			logger.log(Level.INFO, logStr + "missing locationCluster. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocationCluster");
		}

		if (entitySerivce.exists(StorageLocation.class, "name", entity.getName(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}
	}

	@Override
	public void validateDelete(StorageLocation entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(UnitLoad.class, "storageLocation", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to UnitLoad. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByUnitLoad");
		}
	}

}
