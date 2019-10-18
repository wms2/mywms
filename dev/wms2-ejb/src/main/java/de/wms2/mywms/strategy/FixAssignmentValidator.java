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

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for FixAssignment
 * 
 * @author krane
 *
 */
public class FixAssignmentValidator implements EntityValidator<FixAssignment> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;
	@Inject
	private PersistenceManager manager;

	@Override
	public void validateCreate(FixAssignment entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(FixAssignment entityOld, FixAssignment entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(FixAssignment entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getStorageLocation() == null) {
			logger.log(Level.INFO, logStr + "missing storageLocation. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocation");
		}
		if (entity.getItemData() == null) {
			logger.log(Level.INFO, logStr + "missing itemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}
		if (entity.getMaxAmount() != null && entity.getMaxAmount().compareTo(BigDecimal.ZERO) < 0) {
			logger.log(Level.INFO,
					logStr + "invalid maxAmount. entity=" + entity + ", maxAmount=" + entity.getMaxAmount());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notNegativeAmount");
		}
		if (entity.getMinAmount() != null && entity.getMinAmount().compareTo(BigDecimal.ZERO) < 0) {
			logger.log(Level.INFO,
					logStr + "invalid minAmount. entity=" + entity + ", minAmount=" + entity.getMinAmount());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notNegativeAmount");
		}
		if (entity.getMaxPickAmount() != null && entity.getMaxPickAmount().compareTo(BigDecimal.ZERO) < 0) {
			logger.log(Level.INFO, logStr + "invalid maxPickAmount. entity=" + entity + ", maxPickAmount="
					+ entity.getMaxPickAmount());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notNegativeAmount");
		}

		StorageLocation location = manager.reload(entity.getStorageLocation(), false);
		Client locationClient = location.getClient();
		if (!locationClient.isSystemClient()) {
			ItemData itemData = manager.reload(entity.getItemData(), false);
			Client itemDataClient = itemData.getClient();
			if (!itemDataClient.isSystemClient()) {
				if (!locationClient.equals(itemDataClient)) {
					logger.log(Level.INFO, logStr + "invalid client. entity=" + entity + ", locations client="
							+ locationClient + ", itemDatas client=" + itemDataClient);
					throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidClientAssotiation");
				}
			}
		}
		String[] attributes = { "storageLocation", "itemData" };
		Object[] values = { entity.getStorageLocation(), entity.getItemData() };
		if (entitySerivce.exists(FixAssignment.class, attributes, values, entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. storageLocation/itemData. entity=" + entity
					+ ", storageLocation=" + entity.getStorageLocation() + ", itemData=" + entity.getItemData());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}

	}

	@Override
	public void validateDelete(FixAssignment entity) throws BusinessException {
	}

}
