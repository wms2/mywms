/* 
Copyright 2014-2019 Matthias Krane

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for list handling
 * 
 * @author krane
 *
 */
public class ListUtils {

	/**
	 * Convert a comma separated String into a List
	 */
	public static List<String> stringToList(String s) {
		if (StringUtils.isEmpty(s)) {
			return new ArrayList<String>();
		}
		return Arrays.asList(s.split(","));
	}

	/**
	 * Convert a comma separated String into a String array
	 */
	public static String[] stringToArray(String s) {
		if (StringUtils.isEmpty(s)) {
			return new String[] {};
		}
		return s.split(",");
	}

	/**
	 * Convert a List into a comma separated String
	 */
	public static String listToString(List<String> list) {
		if (list == null) {
			return null;
		}
		if (list.size() == 0) {
			return null;
		}
		String ret = "";
		int i = 0;
		for (String s : list) {
			if (StringUtils.isEmpty(s)) {
				continue;
			}
			if (i++ > 0) {
				ret += ",";
			}
			ret += s;
		}
		return ret;
	}

}
