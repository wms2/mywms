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
package de.wms2.mywms.address;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.shipping.ShippingOrder;

/**
 * @author krane
 *
 */
@Stateless
public class AddressEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;

	@Inject
	private GenericEntityService entityService;

	public void removeIfUnused(Address address) throws BusinessException {
		if (entityService.exists(DeliveryOrder.class, "address", address)) {
			logger.info("Do not remove address. Used by delivery order. address="+address);
			return;
		}
		if (entityService.exists(PickingOrder.class, "address", address)) {
			logger.info("Do not remove address. Used by delivery order. address="+address);
			return;
		}
		if (entityService.exists(ShippingOrder.class, "address", address)) {
			logger.info("Do not remove address. Used by delivery order. address="+address);
			return;
		}

		logger.info("Remove unused address. address="+address);
		manager.removeValidated(address);
	}

}
