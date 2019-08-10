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

public class LockType {

	public static final int UNLOCKED = 0;
	public static final int GENERAL = 1;
	public static final int STOCKTAKING = 7;
	public static final int QUALITY_FAULT = 103;
	public static final int LOT_EXPIRED = 202;
	public static final int LOT_TOO_YOUNG = 203;
	public static final int SHIPPED = 405;

}
