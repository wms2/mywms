/*
 * LoginException.java
 *
 * Created on 21. November 2006, 17:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.userlogin;

import org.mywms.facade.FacadeException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class LoginException extends FacadeException {
  
  /** Creates a new instance of LoginException */
  public LoginException(String user) {
    super("login failed","BusinessException.loginFailed",new Object[]{user});
  }
  
}
