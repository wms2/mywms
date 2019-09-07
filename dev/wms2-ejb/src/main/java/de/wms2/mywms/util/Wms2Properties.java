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
package de.wms2.mywms.util;

/**
 * Constants for the wms2 module
 * 
 * @author krane
 *
 */
public class Wms2Properties {

	/**
	 * Keys of system properties
	 */
	public final static String KEY_PASSWORD_EXPRESSION = "PASSWORD_REGULAR_EXPRESSION";
	public static final String KEY_ORDERSTRATEGY_DEFAULT = "STRATEGY_ORDER_DEFAULT";
	public static final String KEY_ORDERSTRATEGY_EXTINGUISH = "STRATEGY_ORDER_EXTINGUISH";
	public static final String KEY_LOCATIONCLUSTER_DEFAULT = "LOCATIONCLUSTER_DEFAULT";
	public static final String KEY_LOCATIONTYPE_DEFAULT = "LOCATIONTYPE_DEFAULT";
	public static final String KEY_UNITLOADTYPE_DEFAULT = "UNITLOADTYPE_DEFAULT";
	public static final String KEY_AREA_DEFAULT = "AREA_DEFAULT";
	public static final String KEY_REPORT_LOCALE = "REPORT_LOCALE";
	public static final String KEY_SHIPPING_LOCATION = "SHIPPING_LOCATION";
	public static final String KEY_SHIPPING_RENAME_UNITLOAD = "SHIPPING_RENAME_UNITLOAD";
	public static final String KEY_GOODSRECEIPT_LIMIT_AMOUNT_TO_NOTIFIED = "GOODS_RECEIPT_LIMIT_AMOUNT_TO_NOTIFIED";
	public static final String KEY_REPLENISH_FROM_PICKING = "REPLENISH_FROM_PICKING_LOCATION";

	/**
	 * Names of system property groups
	 */
	public final static String GROUP_SETUP = "SETUP";
	public final static String GROUP_UI = "UI";
	public final static String GROUP_MOBILE = "MOBILE";
	public final static String GROUP_SERVER = "SERVER";
	public final static String GROUP_GENERAL = "GENERAL";
	public final static String GROUP_WMS = "WMS";

	/**
	 * Names of sequences
	 */
	public static final String SEQ_SHIPPING_ORDER = "Shipment";
}
