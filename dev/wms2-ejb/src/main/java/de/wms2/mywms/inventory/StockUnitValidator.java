/* 
Copyright 2020 Matthias Krane
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
package de.wms2.mywms.inventory;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for StockUnit
 * 
 * @author krane
 *
 */
public class StockUnitValidator implements EntityValidator<StockUnit> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void validateCreate(StockUnit entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(StockUnit entityOld, StockUnit entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(StockUnit entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getStrategyDate() == null) {
			logger.log(Level.INFO, logStr + "missing strategy date. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingStrategyDate");
		}
	}

	@Override
	public void validateDelete(StockUnit entity) throws BusinessException {
	}
}
