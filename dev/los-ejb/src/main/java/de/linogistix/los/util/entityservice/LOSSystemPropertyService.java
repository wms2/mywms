/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.entityservice;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.model.LOSSystemProperty;

/**
 * @author krane
 *
 */
@Local
public interface LOSSystemPropertyService extends BasicService<LOSSystemProperty> {
	
	/**
	 * Create a new SystemProperty for the given key.
	 * As client and workstation default values are used. 
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public LOSSystemProperty createSystemProperty(String key, String value);

	/**
	 * Create a new SystemProperty for the given key.
	 * 
	 * @param client If null the callers client is used
	 * @param workstation If null, the default workstation is used
	 * @param key
	 * @param value
	 * @param groupName
	 * @param description
	 * @param hidden
	 * @param reinitialize If false, an existing property will not be changed
	 * @return
	 */
	public LOSSystemProperty createSystemProperty(Client client, String workstation, String key, String value, String groupName, String description, boolean hidden, boolean reinitialize);

	
	
	
	/**
	 * @see #getByKey(Client, String, String)
	 */
	public LOSSystemProperty getByKey(String key);
	
	/**
	 * Read a SystemProperty for the given client, workstation and key.<br>
	 * In contrast to the other get... methods, the getByKey(...) methods do not try to
	 * find something with adapted client and workstation settings.
	 * 
	 * @param client If null, the callers client is used
	 * @param workstation If null, the default workstation is used
	 * @param key
	 * @return
	 */
	public LOSSystemProperty getByKey(Client client, String workstation, String key);
	
	
	
	/**
	 * @see #getStringDefault(Client, String, String, String)
	 */
	public String getString( String key );
	
	/**
	 * @see #getStringDefault(Client, String, String, String)
	 */
	public String getString( String workstation, String key );
	
	/**
	 * @see #getStringDefault(Client, String, String, String)
	 */
	public String getString( Client client, String workstation, String key );

	/**
	 * @see #getStringDefault(Client, String, String, String)
	 */
	public String getStringDefault( String key, String defaultValue );

	/**
	 * @see #getStringDefault(Client, String, String, String)
	 */
	public String getStringDefault( String workstation, String key, String defaultValue );
	
	/**
	 * Read a SystemProperty for the given client, workstation and key.<br>
	 * The default-value is "".<br>
	 * - If the requested property does not exist for the requested client, the system client is used.<br>
	 * - If that property does not exist for the requested workstation, the default workstation is used.<br>
	 * - If that property does not exist, it will be generated with the default value for system client and default workstation.
	 * 
	 * @param client If null, the callers client is used
	 * @param workstation If null, the default workstation is used
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getStringDefault( Client client, String workstation, String key, String defaultValue );

	
	
	
	/**
	 * @see #getBooleanDefault(Client, String, String, boolean)
	 */
	public boolean getBoolean( String key );

	/**
	 * @see #getBooleanDefault(Client, String, String, boolean)
	 */
	public boolean getBoolean( String workstation, String key );
	
	/**
	 * @see #getBooleanDefault(Client, String, String, boolean)
	 */
	public boolean getBoolean( Client client, String workstation, String key );
	
	/**
	 * @see #getBooleanDefault(Client, String, String, boolean)
	 */
	public boolean getBooleanDefault( String key, boolean defaultValue );

	/**
	 * @see #getBooleanDefault(Client, String, String, boolean)
	 */
	public boolean getBooleanDefault( String workstation, String key, boolean defaultValue );

	/**
	 * Read a SystemProperty for the given client, workstation and key.<br>
	 * The default-value is FALSE.
	 * The value TRUE will be returned for property-values of '1', 'true' and 'yes'. Everything else is FALSE.<br>
	 * - If the requested property does not exist for the requested client, the system client is used.<br>
	 * - If that property does not exist for the requested workstation, the default workstation is used.<br>
	 * - If that property does not exist, it will be generated with the default value for system client and default workstation.
	 * 
	 * @param client If null, the callers client is used
	 * @param workstation If null, the default workstation is used
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanDefault( Client client, String workstation, String key, boolean defaultValue );

	
	/**
	 * @see #getLongDefault(Client, String, String, long)
	 */
	public long getLong( String key );

	/**
	 * @see #getLongDefault(Client, String, String, long)
	 */
	public long getLong( String workstation, String key );
	
	/**
	 * @see #getBLongDefault(Client, String, String, long)
	 */
	public long getLong( Client client, String workstation, String key );
	
	/**
	 * @see #getLongDefault(Client, String, String, long)
	 */
	public long getLongDefault( String key, long defaultValue );

	/**
	 * @see #getLongDefault(Client, String, String, long)
	 */
	public long getLongDefault( String workstation, String key, long defaultValue );

	/**
	 * Read a SystemProperty for the given client, workstation and key.<br>
	 * The default-value is 0.<br>
	 * - If the requested property does not exist for the requested client, the system client is used.<br>
	 * - If that property does not exist for the requested workstation, the default workstation is used.<br>
	 * - If that property does not exist, it will be generated with the default value for system client and default workstation.
	 * 
	 * @param client If null, the callers client is used
	 * @param workstation If null, the default workstation is used
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLongDefault( Client client, String workstation, String key, long defaultValue );
	

	
	
	
	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( String key, String value ) throws UnAuthorizedException;

	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( String workstation, String key, String value ) throws UnAuthorizedException;

	/**
	 * Writing a system-property.
	 * If the requested property does not exists, it will be generated.
	 * 
	 * @param client If null, the callers client is used
	 * @param workstation If null, the default workstation is used
	 * @param key
	 * @param value
	 */
	public void setValue(Client client, String workstation, String key, String value) throws UnAuthorizedException;

	
	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( String key, boolean value ) throws UnAuthorizedException;
	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( String workstation, String key, boolean value ) throws UnAuthorizedException;
	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( Client client, String workstation, String key, boolean value ) throws UnAuthorizedException;

	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( String key, long value ) throws UnAuthorizedException;
	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( String workstation, String key, long value ) throws UnAuthorizedException;
	/**
	 * @see #setValue(Client, String, String, String)
	 */
	public void setValue( Client client, String workstation, String key, long value ) throws UnAuthorizedException;
}
