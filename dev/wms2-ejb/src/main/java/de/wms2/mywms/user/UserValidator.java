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
package de.wms2.mywms.user;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.User;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for User
 * 
 * @author krane
 *
 */
public class UserValidator implements EntityValidator<User> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private UserEntityService userService;

	@Override
	public void validateCreate(User entity) throws BusinessException {
		validate(entity);

		if (userService.existsIgnoreCase(entity.getName())) {
			logger.log(Level.INFO, "Not unique. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueName");
		}
	}

	@Override
	public void validateUpdate(User entityOld, User entityNew) throws BusinessException {
		validate(entityNew);

		if (!StringUtils.equals(entityOld.getName(), entityNew.getName())) {
			if (userService.existsIgnoreCase(entityNew.getName())) {
				logger.log(Level.INFO, "Not unique. name=" + entityNew.getName());
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueName");
			}
		}
	}

	private void validate(User entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}
		if (StringUtils.containsAny(entity.getName(), "/*$!\"\\$%&=?'")) {
			logger.log(Level.INFO, logStr + "invalid characters in name. name=" + entity.getName());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidCharacterName");
		}
	}

	@Override
	public void validateDelete(User entity) throws BusinessException {
	}

}
