/* 
Copyright 2019 Matthias Krane
info@krane.engineer

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
package de.wms2.mywms.replenish;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for replenish order
 * 
 * @author krane
 *
 */
public class ReplenishOrderValidator implements EntityValidator<ReplenishOrder> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService genericSerivce;

	@Override
	public void validateCreate(ReplenishOrder entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(ReplenishOrder entityOld, ReplenishOrder entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(ReplenishOrder entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getOrderNumber())) {
			logger.log(Level.INFO, logStr + "missing number. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}
		if (genericSerivce.exists(ReplenishOrder.class, "orderNumber", entity.getOrderNumber(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique, orderNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}

		if (entity.getItemData() == null) {
			logger.log(Level.INFO, logStr + "missing itemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}
		if (entity.getClient() == null) {
			entity.setClient(entity.getItemData().getClient());
		}

		if (entity.getDestinationLocation() == null) {
			logger.log(Level.INFO, logStr + "missing destination. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingDestination");
		}

	}

	@Override
	public void validateDelete(ReplenishOrder entity) throws BusinessException {
	}

}
