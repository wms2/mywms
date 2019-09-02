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
package de.wms2.mywms.goodsreceipt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for GoodsReceipt
 * 
 * @author krane
 *
 */
public class GoodsReceiptValidator implements EntityValidator<GoodsReceipt> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService genericSerivce;

	@Override
	public void validateCreate(GoodsReceipt entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(GoodsReceipt entityOld, GoodsReceipt entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(GoodsReceipt entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getOrderNumber())) {
			logger.log(Level.INFO, logStr + "missing number. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}
		if (genericSerivce.exists(GoodsReceipt.class, "orderNumber", entity.getOrderNumber(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique orderNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}

		if (entity.getClient() == null) {
			logger.log(Level.INFO, logStr + "missing client. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingClient");
		}

		if (entity.getStorageLocation() == null) {
			logger.log(Level.INFO, logStr + "missing location. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLocation");
		}

		if (!entity.isUseAdvice()) {
			if (entity.getAdviceLines() != null && entity.getAdviceLines().size() > 0) {
				logger.log(Level.INFO,
						logStr + "cannot handle advices for GoodsReceipt with useAdvice=false. entity=" + entity);
				throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidMustNotHaveAdvice");
			}
		}
	}

	@Override
	public void validateDelete(GoodsReceipt entity) throws BusinessException {
	}

}
