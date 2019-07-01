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
public class BusinessException extends java.lang.Exception {
	private static final long serialVersionUID = 1L;

	protected String resourceKey;
	protected Object[] parameters;
	protected Class<?> bundleResolver = BusinessException.class;
	protected String bundleName;
	public int HTTPStatusCode = 500;

	public BusinessException(Class<?> bundleResolver, String resourceKey) {
		super(resourceKey);
		this.resourceKey = resourceKey;
		this.parameters = null;
		this.bundleResolver = bundleResolver;
	}

	public BusinessException(Class<?> bundleResolver, String resourceKey, String parameter) {
		super(resourceKey);
		this.resourceKey = resourceKey;
		this.parameters = new Object[] { parameter };
		this.bundleResolver = bundleResolver;
	}

	public BusinessException(Class<?> bundleResolver, String resourceKey, Object[] parameters) {
		super(resourceKey);
		this.resourceKey = resourceKey;
		this.parameters = parameters;
		this.bundleResolver = bundleResolver;
	}

	public BusinessException(Class<?> bundleResolver, String bundleName, String resourceKey, Object[] parameters) {
		super(resourceKey);
		this.resourceKey = resourceKey;
		this.parameters = parameters;
		this.bundleResolver = bundleResolver;
		this.bundleName = bundleName;
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
		return Translator.getString(bundleResolver, bundleName, null, resourceKey, null, locale, parameters);
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * By setting the BundleResolver you set the translation to a different bundle.
	 * <p>
	 * The BundleResolver is a class inside the package, where the resource bundle
	 * files are located.
	 */
	public void setBundleResolver(Class<?> bundleResolver) {
		this.bundleResolver = bundleResolver;
	}

	public Class<?> getBundleResolver() {
		return this.bundleResolver;
	}

	public FacadeException toFacadeException() {
		FacadeException facadeException = new FacadeException(getMessage(), getMessage(), new Object[] {}, getBundleResolver());
		facadeException.setBundleName("translation.Bundle");
		return facadeException;
	}

}
