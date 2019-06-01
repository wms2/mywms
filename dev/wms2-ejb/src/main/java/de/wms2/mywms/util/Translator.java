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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 * Translation with ResourceBundle files
 * <p>
 * ResourceBundle files are loaded by the ClassLoader of a bundleResolver. As
 * fallback the parent classes of the given resolver are used.
 * <p>
 * The bundle entries have a logical structure. The key consists of 3 parts.
 * These parts are separated by a dot '.'.
 * <p>
 * <code>group.key.subkey = value</code>
 * <p>
 * The access is done in several steps until a value is found: <code>
 * <li>group.key.subKey
 * <li>group.key
 * <li>key.subKey
 * <li>key
 * </code>
 * <p>
 * In case of no success the search is repeated with the BaseBundleResolver.
 * <p>
 * 
 * @author krane
 */
public class Translator {
	private static final Logger logger = Logger.getLogger(Translator.class.getName());
	private static final String DEFAULT_BUNDLE_NAME = "translation.Bundle";

	/**
	 * Parses and returns Locale by the given String (en_US / de_CH)
	 * 
	 * @return Locale null if not possible to parse
	 */
	public static Locale parseLocale(String localeString) {
		Locale locale = null;

		if (localeString == null) {
			return Locale.getDefault();
		}

		locale = Locale.forLanguageTag(localeString);
		if (locale != null && !StringUtils.isEmpty(locale.getLanguage())) {
			return locale;
		}

		String[] localeTokens = localeString.split("_");
		switch (localeTokens.length) {
		case 1:
			locale = new Locale(localeTokens[0]);
			break;
		case 2:
			locale = new Locale(localeTokens[0], localeTokens[1]);
			break;
		case 3:
			locale = new Locale(localeTokens[0], localeTokens[1], localeTokens[2]);
			break;
		default:
			locale = null;
		}

		if (locale == null) {
			locale = Locale.getDefault();
		}

		return locale;
	}

	public static String getString(Class<?> resolver, String key, Locale locale) {
		return getStringInternal(resolver, resolver, null, null, key, null, true, locale);
	}

	public static String getString(Class<?> resolver, String key, String subKey, Locale locale) {
		return getStringInternal(resolver, resolver, null, null, key, subKey, true, locale);
	}

	public static String getString(Class<?> resolver, String group, String key, String subKey, Locale locale) {
		return getStringInternal(resolver, resolver, null, group, key, subKey, true, locale);
	}

	public static String getString(Class<?> resolver, String group, String key, String subKey, String defaultValue,
			Locale locale) {
		String value = getStringInternal(resolver, resolver, null, group, key, subKey, false, locale);
		if (StringUtils.equals(key, value)) {
			return defaultValue;
		}
		return value;
	}

	public static String getString(Class<?> resolver, String key, Locale locale, Object... parameters) {
		return format(getStringInternal(resolver, resolver, null, null, key, null, true, locale), parameters);
	}

	public static String getString(Class<?> resolver, String key, String subKey, Locale locale, Object... parameters) {
		return format(getStringInternal(resolver, resolver, null, null, key, subKey, true, locale), parameters);
	}

	public static String getString(Class<?> resolver, String group, String key, String subKey, Locale locale,
			Object... parameters) {
		return format(getStringInternal(resolver, resolver, null, group, key, subKey, true, locale), parameters);
	}

	public static String getString(Class<?> resolver, String bundleName, String group, String key, String subKey,
			Locale locale, Object... parameters) {
		return format(getStringInternal(resolver, resolver, bundleName, group, key, subKey, true, locale), parameters);
	}

	private static String getStringInternal(Class<?> resolver, Class<?> originalResolver, String bundleName,
			String group, String key, String subKey, boolean warnNotExisting, Locale locale) {
		ResourceBundle bundle = getBundle(resolver, bundleName, locale);
		if (bundle == null) {
			return key;
		}
		if (key == null) {
			return "";
		}

		if (group != null && subKey != null) {
			try {
				return bundle.getString(group + "." + key + "." + subKey);
			} catch (Throwable e) {
			}
		}

		if (group != null) {
			try {
				return bundle.getString(group + "." + key);
			} catch (Throwable e) {
			}
		}

		if (subKey != null) {
			try {
				return bundle.getString(key + "." + subKey);
			} catch (Throwable e) {
			}
		}

		try {
			return bundle.getString(key);
		} catch (Throwable e) {
		}

		if (resolver != null) {
			Class<?> superResolver = resolver.getSuperclass();
			if (superResolver != null && !resolver.equals(Object.class)) {
				if (originalResolver == null) {
					originalResolver = resolver;
				}
				return getStringInternal(superResolver, originalResolver, bundleName, group, key, subKey,
						warnNotExisting, locale);
			}
		}

		if (warnNotExisting) {
			logger.log(Level.WARNING, "Translator, missing translation: bundleName=" + bundleName + ", group=" + group
					+ ", key=" + key + ", subKey=" + subKey + ", locale=" + locale + ", resolver=" + originalResolver);
		}

		return key;
	}

	private static String format(String value, Object[] parameters) {
		if (parameters != null && parameters.length > 0) {
			try {
				value = String.format(value, parameters);
			} catch (Throwable t) {
			}
		}
		return value;
	}

	/**
	 * Load the ResourceBundle
	 * 
	 * @param resolver   To load the bundle, the classloader of this class is used.
	 *                   So it has to be in the same jar as the bundle files.
	 * @param bundleName Absolute name of the bundle. If empty, the package of the
	 *                   resolver is used with file name 'Bundle'
	 * @param locale     If empty, the systems default is used
	 */
	public static ResourceBundle getBundle(Class<?> resolver, String bundleName, Locale locale) {
		if (resolver == null) {
			resolver = Translator.class;
		}
		if (resolver.equals(Object.class)) {
			resolver = Translator.class;
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		if (StringUtils.isEmpty(bundleName)) {
			bundleName = DEFAULT_BUNDLE_NAME;
		}

		ClassLoader classLoader = resolver.getClassLoader();

		try {
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
			return bundle;
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Translator, missing bundle. bundleName=" + bundleName + ", resolver=" + resolver
					+ ", classLoader=" + classLoader);
			logger.log(Level.WARNING, "Translator, missing bundle. " + t.getClass().getName() + ", " + t.getMessage(),
					t);
		}

		return null;
	}

}
