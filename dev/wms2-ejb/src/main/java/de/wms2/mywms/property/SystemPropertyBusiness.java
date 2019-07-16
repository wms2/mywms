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
package de.wms2.mywms.property;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.PersistenceManager;

/**
 * The key of a system property contains of 3 parts<br>
 * - propertyKey: The main key<br>
 * - client: A property may be client-specific. At least the main implementation
 * with the system client has to exist.<br>
 * - propertyContext: An optional further subkey.
 * <p>
 * 
 * @author krane
 *
 */
@Stateless
public class SystemPropertyBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private PersistenceManager manager;

	/**
	 * Create a SystemProperty for the given propertyKey
	 * 
	 * @param key     The property key
	 * @param client  The Client of the property. If null the system client is used.
	 * @param context An optional subkey
	 * @param value   The value of the property
	 */
	public SystemProperty create(String key, Client client, String context, String value) {
		if (client == null) {
			client = clientBusiness.getSystemClient();
		}

		SystemProperty property = manager.createInstance(SystemProperty.class);
		property.setPropertyKey(key);
		property.setClient(client);
		property.setPropertyContext(context);
		property.setPropertyValue(value);

		manager.persist(property);

		return property;
	}

	/**
	 * Create or update a SystemProperty
	 * 
	 * @param key   The property key
	 * @param value The value of the property
	 * @param group The properties group
	 * @param desc  The properties description
	 */
	public SystemProperty createOrUpdate(String key, String value, String group, String desc) {
		return createOrUpdate(key, null, null, value, group, desc);
	}

	/**
	 * Create or update a SystemProperty
	 * 
	 * @param key     The property key
	 * @param client  The Client of the property. If null the system client is used.
	 * @param context An optional subkey
	 * @param value   The value of the property
	 * @param group   The properties group
	 * @param desc    The properties description
	 */
	public SystemProperty createOrUpdate(String key, Client client, String context, String value, String group,
			String desc) {
		if (client == null) {
			client = clientBusiness.getSystemClient();
		}

		SystemProperty property = read(key, client, context);
		if (property == null) {
			property = create(key, client, context, value);
		}
		property.setPropertyGroup(group);
		property.setDescription(desc);
		property.setPropertyValue(value);

		return property;
	}

	/**
	 * Read a SystemProperty for the given propertyKey
	 * 
	 * @param key The property key
	 */
	public SystemProperty read(String key) {
		return read(key, null, null);
	}

	/**
	 * Read a SystemProperty for the given propertyKey, client and context.
	 * <p>
	 * Do not search with adapted client and context settings.
	 * 
	 * @param key     The property key
	 * @param client  The Client of the property. If null the system client is used.
	 * @param context An optional subkey
	 */
	public SystemProperty read(String key, Client client, String context) {

		String jpql = "SELECT entity FROM " + SystemProperty.class.getName() + " entity ";
		jpql += " WHERE entity.propertyKey=:propertyKey ";
		if (client == null) {
			jpql += " and entity.client.id=0";
		} else {
			jpql += " and entity.client=:client";
		}
		if (StringUtils.isEmpty(context)) {
			jpql += " and (entity.propertyContext='' or entity.propertyContext is null)";
		} else {
			jpql += " and entity.propertyContext=:context";
		}

		Query query = manager.createQuery(jpql);
		query.setParameter("propertyKey", key);
		if (client != null) {
			query.setParameter("client", client);
		}
		if (!StringUtils.isEmpty(context)) {
			query.setParameter("context", context);
		}

		try {
			SystemProperty property = (SystemProperty) query.getSingleResult();
			return property;
		} catch (NoResultException ne) {
			return null;
		}
	}

	/**
	 * Read system property for the given key.
	 * 
	 * @param key          The property key
	 * @param defaultValue The value to return, if no property can be resolved
	 * @see SystemPropertyBusiness#getString(String, Client, String, String)
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return getBoolean(key, null, null, false);
	}

	/**
	 * Read system property for the given key.
	 * 
	 * @param key          The property key
	 * @param client       The Client of the property. If null the system client is
	 *                     used.
	 * @param context      An optional subkey
	 * @param defaultValue The value to return, if no property can be resolved
	 * @see SystemPropertyBusiness#getString(String, Client, String, String)
	 */
	public boolean getBoolean(String key, Client client, String context, boolean defaultValue) {
		String stringValue = getString(key, client, context, String.valueOf(defaultValue));

		stringValue = stringValue.toLowerCase();
		if ("1".equals(stringValue)) {
			return true;
		} else if ("true".equals(stringValue)) {
			return true;
		} else if ("yes".equals(stringValue)) {
			return true;
		}

		return false;
	}

	/**
	 * Read system property for the given key.
	 * 
	 * @param key          The property key
	 * @param defaultValue The value to return, if no property can be resolved
	 * @see SystemPropertyBusiness#getString(String, Client, String, String)
	 */
	public int getInt(String key, int defaultValue) {
		return getInt(key, null, null, defaultValue);
	}

	/**
	 * Read system property for the given key.
	 * 
	 * @param propertyKey  The property key
	 * @param client       The Client of the property. If null the system client is
	 *                     used.
	 * @param context      An optional subkey
	 * @param defaultValue The value to return, if no property can be resolved
	 * @see SystemPropertyBusiness#getString(String, Client, String, String)
	 */
	public int getInt(String propertyKey, Client client, String context, int defaultValue) {
		String stringValue = getString(propertyKey, client, context, String.valueOf(defaultValue));

		int intValue = defaultValue;
		try {
			if (stringValue != null) {
				intValue = Integer.parseInt(stringValue);
			}
		} catch (Throwable t) {
			intValue = defaultValue;
		}

		return intValue;
	}

	/**
	 * Read system property for the given key.
	 * 
	 * @param key          The property key
	 * @param defaultValue The value to return, if no property can be resolved
	 * @see SystemPropertyBusiness#getString(String, Client, String, String)
	 */
	public String getString(String key, String defaultValue) {
		return getString(key, null, null, defaultValue);
	}

	/**
	 * Read a String property
	 * <p>
	 * 
	 * Strategy to find the requested property:<br>
	 * - The key matches in every case.<br>
	 * - For a given client look first for a property with the given client.<br>
	 * If this does not exist look for a property without client<br>
	 * - For a given context look first for a property with the given context. <br>
	 * If this does not exist look for a property without context Client trumps
	 * context. So a property with matching client is better than a property with
	 * matching context. <br>
	 * 1. key + client + context<br>
	 * 2. key + client + empty context<br>
	 * 3. key + system client + context<br>
	 * 4. key + system client + empty context
	 *
	 * @param key          The property key
	 * @param client       The Client of the property. If null the system client is
	 *                     used.
	 * @param context      An optional subkey
	 * @param defaultValue The value to return, if no property can be resolved
	 */
	public String getString(String key, Client client, String context, String defaultValue) {
		String logStr = "getString ";

		String jpql = "SELECT entity.propertyValue, entity.client.id, entity.propertyContext FROM ";
		jpql += SystemProperty.class.getName() + " entity ";
		jpql += " WHERE entity.propertyKey=:propertyKey";

		Query query = manager.createQuery(jpql);

		query.setParameter("propertyKey", key);

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		if (StringUtils.isEmpty(context)) {
			context = null;
		}

		Long clientId = 0L;
		Long systemClientId = 0L;
		if (client != null) {
			clientId = client.getId();
		}

		for (Object[] result : results) {
			Long propertyClientId = (Long) result[1];
			String propertyContext = (String) result[2];
			if (StringUtils.isEmpty(propertyContext)) {
				propertyContext = null;
			}

			if (Objects.equals(propertyClientId, clientId) && StringUtils.equals(context, propertyContext)) {
				String value = (String) result[0];
				return (value == null ? null : value.trim());
			}
		}

		// Try without context
		if (context != null) {
			for (Object[] result : results) {
				Long propertyClientId = (Long) result[1];
				String propertyContext = (String) result[2];
				if (!StringUtils.isEmpty(propertyContext)) {
					continue;
				}

				if (Objects.equals(propertyClientId, clientId)) {
					String value = (String) result[0];
					return (value == null ? null : value.trim());
				}
			}
		}

		// Try without client
		if (clientId != null) {
			for (Object[] result : results) {
				Long propertyClientId = (Long) result[1];
				if (!Objects.equals(propertyClientId, systemClientId)) {
					continue;
				}
				String propertyContext = (String) result[2];
				if (StringUtils.isEmpty(propertyContext)) {
					propertyContext = null;
				}

				if (StringUtils.equals(context, propertyContext)) {
					String value = (String) result[0];
					return (value == null ? null : value.trim());
				}
			}
		}

		// Try without client & context
		if (context != null && clientId != null) {
			for (Object[] result : results) {
				Long propertyClientId = (Long) result[1];
				if (!Objects.equals(propertyClientId, systemClientId)) {
					continue;
				}
				String propertyContext = (String) result[2];
				if (!StringUtils.isEmpty(propertyContext)) {
					continue;
				}

				if (StringUtils.equals(context, propertyContext)) {
					String value = (String) result[0];
					return (value == null ? null : value.trim());
				}
			}
		}

		logger.log(Level.FINER, logStr + "SystemProperty not defined. Use default. key=" + key + ", client=" + client
				+ ", context=" + context + ", defaultValue=" + defaultValue);

		return (defaultValue == null ? null : defaultValue.trim());
	}

	/**
	 * Set the value of a system property
	 * 
	 * @param key   The property key
	 * @param value The value to set
	 */
	public void setValue(String key, String value) {
		setValue(key, null, null, value);
	}

	/**
	 * Set the value of a system property
	 * 
	 * @param key   The property key
	 * @param value The value to set
	 */
	public void setValue(String key, boolean value) {
		setValue(key, null, null, Boolean.toString(value));
	}

	/**
	 * Set the value of a system property
	 * 
	 * @param key   The property key
	 * @param value The value to set
	 */
	public void setValue(String key, int value) {
		setValue(key, null, null, Integer.toString(value));
	}

	/**
	 * Set the value of a system property
	 * 
	 * @param key     The property key
	 * @param client  The Client of the property. If null the system client is used.
	 * @param context An optional subkey
	 * @param value   The value to set
	 */
	public void setValue(String key, Client client, String context, String value) {
		if (client == null) {
			client = clientBusiness.getSystemClient();
		}

		SystemProperty prop = read(key, client, context);
		if (prop == null) {
			create(key, client, context, value);
			return;
		}

		if (!value.equals(prop.getPropertyValue())) {
			prop.setPropertyValue(value);
		}
	}

}
