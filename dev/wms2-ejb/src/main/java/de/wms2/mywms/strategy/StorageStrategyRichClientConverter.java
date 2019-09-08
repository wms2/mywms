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

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.BasicEntity;

/**
 * 
 * @author krane
 *
 */
public class StorageStrategyRichClientConverter extends BasicEntity {
	private static final long serialVersionUID = 1L;

	public final static int UNDEFINED = -1;
	public final static int ORDER_BY_NAME = 1;
	public final static int ORDER_BY_ZONE_NAME = 2;
	public final static int ORDER_BY_ZONE_X_Y = 3;
	public final static int ORDER_BY_ZONE_Y_X = 4;
	public final static int ORDER_BY_CLIENT_NAME = 5;
	public final static int ORDER_BY_CLIENT_ZONE_NAME = 6;
	public final static int ORDER_BY_CLIENT_ZONE_X_Y = 7;
	public final static int ORDER_BY_CLIENT_ZONE_Y_X = 8;

	/**
	 * Convert orderByMode to comma separated String
	 * 
	 * @return String representation of known combination. Otherwise null.
	 */
	public static String convertOrderByModeToSorts(int orderByMode) {
		switch (orderByMode) {
		case ORDER_BY_NAME:
			return "NAME";
		case ORDER_BY_ZONE_NAME:
			return "ZONE,NAME";
		case ORDER_BY_ZONE_X_Y:
			return "ZONE,POSITION_X,POSITION_Y";
		case ORDER_BY_ZONE_Y_X:
			return "ZONE,POSITION_Y,POSITION_X";
		case ORDER_BY_CLIENT_NAME:
			return "CLIENT,NAME";
		case ORDER_BY_CLIENT_ZONE_NAME:
			return "CLIENT,ZONE,NAME";
		case ORDER_BY_CLIENT_ZONE_X_Y:
			return "CLIENT,ZONE,POSITION_X,POSITION_Y";
		case ORDER_BY_CLIENT_ZONE_Y_X:
			return "CLIENT,ZONE,POSITION_Y,POSITION_X";
		}
		return null;
	}

	/**
	 * Convert comma separated sort String to orderByMode
	 * 
	 * @return orderByMode representation of known combination. Otherwise UNDEFINED.
	 */
	public static int convertSortsToOrderByMode(String sorts) {
		if (StringUtils.startsWithIgnoreCase(sorts, "CLIENT,ZONE,POSITION_Y,POSITION_X")) {
			return ORDER_BY_CLIENT_ZONE_Y_X;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "CLIENT,ZONE,POSITION_X,POSITION_Y")) {
			return ORDER_BY_CLIENT_ZONE_X_Y;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "CLIENT,ZONE,NAME")) {
			return ORDER_BY_CLIENT_ZONE_NAME;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "CLIENT,NAME")) {
			return ORDER_BY_CLIENT_NAME;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "ZONE,POSITION_Y,POSITION_X")) {
			return ORDER_BY_ZONE_Y_X;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "ZONE,POSITION_X,POSITION_Y")) {
			return ORDER_BY_ZONE_X_Y;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "ZONE,NAME")) {
			return ORDER_BY_ZONE_NAME;
		} else if (StringUtils.startsWithIgnoreCase(sorts, "NAME")) {
			return ORDER_BY_NAME;
		}
		return UNDEFINED;
	}

}
