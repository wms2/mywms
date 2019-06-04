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
package de.wms2.mywms.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;
import org.mywms.model.Document;
import org.mywms.model.User;

import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemProperty;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for Client
 * 
 * @author krane
 *
 */
public class ClientValidator implements EntityValidator<Client> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	GenericEntityService entitySerivce;

	@Override
	public void validateCreate(Client entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(Client entityOld, Client entityNew) throws BusinessException {
		validate(entityNew);
	}

	private void validate(Client entity) throws BusinessException {
		String logStr = "validate ";

		if (StringUtils.isEmpty(entity.getNumber())) {
			logger.log(Level.INFO, logStr + "missing number. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}
		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}
		if (entitySerivce.exists(Client.class, "name", entity.getName(), entity.getId())) {
			logger.log(Level.INFO, logStr + "not unique, name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueName");
		}
	}

	@Override
	public void validateDelete(Client entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(SystemProperty.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to LOSSystemProperty. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedBySystemProperty");
		}
		if (entitySerivce.exists(StockUnit.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to StockUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByStockUnit");
		}
		if (entitySerivce.exists(UnitLoad.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to UnitLoad. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByUnitLoad");
		}
		if (entitySerivce.exists(ItemData.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemData. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemData");
		}
		if (entitySerivce.exists(Lot.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to Lot. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByLot");
		}
		if (entitySerivce.exists(StorageLocation.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to StorageLocation. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByStorageLocation");
		}
		if (entitySerivce.exists(Document.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to Document. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByDocument");
		}
		if (entitySerivce.exists(User.class, "client", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to User. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByUser");
		}
	}
}