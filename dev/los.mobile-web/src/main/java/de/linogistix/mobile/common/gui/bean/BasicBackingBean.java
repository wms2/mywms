/*
 * BasicBackingBean.java
 *
 * Created on 2. Mai 2007, 05:23
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.mobile.common.gui.bean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;
import org.mywms.ejb.BeanLocator;
import org.mywms.ejb.BeanLocatorException;
import org.mywms.model.Role;
import org.mywms.model.User;

import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.user.query.UserQueryRemote;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.mobile.processes.login.gui.bean.CenterBean;

/**
 * Basic utility operations for all BAcking Beans.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas
 *         Trautmann</a>
 */
public class BasicBackingBean {

	private static final Logger log = Logger.getLogger(BasicBackingBean.class.getName());

	//static JndiServiceResolver jndiServiceResolver;

	private UserQueryRemote userQuery;

	private Locale locale;

	/** Creates a new instance of BasicBackingBean */
	public BasicBackingBean() {

	}

	/**
	 * Resolves and adds a message to the FacesContext
	 * 
	 * @param clientId
	 *            if null, use global context. If empty use calling component
	 */
	public void message(String bundleKey, Object[] params, String clientId) {
		FacesMessage msg;
		String s;
		s = resolve(bundleKey, params);
		msg = new FacesMessage(s);
		if (clientId != null && clientId.length() == 0) {
			clientId = getUIViewRoot().getClientId(getContext());
		}
		getContext().addMessage(clientId, msg);
	}

	public void message(String message) {
		throw new ValidatorException(new FacesMessage(resolve(message, new Object[] {})));
	}

	

	/**
	 * Resolves a given ResourceBundle entry by key
	 */
	public String resolve(String key, Object[] parameters) {
		String ret;
		try {
			ResourceBundle bundle = getResourceBundle();
			ret = bundle.getString(key);
			if (parameters != null && parameters.length > 0) {
				ret = String.format(ret, parameters);
			}
			return ret;
		} catch (Throwable t) {
			return key;
		}
	}

	/**
	 * Resolves a given ResourceBundle entry by key
	 */
	public String resolve(String key) {
		Object[] parameters = new Object[] {};
		return resolve(key, parameters);
	}

	/**
	 * Resolves a given ResourceBundle entry by key and one parameter
	 */
	public String resolve(String key, String param) {
		Object[] parameters = new Object[] { param };
		return resolve(key, parameters);
	}

	/**
	 * @return current Locale of calling component
	 */
	public String getLocaleString() {

		if (this.locale == null) {
			return getLocale().toString();
		} else {
			return this.locale.toString();
		}

	}

	/**
	 * @return current Locale of calling component
	 */
	public Locale getLocale() {

		if (this.locale == null) {
			if (userQuery == null) {
				userQuery = getStateless(UserQueryRemote.class);
			}

			try {
				User user = userQuery.queryByIdentity(getPrincipalName());
				this.locale = BundleHelper.getLocale(user.getLocale());
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				this.locale = getUIViewRoot().getLocale();
			}
		}
		getUIViewRoot().setLocale(this.locale);
		return this.locale;

	}

	/**
	 * @return Calling component
	 */
	protected UIViewRoot getUIViewRoot() {
		UIViewRoot ret;
		ret = getContext().getViewRoot();
		return ret;
	}

	/**
	 * @return ResourceBundle for this Beans messages
	 */
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.res.Bundle", loc);
		return bundle;
	}

	/**
	 * @return FacesContext.getCurrentInstance()
	 */
	public FacesContext getContext() {
		FacesContext context;

		context = FacesContext.getCurrentInstance();
		return context;
	}

	protected void reset(String beanName) {
		// dgrys new code portierung jboss, TODO dgrys test it
		// FacesContext.getCurrentInstance().getApplication().createValueBinding(
		// "#{beanName}").setValue(FacesContext.getCurrentInstance(), null);

		ExpressionFactory elFactory = getContext().getApplication().getExpressionFactory();
		ELContext elContext = getContext().getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext, "#{beanName}", Object.class);
		valueExp.setValue(elContext, null);
	}


	@SuppressWarnings("unchecked")
	protected <T> T getStateless(Class<T> remoteInterface) {
		String logStr = "getStateless ";
		log.debug(logStr + remoteInterface);

		
		T t = null;
		try {
			t = getBeanLocator().getStateless(remoteInterface);
		} catch (BeanLocatorException e) {
			log.error(logStr + e.getMessage());
		}

		return t;
	}
	
	private BeanLocator beanLocator = null;
	protected BeanLocator getBeanLocator() {
		String logStr = "getBeanLocator ";
		log.debug(logStr);
		
		if( beanLocator!=null ) {
			return beanLocator;
		}
		
		
		Properties props = new Properties();

		try {
			InputStream is;
			String file = "META-INF/appserver.properties";
			
		    is = BasicBackingBean.class.getClassLoader().getResourceAsStream(file);
			if (is != null) {
				props.load(is);
			} else{
				throw new RuntimeException(logStr+"Cannot read " + file );
			}

			is.close();
		} catch (FileNotFoundException e) {
			log.error(logStr+e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(logStr+e.getLocalizedMessage());
		}

		try {
			beanLocator = new BeanLocator(props);
		} catch (BeanLocatorException e) {
			log.error(logStr + e.getMessage());
		}

		return beanLocator;

	}
	
	// TODO: Make LoginBean Available?
	protected CenterBean getLoginBean() {
		CenterBean b = (CenterBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("LoginBean");
		return b;
	}

	public String getPrincipalName() {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		return context.getUserPrincipal().getName();
	}

	public String[] getPrincipalRoles() {
		if (userQuery == null) {
			userQuery = getStateless(UserQueryRemote.class);
		}

		try {
			User user = userQuery.queryByIdentity(getPrincipalName());
			List<Role> roles = user.getRoles();
			String ret[] = new String[roles.size()];
			int i = 0;
			for (Role r : roles) {
				ret[i++] = r.getName();
			}
			return ret;

		} catch (BusinessObjectNotFoundException e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}

	/**
	 * Overwrite in extended classes to check Security Constraints
	 * 
	 * @return
	 */
	public boolean isRolesAllowed() {
		String[] allowed;
		String[] roles;

		allowed = getRolesAllowed();
		if (allowed == null || allowed.length == 0) {
			return true;
		}

		roles = getPrincipalRoles();

		if ((allowed == null || allowed.length == 0) && (roles == null || roles.length == 0)) {
			return true;
		}

		for (String allow : allowed) {
			for (String role : roles) {
				if (allow.equals(role)) {
					return true;
				}
			}
		}

		return false;

	}

	public String[] getRolesAllowed() {
		return new String[0];
	}

	public String getWorkstationName() {
		// TODO krane: Give an identifier to the mobile devices. Maybe a
		// parameter in the URL or an extended login dialog
		return null;
	}
}
