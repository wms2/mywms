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
package de.wms2.mywms.inventory;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * @author krane
 *
 */
@Stateless
public class InventoryBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Changes the packaging unit. <br>
	 * Only the packaging information is changed. The amount of base units will not
	 * be changed!
	 */
	public StockUnit changePackagingUnit(StockUnit stock, PackagingUnit packagingUnit) throws BusinessException {
		String logStr = "changePackagingUnit ";
		logger.log(Level.FINE, logStr + "stock=" + stock + ", packagingUnit=" + packagingUnit);

		if (packagingUnit != null) {
			if (!Objects.equals(packagingUnit.getItemData(), stock.getItemData())) {
				logger.log(Level.WARNING,
						logStr + "Different products in in StockUnit and PackagingUnit. stock.itemData="
								+ stock.getItemData() + ", unit.itemData=" + packagingUnit.getItemData());
				throw new BusinessException(Wms2BundleResolver.class, "Validator.inequalProduct");
			}
		}

		stock.setPackagingUnit(packagingUnit);

		return stock;
	}
}
