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

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for AdviceLine
 * 
 * @author krane
 *
 */
public class AdviceLineValidator implements EntityValidator<AdviceLine> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService genericSerivce;

	@Override
	public void validateCreate(AdviceLine entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(AdviceLine entityOld, AdviceLine entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(AdviceLine entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getAdvice() == null) {
			logger.log(Level.INFO, logStr + "missing advice. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingAdvice");
		}

		if (StringUtils.isEmpty(entity.getLineNumber())) {
			logger.log(Level.INFO, logStr + "missing line number. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLineNumber");
		}

		if (StringUtils.isEmpty(entity.getLineNumber())) {
			logger.log(Level.INFO, logStr + "missing number. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}
		if (genericSerivce.exists(AdviceLine.class, "lineNumber", entity.getLineNumber(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique, lineNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}

		if (entity.getItemData() == null) {
			logger.log(Level.INFO, logStr + "missing itemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingItemData");
		}

		if (entity.getAmount() == null) {
			logger.log(Level.INFO, logStr + "missing amount. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingAmount");
		}
		if (entity.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.INFO,
					logStr + "notified amount <= 0. entity=" + entity + ", amount=" + entity.getAmount());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidAmount");
		}

	}

	@Override
	public void validateDelete(AdviceLine entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (genericSerivce.existsReference(GoodsReceipt.class, "adviceLines", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to GoodsReceipt.adviceLines. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByGoodsReceipt");
		}
	}

}
