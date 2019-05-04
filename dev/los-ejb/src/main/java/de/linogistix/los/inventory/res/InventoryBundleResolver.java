/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.res;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 *
 * @author trautm
 */
public class InventoryBundleResolver {

	private static final Logger log = Logger.getLogger(InventoryBundleResolver.class);
	
	@SuppressWarnings("unchecked")
	public static final String resolve(Class resolver, String key, Locale locale){
		return resolve(resolver, "de.linogistix.los.inventory.res.Bundle", key, new String[0], locale);
	}
	
	@SuppressWarnings("unchecked")
	public static final String resolve(Class resolver, String bundleName, String key, Object[] params, Locale locale){
		
        // assertion
        if (key == null) {
            return key;
        }
        
        if (resolver == null) throw new NullPointerException("resolver must not be null");

        ResourceBundle bundle;
        String formatString;
        
        try {
 
            bundle = ResourceBundle.getBundle(bundleName, locale, resolver.getClassLoader());
            
            // resolving key
            String s = bundle.getString(key);
            formatString = String.format(s, params);
            return formatString;
        }
        catch (MissingResourceException ex) {
            log.error(ex.getMessage(), ex);
            return key;
        }
	}
	
	/**
	 * Parses and returns Locale by given String localString like <code>en_EN</code> or <code>de_DE</code>.
	 * 
	 * @return Locale
	 */
	public static Locale getLocale(String localString) throws IllegalArgumentException{
		Locale ret;
		
		String[] locstr = localString.split("_");
		switch(locstr.length){
			case 1: ret = new Locale(locstr[0]); break;
			case 2: ret = new Locale(locstr[0], locstr[1]); break;
			case 3: ret = new Locale(locstr[0], locstr[1], locstr[2]); break;
			default: throw new IllegalArgumentException("Could not parse locale from string: " + localString);
		}

		return ret;
		
	}
}
