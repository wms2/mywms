/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author artur
 */
public class BundleHelper {

    Locale defaultLocale = Locale.getDefault();
    ResourceBundle bundle;

    public BundleHelper(String bundleName) {
        this.bundle = ResourceBundle.getBundle(bundleName, defaultLocale);

    }
    
    public BundleHelper(String bundleName, Class bundleResolver) {
        ClassLoader loader = bundleResolver.getClassLoader(); 
        this.bundle = ResourceBundle.getBundle(bundleName, defaultLocale, loader);
    }

    public String resolve2(
            String key,
            Object[] parameters) {

        String formatString = new String();
        try {
            // check for bundle

            // resolving key
            formatString = bundle.getString(key);
        } catch (MissingResourceException ex) {
            return key;
        }

        // format the String
        String retVal = String.format(formatString, parameters);
        return retVal;
    }
}
