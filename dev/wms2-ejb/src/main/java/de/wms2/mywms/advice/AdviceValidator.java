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
package de.wms2.mywms.advice;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for Advice
 * 
 * @author krane
 *
 */
public class AdviceValidator implements EntityValidator<Advice> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService genericSerivce;

	@Override
	public void validateCreate(Advice entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(Advice entityOld, Advice entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(Advice entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getOrderNumber())) {
			logger.log(Level.INFO, logStr + "missing number. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}
		if (genericSerivce.exists(Advice.class, "orderNumber", entity.getOrderNumber(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique, orderNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}
	}

	@Override
	public void validateDelete(Advice entity) throws BusinessException {
	}

}
