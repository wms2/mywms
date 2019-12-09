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
package de.wms2.mywms.inventory;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for UnitLoad
 * 
 * @author krane
 *
 */
public class UnitLoadValidator implements EntityValidator<UnitLoad> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entityService;

	@Override
	public void validateCreate(UnitLoad entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(UnitLoad entityOld, UnitLoad entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(UnitLoad entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getLabelId())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}

		if (entityService.exists(UnitLoad.class, "labelId", entity.getLabelId(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique. labelId=" + entity.getLabelId());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUnique");
		}
	}

	@Override
	public void validateDelete(UnitLoad entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entityService.exists(UnitLoad.class, "carrierUnitLoad", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to carrier UnitLoad. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByUnitLoad");
		}
		if (entityService.exists(Packet.class, "unitLoad", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to Packet. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByPacket");
		}
		if (entityService.exists(TransportOrder.class, "unitLoad", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to TransportOrder. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByTransport");
		}
	}
}
