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
package de.wms2.mywms.module;

import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.property.SystemProperty;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2Properties;

/**
 * Setup of a module.
 * <p>
 * Every module has it's own setup service. It is used to generate basic data,
 * demo data or run test procedures.
 * 
 * @author krane
 *
 */
public abstract class ModuleSetup {

	@Inject
	private SystemPropertyBusiness propertyBusiness;

	public enum SetupLevel {
		UNINITIALIZED, INITIALIZED, EXPIRED, DEMO_SMALL, DEMO_MEDIUM, DEMO_LARGE
	}

	/**
	 * Get the modules base package.
	 * <p>
	 * The package has to be unique within the application.
	 */
	public abstract String getModulePackage();

	/**
	 * Get the name of the SystemProperty, which holds the modules status
	 * information.
	 */
	public String getModulePropertyName() {
		return Wms2Properties.GROUP_SETUP + ":" + getModulePackage();
	}

	public boolean isActive() {
		String value = propertyBusiness.getString(getModulePropertyName(), null);
		if (StringUtils.equals(value, SetupLevel.INITIALIZED.name())) {
			return true;
		}
		if (StringUtils.startsWith(value, "DEMO")) {
			return true;
		}
		return false;
	}

	/**
	 * Give the setup methods an order to be called.<br>
	 * The base modules use values below 10 to be the first modules to be
	 * initialized.<br>
	 * If possible leave this at the default value.
	 */
	public int getOrderIndex() {
		return 99;
	}

	/**
	 * Start the setup for the given level.
	 * 
	 * @param level  The SetupLevel
	 * @param locale The language
	 */
	public abstract void setup(SetupLevel level, Locale locale) throws BusinessException;

	/**
	 * Start the selftest of the module.
	 * <p>
	 * Check whether all setup or other logical requirements are working
	 */
	public boolean runSelfTest() throws BusinessException {
		return true;
	}

	/**
	 * Create a system property with a localized description and note values queried
	 * from bundle.
	 * 
	 * @param bundleResolver A class file which is located in the same package as
	 *                       the bundle files
	 * @param key            The property key
	 * @param value          The property value
	 * @param group          The property group
	 * @param locale         The locale
	 */
	protected SystemProperty createProperty(Class<?> bundleResolver, String key, String value, String group, Locale locale) {
		return createProperty(bundleResolver, key, value, group, key, key, locale);
	}

	/**
	 * Create a system property with a localized description and note values queried
	 * from bundle.
	 * 
	 * @param bundleResolver A class file which is located in the same package as
	 *                       the bundle files
	 * @param key            The property key
	 * @param value          The property value
	 * @param group          The property group
	 * @param bundleKeyDesc  Optional, The bundle key to read the description from
	 *                       bundle
	 * @param bundleKeyNote  Optional, The bundle key to read the note from bundle
	 * @param locale         The locale
	 */
	protected SystemProperty createProperty(Class<?> bundleResolver, String key, String value, String group, String bundleKeyDesc,
			String bundleKeyNote, Locale locale) {

		String desc = null;
		if (!StringUtils.isBlank(bundleKeyDesc)) {
			String bundleKey = "property" + key;
			desc = Translator.getString(bundleResolver, "BasicData", bundleKey, "desc", null, locale);
		}
		SystemProperty property = propertyBusiness.createOrUpdate(key, null, null, value, group, desc);
		if (!StringUtils.isBlank(bundleKeyNote)) {
			String bundleKey = "property" + key;
			String note = Translator.getString(bundleResolver, "BasicData", bundleKey, "note", null, locale);
			property.setAdditionalContent(note);
		}

		return property;
	}

}
