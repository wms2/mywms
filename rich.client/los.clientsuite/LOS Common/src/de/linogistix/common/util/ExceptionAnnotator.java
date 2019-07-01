/*
 * ExceptionAnnotator.java
 *
 * Created on 18. September 2006, 06:06
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.util;

import de.linogistix.common.exception.InternalErrorException;
import de.linogistix.common.preferences.AppPreferences;
import de.linogistix.common.res.CommonBundleResolver;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import javax.ejb.EJBAccessException;
import javax.persistence.OptimisticLockException;
import org.hibernate.StaleObjectStateException;
import org.mywms.facade.FacadeException;
import org.mywms.service.ServiceException;
import org.openide.ErrorManager;

/**
 * Annotates Exceptions to ErrorManager so that they can be presented to the user.
 *
 * Knows about {@link FacadeException},  {@link BusinessException}, {@link ServiceException}
 * and {@link EJBAccessException}.
 *
 * @see ErrorManager
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public final class ExceptionAnnotator {
  
  final static Logger log = Logger.getLogger(ExceptionAnnotator.class.getName());
  
  private static Properties businessExProps;
  
  static{
    businessExProps  = AppPreferences.loadFromClasspath("de/linogistix/common/res/Bundle.properties");
  }
  
  public static String resolveMessage(Throwable t, Class bundleResolver){
    ErrorManager em = ErrorManager.getDefault();
    
    FacadeException bex = null;
    
    try{
      bex = guessExcpetion(t);
    } finally{
      log.log(Level.INFO,t.getMessage(),t);
      return BusinessExceptionResolver.resolve(bex,bundleResolver);
    }
  }
  
  public static String resolveMessage(Throwable t){
    return ExceptionAnnotator.resolveMessage(t,null);
  }
  
  public static void annotate(Throwable t){
    ExceptionAnnotator.annotate(t,null);
  }
  
  /**
   * Iterates through causes of given Throwable to find an instance of either {@link FacadeException},
   * {@link BusinessException}, {@link ServiceException}
   * or {@link EJBAccessException}.
   *
   * Iteration stops when first instance of mentioned Exceptions is found.
   *
   * @return an instance of mentioned Excpetions or the root cause.
   */
  public static Throwable getRootCause(Throwable t){
    Throwable ret = t;
    Throwable cause;
    
    while ((cause=ret.getCause()) != null){
      ret = cause;
      if (ret instanceof FacadeException) break;
      if (ret instanceof ServiceException) break;
//      if (ret instanceof EJBAccessException) break;
    }
    return ret;
  }
  
  public static void annotate(Throwable t, Class bundleResolver){
    ErrorManager em = ErrorManager.getDefault();
    
    FacadeException bex = null;
    
    try{
      bex = guessExcpetion(t);
    } finally{
      String ann = BusinessExceptionResolver.resolve(bex, bundleResolver);
      log.log(Level.INFO,ann,t);
      em.annotate(bex, ann);
      em.notify(ErrorManager.EXCEPTION,bex);
    }
  }
  
  public static FacadeException guessExcpetion(Throwable t){
    FacadeException bex;
    Throwable x;

    bex = new InternalErrorException(); // fallback
    x = getRootCause(t);
    if ( x instanceof FacadeException){
        bex = (FacadeException)x;
//    } else if (x instanceof EJBAccessException){
//      bex = new AuthentificationException();
    } else if (x instanceof StaleObjectStateException){
        Class[] bundleResolvers;
        bundleResolvers = new Class[]{CommonBundleResolver.class};
        bex = new InternalErrorException(BundleResolve.resolve(bundleResolvers,"StaleObjectStateExceptionText",new Object[]{}));
    } else{
        if (t.getMessage() != null){
            bex = new InternalErrorException(t);
        } else{
            bex = new InternalErrorException();
        }
    }
    return bex;
  }

}
