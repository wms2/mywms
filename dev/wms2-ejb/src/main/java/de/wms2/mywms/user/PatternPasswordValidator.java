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
package de.wms2.mywms.user;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.User;
import org.mywms.res.BundleResolver;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.util.Wms2Properties;

/**
 * Validation of passwords.
 * <p>
 * The password has to match a regular expression.
 * 
 * @author krane
 *
 */
public class PatternPasswordValidator implements PasswordValidator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private SystemPropertyBusiness propertyBusiness;

	public void validate(User user, String password, String encryptedPassword) throws BusinessException {
		String logStr = "validate ";

		String passwordCheckExpression = propertyBusiness.getString(Wms2Properties.KEY_PASSWORD_EXPRESSION, null);
		if (!StringUtils.isEmpty(passwordCheckExpression)) {
			if (password == null) {
				password = "";
			}

			Pattern pattern = Pattern.compile(passwordCheckExpression);
			Matcher matcher = pattern.matcher(password);
			if (!matcher.matches()) {
				logger.log(Level.INFO, logStr + "Password does not match the policy expression. user=" + user);
				throw new BusinessException(BundleResolver.class, "Validator.invalidPassword");
			}
		}

	}

}
