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
package de.wms2.mywms.exception;

import java.util.Locale;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

import de.wms2.mywms.util.Translator;

/**
 * BusinessException is the super class of the business exceptions.
 * <p>
 * The basic feature of the BusinessException is internationalization.
 * <p>
 * The BusinessException is using a BundleResolver class to get class loader
 * information to load the bundle files.
 * <p>
 * Messages are supplied with a resource key and optional parameters. The
 * parameters are only used in generation of a localized message. They are
 * formatted by the String.format methods (%1$s)
 * 
 * This exception will cause a rollback of the current transaction
 *
 * @author krane
 */
@ApplicationException(rollback = true)
public class BusinessException extends FacadeException {
	private static final long serialVersionUID = 1L;

	public int HTTPStatusCode = 500;

	public BusinessException(Class<?> bundleResolver, String resourceKey) {
		super(resourceKey, resourceKey, null);

		setBundleResolver(bundleResolver);
		setBundleName("translation.Bundle");
	}

	public BusinessException(Class<?> bundleResolver, String resourceKey, String parameter) {
		super(resourceKey, resourceKey, new Object[] { parameter });

		setBundleResolver(bundleResolver);
		setBundleName("translation.Bundle");
	}

	public BusinessException(Class<?> bundleResolver, String resourceKey, Object[] parameters) {
		super(resourceKey, resourceKey, parameters);

		setBundleResolver(bundleResolver);
		setBundleName("translation.Bundle");
	}

	public BusinessException(Class<?> bundleResolver, String bundleName, String resourceKey, Object[] parameters) {
		super(resourceKey, resourceKey, parameters);

		setBundleResolver(bundleResolver);
		setBundleName(bundleName);
	}

	/**
	 * Get the localized message.
	 * <p>
	 * The message contains the parameters by String.format().
	 * <p>
	 * The system default locale is used for localization.
	 */
	public String getLocalizedMessage() {
		return getLocalizedMessage(null);
	}

	/**
	 * Get the localized message.
	 * <p>
	 * The message contains the parameters by String.format().
	 */
	public String getLocalizedMessage(Locale locale) {
		return Translator.getString(getBundleResolver(), getBundleName(), null, getKey(), null, locale,
				getParameters());
	}

	public String getResourceKey() {
		return getKey();
	}

}
