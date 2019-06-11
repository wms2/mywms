/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.entityservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.property.SystemProperty;
import de.wms2.mywms.property.SystemPropertyBusiness;

/**
 * @author krane
 *
 */
@Stateless
public class LOSSystemPropertyServiceBean extends BasicServiceBean<SystemProperty>
		implements LOSSystemPropertyService, LOSSystemPropertyServiceRemote {
	static final Logger log = Logger.getLogger(LOSSystemPropertyServiceBean.class);
	private final static String WORKSTATION_DEFAULT = "DEFAULT";

	@EJB
	private ContextService ctxService;
	@EJB
	private QueryClientService clientService;
	@Inject
	private SystemPropertyBusiness propertyBusiness;

	public SystemProperty createSystemProperty(String key, String value) {
		return createSystemProperty(null, null, key, value, null, null, false);
	}

	public SystemProperty createSystemProperty(Client client, String workstation, String key, String value,
			String groupName, String description, boolean reinitialize) {

		if (client == null) {
			client = ctxService.getCallersClient();
		}
		if (value == null) {
			value = "";
		}

		if (reinitialize) {
			SystemProperty sysProp = propertyBusiness.createOrUpdate(key, client, workstation, value, groupName,
					description);
			return sysProp;
		}

		SystemProperty sysProp = propertyBusiness.read(key, client, workstation);
		if (sysProp == null) {
			sysProp = propertyBusiness.createOrUpdate(key, client, workstation, value, groupName, description);
		}
		sysProp.setPropertyGroup(groupName);
		sysProp.setDescription(description);

		return sysProp;
	}

	public SystemProperty getByKey(String key) {
		SystemProperty property = propertyBusiness.read(key, null, null);
		if(property==null) {
			property = propertyBusiness.read(key, null, WORKSTATION_DEFAULT);
		}
		return property;
	}

	public SystemProperty getByKey(Client client, String workstation, String key) {
		if (client == null) {
			client = ctxService.getCallersClient();
		}

		SystemProperty property = propertyBusiness.read(key, client, workstation);
		if(property==null && StringUtils.isEmpty(workstation)) {
			property = propertyBusiness.read(key, client, WORKSTATION_DEFAULT);
		}

		return property;
	}

	public boolean getBoolean(String key) {
		return getBooleanDefault(null, null, key, false);
	}

	public boolean getBoolean(String workstation, String key) {
		return getBooleanDefault(null, workstation, key, false);
	}

	public boolean getBoolean(Client client, String workstation, String key) {
		return getBooleanDefault(client, workstation, key, false);
	}

	public boolean getBooleanDefault(String key, boolean defaultValue) {
		return getBooleanDefault(null, null, key, defaultValue);
	}

	public boolean getBooleanDefault(String workstation, String key, boolean defaultValue) {
		return getBooleanDefault(null, workstation, key, defaultValue);
	}

	public boolean getBooleanDefault(Client client, String workstation, String key, boolean defaultValue) {
		String valueS = getStringDefault(client, workstation, key, String.valueOf(defaultValue));
		if (valueS == null) {
			valueS = "";
		}
		valueS = valueS.toLowerCase();
		if ("1".equals(valueS)) {
			return true;
		} else if ("true".equals(valueS)) {
			return true;
		} else if ("yes".equals(valueS)) {
			return true;
		}

		return false;
	}

	public long getLong(String key) {
		return getLongDefault(null, null, key, 0);
	}

	public long getLong(String workstation, String key) {
		return getLongDefault(null, workstation, key, 0);
	}

	public long getLong(Client client, String workstation, String key) {
		return getLongDefault(client, workstation, key, 0);
	}

	public long getLongDefault(String key, long defaultValue) {
		return getLongDefault(null, null, key, defaultValue);
	}

	public long getLongDefault(String workstation, String key, long defaultValue) {
		return getLongDefault(null, workstation, key, defaultValue);
	}

	public long getLongDefault(Client client, String terminal, String key, long defaultValue) {
		String valueS = getStringDefault(client, terminal, key, String.valueOf(defaultValue));
		if (valueS == null) {
			valueS = "";
		}

		long valueL = defaultValue;
		try {
			valueL = Long.valueOf(valueS);
		} catch (NumberFormatException e) {
			valueL = defaultValue;
		}

		return valueL;
	}

	public String getString(String key) {
		return getStringDefault(null, null, key, null);
	}

	public String getString(String workstation, String key) {
		return getStringDefault(null, workstation, key, null);
	}

	public String getString(Client client, String workstation, String key) {
		return getStringDefault(client, workstation, key, null);
	}

	public String getStringDefault(String key, String defaultValue) {
		return getStringDefault(null, WORKSTATION_DEFAULT, key, defaultValue);
	}

	public String getStringDefault(String workstation, String key, String defaultValue) {
		return getStringDefault(null, workstation, key, defaultValue);
	}

	public String getStringDefault(Client client, String workstation, String key, String defaultValue) {
		if (client == null) {
			client = clientService.getSystemClient();
		}

		String value = propertyBusiness.getString(key, client, workstation, defaultValue);

		if (StringUtils.equals(value, defaultValue)) {
			// propertyBusiness does not automatically generate properties
			String checkDefaultValue = "CHECK_NOT_EXISTING";
			if (checkDefaultValue.equals(defaultValue)) {
				checkDefaultValue = "CHECK_NOT_EXISTING2";
			}
			String value2 = propertyBusiness.getString(key, client, workstation, checkDefaultValue);
			if (checkDefaultValue.equals(value2)) {
				// Property does not exist
				if( !StringUtils.equals(workstation, WORKSTATION_DEFAULT)) {
					// Property does not exist
					return getStringDefault(client, WORKSTATION_DEFAULT, key, defaultValue);
				}
				
				propertyBusiness.create(key, clientService.getSystemClient(), null,
						defaultValue);
				value = defaultValue;
			}
		}

		return value == null ? null : value.trim();
	}

	public void setValue(String key, String value) throws UnAuthorizedException {
		setValue(null, null, key, value);
	}

	public void setValue(String workstation, String key, String value) throws UnAuthorizedException {
		setValue(null, workstation, key, value);
	}

	public void setValue(Client client, String workstation, String key, String value) throws UnAuthorizedException {
		if (client == null) {
			client = ctxService.getCallersClient();
		}
		if (value == null) {
			value = "";
		}

		SystemProperty prop = getByKey(client, workstation, key);

		if (prop == null) {
			propertyBusiness.create(key, client, workstation, value);
			return;
		}

		Client callersClient = ctxService.getCallersClient();
		if (callersClient != null && !callersClient.isSystemClient() && client.isSystemClient()) {
			throw new UnAuthorizedException();
		}
		if (!StringUtils.equals(value, prop.getPropertyValue())) {
			prop.setPropertyValue(value);
		}
	}

	public void setValue(String key, boolean value) throws UnAuthorizedException {
		setValue(null, null, key, Boolean.toString(value));
	}

	public void setValue(String terminal, String key, boolean value) throws UnAuthorizedException {
		setValue(null, terminal, key, Boolean.toString(value));
	}

	public void setValue(Client client, String terminal, String key, boolean value) throws UnAuthorizedException {
		setValue(client, terminal, key, Boolean.toString(value));
	}

	public void setValue(String key, long value) throws UnAuthorizedException {
		setValue(null, null, key, Long.toString(value));
	}

	public void setValue(String terminal, String key, long value) throws UnAuthorizedException {
		setValue(null, terminal, key, Long.toString(value));
	}

	public void setValue(Client client, String terminal, String key, long value) throws UnAuthorizedException {
		setValue(client, terminal, key, Long.toString(value));
	}

}
