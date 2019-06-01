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
package de.linogistix.los.inventory.crud;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.PackagingUnitService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.product.PackagingUnit;

/**
 * @author krane
 *
 */
@Stateless
public class PackagingUnitCRUDBean extends BusinessObjectCRUDBean<PackagingUnit> implements PackagingUnitCRUDRemote {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@EJB
	PackagingUnitService service;

	@Override
	protected BasicService<PackagingUnit> getBasicService() {
		return service;
	}
	
	@Override
	public void update(PackagingUnit entity) throws BusinessObjectNotFoundException, BusinessObjectModifiedException,
			BusinessObjectMergeException, BusinessObjectSecurityException, FacadeException {

		PackagingUnit original = manager.find(PackagingUnit.class, entity.getId());
		if (original != null && !Objects.equals(entity.getItemData(), original.getItemData())) {
			logger.log(Level.INFO, "Cannot change ItemData of packagingUnit=" + entity);
			throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA,
					new Object[] { entity.getItemData().getNumber(), original.getItemData().getNumber() });
		}

		super.update(entity);
	}
}
