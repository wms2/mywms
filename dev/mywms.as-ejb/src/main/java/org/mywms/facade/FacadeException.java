/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.util.Locale;

import javax.ejb.ApplicationException;

import org.mywms.service.ServiceException;
import org.mywms.util.BundleHelper;

/**
 * FacadeException is the base class of exceptions thrown by the myWMS
 * facades. The basic feature of the FacadeException is localization and
 * internationalization. Messages can be supplied as a ressource key and
 * an object array, containing values which are formatted into the
 * translated resource. Messages of this exception are tried to be
 * resolved against the file
 * <code>de/mywms/facade/FacadeExceptionMessages.properties</code> or
 * its local apropriates. A sample for a key resolved: If the resolved
 * String is <code>"%2$s %1$s"<code>
 * and the parameters are <code>{"a", "b"}</code>, the String returned by 
 * <code>resolve</code> is <code>"a b"</code>.
 *
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@SuppressWarnings("serial")
@ApplicationException(rollback = true)
public class FacadeException	
    extends java.lang.Exception
{
    public static final String DEFAULT_RESOURCE_BUNDLE_NAME =
        "/org/mywms/res/mywms-messages";

    private String bundleName = DEFAULT_RESOURCE_BUNDLE_NAME;
    private String key;
    private Object[] parameters;
    // Helpful if proeprties cannot be loaded because of separate Classloaders, e.e. netbeans
    @SuppressWarnings("unchecked")
	private Class bundleResolver;
    
    
    public FacadeException(ServiceException serviceException){
        super(serviceException);
        this.key = serviceException.getKey();
        this.parameters = serviceException.getParameters();
        this.bundleName = serviceException.getBundleName();
        setBundleResolver(serviceException.getBundleResolver());
    }
    
    /**
     * Constructs an instance of <code>FacadeException</code> with the
     * specified detail message.
     * 
     * @param msg the detail message.
     * @param resourcekey the key to load the translated resource
     * @param parameters the array of objects, which are formatted into
     *            the translated message, resolved from the resource
     *            key; the array can be null, if no parameters are used
     */
    public FacadeException(String msg, String resourcekey, Object[] parameters)
    {
        super(msg);
        this.key = resourcekey;
        this.parameters = parameters;
    }

    /**
     * Constructs an instance of <code>FacadeException</code> with the
     * specified original exception.
     * 
     * @param t the original exception.
     * @param resourcekey the key to load the translated resource
     * @param parameters the array of objects, which are formatted into
     *            the translated message, resolved from the resource
     *            key; the array can be null, if no parameters are used
     */
    public FacadeException(Throwable t, String resourcekey, Object[] parameters)
    {
        super(t);
        this.key = resourcekey;
        this.parameters = parameters;
    }

    /**
     * Constructs an instance of <code>FacadeException</code> with the
     * specified detail message.
     * 
     * @param msg the detail message.
     * @param resourcekey the key to load the translated resource
     * @param parameters the array of objects, which are formatted into
     *            the translated message, resolved from the resource
     *            key; the array can be null, if no parameters are used
     * @param bundleName
     */
    public FacadeException(
        String msg,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(msg);
        this.key = resourcekey;
        this.parameters = parameters;
        this.bundleName = bundleName;
    }

    /**
     * Constructs an instance of <code>FacadeException</code> with the
     * specified original exception.
     * 
     * @param t the original exception.
     * @param resourcekey the key to load the translated resource
     * @param parameters the array of objects, which are formatted into
     *            the translated message, resolved from the resource
     *            key; the array can be null, if no parameters are used
     * @param bundleName
     */
    public FacadeException(
        Throwable t,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(t);
        this.key = resourcekey;
        this.parameters = parameters;
        this.bundleName = bundleName;
    }

    public FacadeException(String message, String key, Object[] parameters,	Class bundleResolver) {
		this(message, key, parameters);
		setBundleResolver(bundleResolver);
	}

	/**
     * This method resolves the key from a predefined ResourceBundle.
     * The resolved String may contain additional formatter information.
     * Sample: If the resolved String is <code>"%2$s %1$s"<code>
     * and the parameters are <code>{"a", "b"}</code>, the String returned by 
     * <code>resolve</code> is <code>"a b"</code>.
     *
     * If the resource bundle file cannot be found, the key is null or the key 
     * cannot be resolved message is returned directly.
     *
     * @param key the key to be resolved
     * @param message the message returned, if the key cannot be resolved
     * @param parameters the parameters to be formatted into the resolved key 
     *          can be null
     * @return the resolved String
     */
    protected String resolve(String message, String key, Object[] parameters) {
        return resolve(message, key, parameters, Locale.getDefault());
    }

    /**
     * This method resolves the key from a predefined ResourceBundle.
     * The resolved String may contain additional formatter information.
     * Sample: If the resolved String is <code>"%2$s %1$s"<code>
     * and the parameters are <code>{"a", "b"}</code>, the String returned by 
     * <code>resolve</code> is <code>"a b"</code>.
     *
     * If the resource bundle file cannot be found, the key is null or the key 
     * cannot be resolved message is returned directly.
     *
     * @param key the key to be resolved
     * @param message the message returned, if the key cannot be resolved
     * @param parameters the parameters to be formatted into the resolved key 
     *          can be null
     * @param locale the locale to use for i18n
     * @return the resolved String
     */
    protected String resolve(
        String message,
        String key,
        Object[] parameters,
        Locale locale)
    {
       return BundleHelper.resolve(message, key, parameters, getBundleName(), getBundleResolver(), locale);
    }

    /**
     * Creates a localized description of this throwable.
     * 
     * @see #resolve(String,String,Object[])
     * @return The localized description of this throwable.
     */
    public String getLocalizedMessage() {
        return resolve(getMessage(), key, parameters);
    }
    
    /**
     * Creates a description of this Exception.
     * 
     * @return The description of this throwable.
     */
    public String getMessage() {
    	String msg = super.getMessage();
    	if( msg == null || msg.length() == 0 ) {
    		msg = key+", "+resolve("", key, parameters);
    	}
    	return msg;
    }

    /**
     * Creates a localized description of this throwable.
     * 
     * @see #resolve(String,String,Object[])
     * @return The localized description of this throwable.
     */
    public String getLocalizedMessage(Locale locale) {
        return resolve(getMessage(), key, parameters, locale);
    }

    public String getKey() {
        return key;
    }

    public Object[] getParameters() {
        return parameters;
    }
    
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return bundleName;
    }

    /**
     * Set to a class (normally within the package that contains the properties file and often an empty class called BundleResolver)
     * which can be used for loading the property file
     * @param bundleResolver
     */
    @SuppressWarnings("unchecked")
	public void setBundleResolver(Class bundleResolver){
        this.bundleResolver = bundleResolver;
    }
    
    /**
      *Get the class (normally within the package that contains the properties file and often an empty class called BundleResolver)
     * which can be used for loading the property file
     * @return
     */
    @SuppressWarnings("unchecked")
	public Class getBundleResolver(){
        return this.bundleResolver;
    }
}
