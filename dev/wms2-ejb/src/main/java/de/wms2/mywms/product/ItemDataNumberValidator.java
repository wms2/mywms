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
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service of ItemDataNumber
 * 
 * @author krane
 *
 */
public class ItemDataNumberValidator implements EntityValidator<ItemDataNumber> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(ItemDataNumber entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(ItemDataNumber entityOld, ItemDataNumber entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(ItemDataNumber entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getItemData() == null) {
			logger.log(Level.INFO, logStr + "missing itemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}
		if (StringUtils.isEmpty(entity.getNumber())) {
			logger.log(Level.INFO, logStr + "missing code. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}

		String[] attributes = { "itemData", "number" };
		Object[] values = { entity.getItemData(), entity.getNumber() };
		if (entitySerivce.exists(ItemDataNumber.class, attributes, values, entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. itemData/number. entity=" + entity + ", itemData="
					+ entity.getItemData() + ", number=" + entity.getNumber());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}

		if (entity.getPackagingUnit() != null) {
			if (!entity.getItemData().equals(entity.getPackagingUnit().getItemData())) {
				logger.log(Level.INFO,
						logStr + "not matching packagingUnit.itemData. entity=" + entity + ", entity.itemData="
								+ entity.getItemData() + ", entity.packagingUnit.itemData="
								+ entity.getPackagingUnit().getItemData());
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notMatchingItemData");
			}
		}
	}

	@Override
	public void validateDelete(ItemDataNumber entity) throws BusinessException {
	}

}
