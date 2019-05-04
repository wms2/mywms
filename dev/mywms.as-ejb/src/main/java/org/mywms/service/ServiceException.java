/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.service;

import java.util.Locale;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.res.BundleResolver;
import org.mywms.util.BundleHelper;

/**
 * This exception wraps the exception key. The key can be resolved to a
 * localized message.
 * 
 * @author Markus Jordan
 * @version $Revision$ provided by $Author$
 */
public class ServiceException  extends Exception
{

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_RESOURCE_BUNDLE_NAME = "/org/mywms/res/mywms-messages";

    private String bundleName = DEFAULT_RESOURCE_BUNDLE_NAME;
    private String key;
    private Object[] parameters;
    // Helpful if proeprties cannot be loaded because of separate Classloaders, e.e. netbeans
    @SuppressWarnings("unchecked")
	private Class bundleResolver;
    
    private ServiceExceptionKey messageKey;

    /**
     * Creates a new ServiceException instance.
     * 
     * @param messageKey the ressource key of the exception
     */
    public ServiceException(ServiceExceptionKey messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Returns the ressource key of the message of the exception.
     * 
     * @return the exception's ressource key
     */
    public ServiceExceptionKey getMessageKey() {
        return messageKey;
    }
    
    public ServiceException(String resourcekey)
    {
    	super(resourcekey);
    	this.key = resourcekey;
        this.bundleResolver = BundleResolver.class;
        this.messageKey = ServiceExceptionKey.GENERIC;
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
    public ServiceException(String msg, String resourcekey, Object[] parameters)
    {
        super(msg);
        this.key = resourcekey;
        this.parameters = parameters;
        this.messageKey = ServiceExceptionKey.GENERIC;
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
    public ServiceException(Throwable t, String resourcekey, Object[] parameters)
    {
        super(t);
        this.key = resourcekey;
        this.parameters = parameters;
        this.messageKey = ServiceExceptionKey.GENERIC;
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
    public ServiceException(
        String msg,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(msg);
        this.key = resourcekey;
        this.parameters = parameters;
        this.bundleName = bundleName;
        this.messageKey = ServiceExceptionKey.GENERIC;
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
    public ServiceException(
        Throwable t,
        String resourcekey,
        Object[] parameters,
        String bundleName)
    {
        super(t);
        this.key = resourcekey;
        this.parameters = parameters;
        this.bundleName = bundleName;
        this.messageKey = ServiceExceptionKey.GENERIC;
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
