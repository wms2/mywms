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

import java.math.BigDecimal;
import java.util.Objects;
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
 * Validation service for PackagingUnit
 * 
 * @author krane
 *
 */
public class PackagingUnitValidator implements EntityValidator<PackagingUnit> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;
	@Inject
	private PackagingUnitEntityService packagingUnitEntityService;

	@Override
	public void validateCreate(PackagingUnit entity) throws BusinessException {
		validate(entity);

		if (packagingUnitEntityService.existsIgnoreCase(entity.getItemData(), entity.getName(), null)) {
			logger.log(Level.INFO, "Not unique. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueName");
		}

	}

	@Override
	public void validateUpdate(PackagingUnit oldEntity, PackagingUnit newEntity) throws BusinessException {
		validate(newEntity);

		if (!StringUtils.equals(oldEntity.getName(), newEntity.getName())) {
			if (packagingUnitEntityService.existsIgnoreCase(newEntity.getItemData(), newEntity.getName(),
					newEntity.getId())) {
				logger.log(Level.INFO,
						"Not unique. itemData=" + newEntity.getItemData() + ", name=" + newEntity.getName());
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueName");
			}
		}

		if (!Objects.equals(oldEntity.getItemData(), newEntity.getItemData())) {
			logger.log(Level.INFO, "Cannot change ItemData of packagingUnit=" + oldEntity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidItemDataChange");
		}
	}

	@Override
	public void validateDelete(PackagingUnit entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(StockUnit.class, "packagingUnit", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to StockUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByStockUnit");
		}
		if (entitySerivce.exists(ItemData.class, "defaultPackagingUnit", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemData");
		}
		if (entitySerivce.exists(ItemDataNumber.class, "packagingUnit", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemDataNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemDataNumber");
		}
	}

	public void validate(PackagingUnit entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getItemData() == null) {
			logger.log(Level.INFO, logStr + "missing itemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}
		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}
		if (entity.getAmount() == null) {
			logger.log(Level.INFO, logStr + "missing amount. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingAmount");
		}
		if (entity.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.INFO, logStr + "invalid amount. entity=" + entity + ", amount=" + entity.getAmount());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidAmount");
		}

	}
}
