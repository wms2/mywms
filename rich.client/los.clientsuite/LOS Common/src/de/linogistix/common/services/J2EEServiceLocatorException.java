/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

import org.mywms.facade.FacadeException;

/**
 * @author Rene
 */
public class J2EEServiceLocatorException extends FacadeException {
  
  private static final long serialVersionUID = 1L;
  
  public J2EEServiceLocatorException() {
    super("Exception while lookup J2EE service","BusinessException.J2EEServiceLocator",new Object[]{0});
  }
  
  public J2EEServiceLocatorException(String key, Object[] params){
    super("Exception while lookup J2EE service",key, params);
  }
  
}
