/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.ejb;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

/**
 * The locator creates remote instances of session beans, served by the
 * application server.
 * 
 * @version $Revision: 768 $ provided by $Author: mkrane $
 */
public class BeanLocator implements Externalizable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(BeanLocator.class.getName());

	private static final String JNDI_NAME_USER_TRANSACTION = "java:comp/UserTransaction";
	private static final String JNDI_NAME_CONNECTION_FACTORY = "ConnectionFactory";

	private transient InitialContext initialContext;
	private transient Map<String, Object> statelessCache = new HashMap<String, Object>();
	private transient QueueConnectionFactory connectionFactory;

	private Properties initialContextProperties = new Properties();

	private Properties appServerProperties = new Properties();

	private String applicationName;

	private Map<String, String> mappingHash = null;

	/**
	 * Creates a new instance of BeanLocator.
	 */
	public BeanLocator() {
		this(null);
	}

	/**
	 * Creates a new instance of BeanLocator.
	 * 
	 * @param user
	 *            the user account, used to connect to the application server
	 * @param passwd
	 *            the password of the user account
	 */
	public BeanLocator(String user, String passwd) {
		if (initialContextProperties == null) {
			initialContextProperties = new Properties();
		}
		if (user != null) {
			initialContextProperties.put(Context.SECURITY_PRINCIPAL, user);
			if (passwd != null) {
				initialContextProperties.put(Context.SECURITY_CREDENTIALS, passwd);
			}
		}
		initJNDIContext();
	}

	/**
	 * Creates a new instance of BeanLocator.
	 * 
	 * @param user
	 *            the user account, used to connect to the application server
	 * @param passwd
	 *            the password of the user account
	 */
	public BeanLocator(String user, String passwd, Properties jndiProps, Properties appServerProps) {

		if (jndiProps != null) {
			initialContextProperties = jndiProps;
		}

		if (appServerProps != null) {
			appServerProperties = appServerProps;
		}

		mappingHash = propertiesToMapForModules(appServerProperties);

		applicationName = appServerProperties.getProperty("org.mywms.env.applicationName");

		String defaultUser = initialContextProperties.getProperty("org.mywms.env.defaultUser");
		String defaultPassword = initialContextProperties.getProperty("org.mywms.env.defaultPassword");

		// with default user
		if (defaultUser != null && defaultPassword != null && !defaultUser.isEmpty() && !defaultPassword.isEmpty()) {
			authentification(defaultUser, defaultPassword);
		}
		// with login data
		else {
			if (user != null && passwd != null) {
				authentification(user, passwd);
			} else {
				logger.error("Authentification failed, username or password is null");
			}
		}
		initJNDIContext();
	}

	/**
	 * Creates a new instance of BeanLocator.
	 * 
	 * @param initialContextProperties
	 *            the properties to use
	 */
	public BeanLocator(Properties appServerProps) {
		if (appServerProps != null) {
			appServerProperties = appServerProps;
		}

		applicationName = this.appServerProperties.getProperty("org.mywms.env.applicationName");

		mappingHash = propertiesToMapForModules(appServerProperties);
	}

	private static Map<String, String> propertiesToMapForModules(Properties props) {
		final String SEARCH_KEY = "org.mywms.env.mapping.";
		HashMap<String, String> hm = new HashMap<String, String>();
		Enumeration<Object> e = props.keys();
		while (e.hasMoreElements()) {
			String s = (String) e.nextElement();
			if (s.startsWith(SEARCH_KEY)) {

				String[] packages = props.getProperty(s).split(",");

				for (String p : packages) {
					hm.put(p, s.replaceFirst(SEARCH_KEY, ""));
				}
			}
		}
		return hm;
	}

	private void authentification(String username, String password) {

		String value = initialContextProperties.getProperty(Context.INITIAL_CONTEXT_FACTORY);
		if (value == null || value.isEmpty()) {
			initialContextProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		}

		value = initialContextProperties.getProperty(Context.PROVIDER_URL);
		if (value == null || value.isEmpty()) {
			initialContextProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		}

		initialContextProperties.put("java.naming.security.principal", username);
		initialContextProperties.put("java.naming.security.credentials", password);
	}

	/**
	 * Returns the initial context.
	 * 
	 * @return the initial context
	 * @throws BeanLocatorException
	 */

	private Context getInitialContext() throws BeanLocatorException {
		if (initialContext == null) {
			try {
				initialContext = new InitialContext(initialContextProperties);
			} catch (NamingException ne) {
				throw new BeanLocatorException(ne);
			} catch (Exception e) {
				throw new BeanLocatorException(e);
			}
		}
		return initialContext;
	}

	private void initJNDIContext() {
		if (initialContextProperties != null) {
			initialContextProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			initialContextProperties.put("jboss.naming.client.ejb.context", "true");
		}
	}

	public <T> T getStateless(Class<T> interfaceClazz) {
		String logStr = "getStateless ";

		if (applicationName == null) {
			logger.error(logStr + "application name not found");
			return null;
		}

		String interfacePackage = interfaceClazz.getPackage().getName();
		String interfaceName = interfaceClazz.getName();

		String moduleName = null;
		// Try to resolve complete interface name
		for (Iterator<String> it = mappingHash.keySet().iterator(); it.hasNext();) {
			String s = it.next();
			if (interfaceName.equals(s)) {
				moduleName = mappingHash.get(s);
				break;
			}
		}
		// Try to resolve interface package name
		if (moduleName == null) {
			for (Iterator<String> it = mappingHash.keySet().iterator(); it.hasNext();) {
				String s = it.next();
				if (interfacePackage.contains(s)) {
					moduleName = mappingHash.get(s);
					break;
				}
			}
		}
		if (moduleName == null) {
			logger.error(logStr + "no module found for interface. package=" + interfacePackage + ", name=" + interfaceName);
			return null;
		}

		String lookUpString = resolve(interfaceClazz, applicationName, moduleName);
		return getStateless(interfaceClazz, moduleName, lookUpString);

	}

	@SuppressWarnings("unchecked")
	public <T> T getStateless(Class<T> interfaceClazz, String moduleName, String lookUpString) throws BeanLocatorException {
		String logStr = "getStateless ";

		if (moduleName == null || moduleName.equals(null)) {
			logger.error(logStr + "No moduleName defined!");
			return null;
		}

		try {
			final Context ctx = getInitialContext();
			Object result = ctx.lookup(lookUpString);

			return (T) result;

		} catch (NamingException ne) {
			logger.error(logStr + "Error when trying lookup: " + ne.getLocalizedMessage());
			throw new BeanLocatorException(ne);
		}
	}

	public <T> T getStateful(Class<T> interfaceClazz) {
		return getStateful(interfaceClazz, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getStateful(Class<T> interfaceClazz, String jndiName) throws BeanLocatorException {
		if (jndiName == null) {
			jndiName = interfaceClazz.getName();
		}
		try {
			T result = (T) getInitialContext().lookup(jndiName);
			return result;
		} catch (NamingException ne) {
			throw new BeanLocatorException(ne);
		}
	}

	public UserTransaction getUserTransaction() throws BeanLocatorException {
		try {
			UserTransaction ut = (UserTransaction) getInitialContext().lookup(JNDI_NAME_USER_TRANSACTION);
			return ut;
		} catch (NamingException e) {
			throw new BeanLocatorException(JNDI_NAME_USER_TRANSACTION + " konnte nicht erzeugt werden", e);
		}
	}

	public Queue getQueue(String queuename) throws BeanLocatorException {
		try {
			return (Queue) getInitialContext().lookup(queuename);
		} catch (NamingException e) {
			throw new BeanLocatorException(e);
		}
	}

	public Topic getTopic(String topicname) throws BeanLocatorException {
		try {
			return (Topic) getInitialContext().lookup(topicname);
		} catch (NamingException e) {
			throw new BeanLocatorException(e);
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		statelessCache = new HashMap<String, Object>();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	}

	private String resolve(Class<?> interfaceClazz, String applicationName, String moduleName) {
		String logStr = "resolve ";
		String beanName;
		String jndiName;
		beanName = interfaceClazz.getSimpleName().replaceFirst("Remote", "");
		jndiName = "ejb:" + applicationName + "/" + moduleName + "/" + beanName + "Bean!" + interfaceClazz.getName();
		logger.debug(logStr + "jndiName: " + jndiName);
		return jndiName;
	}

}