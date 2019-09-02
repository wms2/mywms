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
package de.wms2.mywms.picking;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.shipping.ShippingOrderLine;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for UnitLoadType
 * 
 * @author krane
 *
 */
public class PacketValidator implements EntityValidator<Packet> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entityService;

	@Override
	public void validateCreate(Packet entity) throws BusinessException {
	}

	@Override
	public void validateUpdate(Packet entityOld, Packet entityNew) throws BusinessException {
	}

	@Override
	public void validateDelete(Packet entity) throws BusinessException {
		if (entityService.exists(ShippingOrderLine.class, "packet", entity)) {
			logger.log(Level.INFO, "Existing reference to shipping order line. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByShippingOrder");
		}
	}
}
