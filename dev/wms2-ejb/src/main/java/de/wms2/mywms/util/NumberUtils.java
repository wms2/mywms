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
package de.wms2.mywms.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for number handling
 * 
 * @author krane
 *
 */
public class NumberUtils {

	public static final BigDecimal THOUSAND = new BigDecimal("1000");
	public static final BigDecimal HUNDRED = new BigDecimal("100");

	/**
	 * Convert a String into a BigDecimal
	 */
	public static BigDecimal parseBigDecimal(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}

		try {
			BigDecimal number = new BigDecimal(code);
			return number;
		} catch (NumberFormatException e) {
		}

		try {
			// Formats where the decimal separator is a ','
			BigDecimal number = new BigDecimal(code.replace(",", "."));
			return number;
		} catch (NumberFormatException e) {
		}

		return null;
	}

	/**
	 * Set the scale of a BigDecimal value.
	 * <p>
	 * Trailing zeros are removed<br>
	 * null parameter will give a null return value
	 */
	public static BigDecimal scale(BigDecimal value, int maxScale) {
		if (value == null) {
			return null;
		}
		return value.setScale(maxScale, RoundingMode.HALF_UP).stripTrailingZeros();
	}
}
