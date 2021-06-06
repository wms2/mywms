/* 
Copyright 2020-2021 Matthias Krane
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
package de.wms2.mywms.sequence;

import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.exception.BusinessException;

/**
 * 3PL implementation of the CheckDigitService
 * 
 * @author krane
 * 
 */
@Stateless
public class CheckDigitService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public String calculateCheckDigit(String code, String type) throws BusinessException {
		if (StringUtils.equalsIgnoreCase(type, "modulo10")) {
			return calculateModulo10(code);
		}
		logger.warning("Undefined check digit type. Will not calculate check digit. type=" + type);
		return "";
	}

	private String calculateModulo10(String code) {
		int checkSum = 0;
		for (int i = code.length() - 1; i >= 0; i--) {
			checkSum += (code.charAt(i) - 48) * (i % 2 == 0 ? 3 : 1);
		}
		return String.valueOf((10 - (checkSum % 10)) % 10);
	}

}
