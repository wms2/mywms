/* 
Copyright 2019-2022 Matthias Krane
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
package de.wms2.mywms.transport;

/**
 * Predefined types of transport orders
 * 
 * @author krane
 *
 */
public class TransportOrderType {
	public static final int UNDEFINED = 0;
	public static final int INBOUND = 1;
	public static final int OUTBOUND = 2;
	public static final int REPLENISH = 3;
	public static final int TRANSFER = 4;
	public static final int REPLENISH_FIX = 5;
	public static final int REPLENISH_AREA = 6;
}
