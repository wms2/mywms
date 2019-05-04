/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.util;

import java.util.logging.Logger;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class BundleResolve {
  
  private static final Logger log = Logger.getLogger(BundleResolve.class.getName());
  
  /**
   *@param keyIfUnresolved if <code>true</code>, returns the key if it can not be resolved
   */
  public static String resolve(Class[] bundleResolvers, String key, Object[] params, boolean keyIfUnresolved){
    String ret;
    
    ret = keyIfUnresolved ?  key : "";
    
    for (Class c : bundleResolvers) {
      try{
        ret = NbBundle.getMessage(c, key, params);
        if (ret != null) {
          break;
        }
      } catch (Throwable th){
        continue;
      }
    }
    return ret;
  }
   
  public static String resolve(Class[] bundleResolvers, String key, Object[] params){
    return BundleResolve.resolve(bundleResolvers,key,params,true);
  } 
    
  }
