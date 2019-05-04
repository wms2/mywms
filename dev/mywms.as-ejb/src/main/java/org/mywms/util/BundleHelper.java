package org.mywms.util;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class BundleHelper {

	public static final String resolve(
	        String message,
	        String key,
	        Object[] parameters,
	        String bundleName,
	        Class bundleResolver,
	        Locale locale)
	    {
	        // assertion
	        if (key == null) {
	            return message;
	        }

	        ResourceBundle bundle;
	        String formatString;
	        try {
	            // check for bundle
	            if (bundleResolver == null){
	                bundle = ResourceBundle.getBundle(bundleName, locale);
	            } else{
	                bundle = ResourceBundle.getBundle(bundleName, locale,bundleResolver.getClassLoader());
	            }
	            // resolving key
	            String s = bundle.getString(key);
	            formatString = String.format(s, parameters);
	            return formatString;
	        }
	        catch (MissingResourceException ex) {
	            return message;
	        }
	        catch (IllegalFormatException ife){
	        	return "--- CONVERSION EXCEPTION >>>>> "+key;
	        }
	    }
}
