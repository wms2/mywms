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
import org.mywms.model.Role;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for Role
 * 
 * @author krane
 *
 */
public class RoleValidator implements EntityValidator<Role> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService genericSerivce;

	@Override
	public void validateCreate(Role entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(Role entityOld, Role entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(Role entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}
		if (StringUtils.isEmpty(entity.getDescription())) {
			logger.log(Level.INFO, logStr + "missing description. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingDescription");
		}
		if (genericSerivce.exists(Role.class, "name", entity.getName(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique, name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueName");
		}
	}

	@Override
	public void validateDelete(Role entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (StringUtils.equals("Admin", entity.getName())) {
			logger.log(Level.INFO, logStr + "System-Role cannot be deleted. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidForSystemRole");
		}
	}
}
