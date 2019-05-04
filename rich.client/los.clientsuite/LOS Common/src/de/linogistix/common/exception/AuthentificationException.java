/*
 * InternalError.java
 *
 * Created on 13. Oktober 2006, 05:56
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.exception;

import org.mywms.facade.FacadeException;

/**
 * Trown if authentification problems arise.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class AuthentificationException extends FacadeException{
  
  /** Creates a new instance of InternalError */
  public AuthentificationException(Throwable t) {
    super("Authentification error", "BusinessException.Authentification",new Object[]{t.getMessage()});
  }
  
  /** Creates a new instance of InternalError */
  public AuthentificationException() {
    super("Authentification Error", "BusinessException.Authentification",new Object[0]);
  }
  
}
