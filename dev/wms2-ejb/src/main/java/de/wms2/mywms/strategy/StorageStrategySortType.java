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

/**
 * Predefined sort types of storage strategy
 * 
 * @author krane
 *
 */
public enum StorageStrategySortType {
	CLIENT(0), ZONE(1), CAPACITY(2), ALLOCATION(3), POSITION_X(4), POSITION_Y(5), NAME(6), ORDERINDEX(7);

	int intValue;

	StorageStrategySortType(int intValue) {
		this.intValue = intValue;
	}

	public int getIntValue() {
		return intValue;
	}
}
