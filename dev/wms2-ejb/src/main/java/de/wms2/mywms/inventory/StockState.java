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

/**
 * StockState defines states for StockUnit and UnitLoad
 * 
 * @author krane
 *
 */
public class StockState {

	/**
	 * Stock is in incoming processes.<br>
	 * Not usable for ordinary outgoing operations.<br>
	 * Not synchronized with ERP.
	 */
	public static final int UNDEFINED = 0;

	/**
	 * Stock is in incoming processes.<br>
	 * Not usable for ordinary outgoing operations.<br>
	 * Not synchronized with ERP.
	 */
	public static final int INCOMING = 100;

	/**
	 * Normal store stock
	 */
	public static final int ON_STOCK = 300;

	/**
	 * Stock is picked for outgoing processing.<br>
	 */
	public static final int PICKED = 600;

	/**
	 * Stock is picked for outgoing processing.<br>
	 */
	public static final int PACKED = 650;

	/**
	 * Stock has left warehouse
	 */
	public static final int SHIPPED = 680;

	/**
	 * Can be removed
	 */
	public static final int DELETABLE = 1000;
}
