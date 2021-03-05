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
package de.wms2.mywms.report;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for Report
 * 
 * @author krane
 *
 */
public class ReportValidator implements EntityValidator<Report> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;

	@Override
	public void validateCreate(Report entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(Report oldEntity, Report newEntity) throws BusinessException {
		validate(newEntity);
	}

	private void validate(Report entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getClient() == null) {
			logger.log(Level.INFO, logStr + "missing client. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingClient");
		}
		if (StringUtils.isBlank(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}
		if (StringUtils.isBlank(entity.getReportVersion())) {
			logger.log(Level.INFO, logStr + "missing reportVersion. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingVersion");
		}

		if (entitySerivce.exists(Report.class, new String[] { "name", "client", "reportVersion" },
				new Object[] { entity.getName(), entity.getClient(), entity.getReportVersion() }, entity.getId())) {
			logger.log(Level.INFO,
					logStr + "not unique. name=" + entity.getName() + ", reportVersion=" + entity.getReportVersion());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}

	}

	@Override
	public void validateDelete(Report entity) throws BusinessException {
	}

}
