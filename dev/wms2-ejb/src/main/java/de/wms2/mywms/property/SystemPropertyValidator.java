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
package de.wms2.mywms.property;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for SystemProperty
 * 
 * @author krane
 *
 */
public class SystemPropertyValidator implements EntityValidator<SystemProperty> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private SystemPropertyBusiness propertyBusiness;

	@Override
	public void validateCreate(SystemProperty entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(SystemProperty entityOld, SystemProperty entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(SystemProperty entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getClient() == null) {
			logger.log(Level.INFO, logStr + "missing client. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingClient");
		}
		if (StringUtils.isEmpty(entity.getPropertyKey())) {
			logger.log(Level.INFO, logStr + "missing key. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingKey");
		}

		SystemProperty property = propertyBusiness.read(entity.getPropertyKey(), entity.getClient(),
				entity.getPropertyContext());
		if (property != null && !property.equals(entity)) {
			logger.log(Level.INFO, logStr + "not unique, tenant/key. key=" + entity.getPropertyKey() + ", tenant="
					+ entity.getClient());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}
	}

	@Override
	public void validateDelete(SystemProperty entity) throws BusinessException {
	}

}
