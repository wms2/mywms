/*
 * BusinessExceptionResolver.java
 *
 * Created on 6. September 2006, 11:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.util;

import de.linogistix.common.res.CommonBundleResolver;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BusinessExceptionResolver {

    private static final Logger log = Logger.getLogger(BusinessExceptionResolver.class.getName());

    /**
     * This method resolves the key from BusinessExcpetion using NbBundle.
     * The resolved String may contain additional formatter information.
     * Sample:
     * If the resolved String is <code>"%2$s %1$s"<code>
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
    public static final String resolve(FacadeException ex, Class bundleResolver) {

        String formatString;
        String retVal;
        // assertion

        if (ex.getBundleName() != null && ex.getKey() != null){  
            //first: build-in resolvement
            retVal = ex.getLocalizedMessage();
            if (ex.getMessage().equals(retVal)){
                //go on
            } else{
                if (ex.getCause() != null && ex.getCause() instanceof FacadeException){
                    retVal = retVal + BusinessExceptionResolver.causeText((FacadeException) ex.getCause(), bundleResolver);
                }
                return retVal;
            }
        } 
        
        if (ex.getBundleResolver() != null && ex.getKey() != null){
            //second: NbBundleApproach
            System.out.println("Try Resolver: " + ex.getBundleResolver().getCanonicalName());
            try {
                retVal = NbBundle.getMessage(ex.getBundleResolver(), ex.getKey());
            }
            catch( MissingResourceException e ) {
                log.warning("Can't resolve key: " + ex.getKey() + " ->" + e.getMessage());
                return keyText(ex);
            }
            retVal = String.format(retVal, ex.getParameters());
            if (ex.getCause() != null && ex.getCause() instanceof FacadeException){
                retVal = retVal + BusinessExceptionResolver.causeText((FacadeException) ex.getCause(), bundleResolver);
            }
            return retVal;
        } 
            
        if (ex.getKey() == null) {
            return ex.getMessage();
        }

        try {
            Class[] bundleResolvers;
            //Fallback
            if (bundleResolver == null) {
                bundleResolver = CommonBundleResolver.class;
                bundleResolvers = new Class[]{bundleResolver};
            } else{
                bundleResolvers = new Class[]{bundleResolver,CommonBundleResolver.class };
            }
            formatString = BundleResolve.resolve(bundleResolvers, ex.getKey(), null); 
            // format the String
            retVal = String.format(formatString, ex.getParameters());
            if (ex.getCause() != null && ex.getCause() instanceof FacadeException){
                retVal = retVal + BusinessExceptionResolver.causeText((FacadeException) ex.getCause(), bundleResolver);
            }
            return retVal;
        } catch (Throwable t) {
            log.warning("can't resolve key " + ex.getKey() + " ->" + t.getMessage());

            return keyText(ex);
        }
    }
    
    private static String causeText(FacadeException cause, Class bundleResolver){
        String ret = ""; 
        if (cause != null){
            ret = "\n" + "(" + resolve((FacadeException) cause, bundleResolver) + ")";
        }
        return ret;
    }
    private static String keyText(FacadeException ex) {
        String retVal;
        retVal = ex.getKey() + ":\n";

        // OK - fallback to original message
        if (ex.getMessage() == null || ex.getMessage().trim().equals("")) {
            retVal = retVal + ex.toString();
        } else {
            retVal = retVal + ex.getMessage();
        }
        return retVal;
        
    }
}
