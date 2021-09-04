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
 * Validation service for StorageStrategyLayer
 * 
 * @author krane
 *
 */
public class StorageStrategyLayerValidator implements EntityValidator<StorageStrategyLayer> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(StorageStrategyLayer entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(StorageStrategyLayer entityOld, StorageStrategyLayer entityNew)
			throws BusinessException {
		validate(entityNew);
	}

	public void validate(StorageStrategyLayer entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getStorageLayer() < 0) {
			logger.log(Level.INFO,
					logStr + "invalid storageLayer. entity=" + entity + ", storageLayer=" + entity.getStorageLayer());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidLayer");
		}

		String[] attributes = { "storageStrategy", "storageLayer" };
		Object[] values = { entity.getStorageStrategy(), entity.getStorageLayer() };
		if (entitySerivce.exists(StorageStrategyLayer.class, attributes, values, entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. storageStrategy=" + entity.getStorageStrategy()
					+ ", storageLayer=" + entity.getStorageLayer());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}

	}

	@Override
	public void validateDelete(StorageStrategyLayer entity) throws BusinessException {
	}

}
