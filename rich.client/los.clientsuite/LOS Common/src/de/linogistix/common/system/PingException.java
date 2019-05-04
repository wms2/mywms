/*
 * PingException.java
 *
 * Created on 21. November 2006, 17:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.system;

import org.mywms.facade.FacadeException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class PingException extends FacadeException {
  
  /**
   * Creates a new instance of PingException
   */
  public PingException() {
    super("ping failed","BusinessException.pingFailed",new Object[0]);
  }
  
}
