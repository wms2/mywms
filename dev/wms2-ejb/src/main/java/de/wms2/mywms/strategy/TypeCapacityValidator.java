/* 
Copyright 2019-2021 Matthias Krane

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

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for TypeCapacityConstraint
 * 
 * @author krane
 *
 */
public class TypeCapacityValidator implements EntityValidator<TypeCapacityConstraint> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(TypeCapacityConstraint entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(TypeCapacityConstraint entityOld, TypeCapacityConstraint entityNew)
			throws BusinessException {
		validate(entityNew);
	}

	public void validate(TypeCapacityConstraint entity) throws BusinessException {
		String logStr = "validate ";
		if (entity.getLocationType() == null) {
			logger.log(Level.INFO, logStr + "Missing locationType. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocationType");
		}
		if (entity.getUnitLoadType() == null) {
			logger.log(Level.INFO, logStr + "Missing unitLoadType. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingUnitLoadType");
		}
		if (entity.getAllocation() == null) {
			logger.log(Level.INFO, logStr + "Missing allocation. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingAllocation");
		}
		if (entity.getAllocation().compareTo(BigDecimal.ZERO) < 0) {
			logger.log(Level.INFO, logStr + "allocation must not be negative. allocation=" + entity.getAllocation()
					+ ", entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notNegativeAllocation");
		}

		String[] attributes = { "locationType", "unitLoadType" };
		Object[] values = { entity.getLocationType(), entity.getUnitLoadType() };
		if (entitySerivce.exists(TypeCapacityConstraint.class, attributes, values, entity.getId())) {
			logger.log(Level.INFO, logStr + "Not unique. locationType=" + entity.getLocationType() + ", unitLoadType="
					+ entity.getUnitLoadType() + ", entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}
	}

	@Override
	public void validateDelete(TypeCapacityConstraint entity) throws BusinessException {
	}
}
