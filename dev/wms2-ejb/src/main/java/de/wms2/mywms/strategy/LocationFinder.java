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
package de.wms2.mywms.strategy;

import java.util.Collection;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 * Strategy Service. Find a StorageLocation
 * 
 * @author krane
 *
 */
public interface LocationFinder {

	/**
	 * Find a location where the stock can be added.<br>
	 * The product and lot is considered.<br>
	 * 1. Search fix assigned location (maybe empty)<br>
	 * 2. Search other picking location. (FIFO)<br>
	 * The vetoStocks are not used
	 */
	StorageLocation findAddToLocation(StockUnit sourceStock, Collection<StockUnit> vetoStocks);

	/**
	 * Find a StorageLocation for a UnitLoad<br>
	 * The location can be used for picking
	 */
	StorageLocation findPickingLocation(UnitLoad unitLoad, StorageStrategy strategy) throws BusinessException;

	/**
	 * Find a StorageLocation for a UnitLoad<br>
	 * The location can be used for normal storage
	 */
	StorageLocation findStorageLocation(UnitLoad unitLoad, StorageStrategy strategy) throws BusinessException;

	/**
	 * Find a StorageLocation for a UnitLoad<br>
	 * The location can be used for normal storage
	 * 
	 * @param targetStorageArea Only valid in combination with a strategy, that has
	 *                          a filled list of storage areas AND this list
	 *                          contains the given storage area.
	 */
	StorageLocation findStorageLocation(UnitLoad unitLoad, StorageStrategy strategy, StorageArea targetStorageArea)
			throws BusinessException;

	/**
	 * Try to find the storageStrategy of the products on the UnitLoad. If there are
	 * different strategies, null is returned.
	 */
	StorageStrategy readStorageStrategy(UnitLoad unitLoad);

}
