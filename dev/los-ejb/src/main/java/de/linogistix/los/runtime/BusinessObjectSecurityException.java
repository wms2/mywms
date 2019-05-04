/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.runtime;

import javax.ejb.ApplicationException;
import org.mywms.facade.FacadeException;
import org.mywms.model.User;

/**
 *
 * @author trautm
 */
@ApplicationException(rollback = true)
public class BusinessObjectSecurityException extends FacadeException{
  
  private static final long serialVersionUID = 1L;
  
  public final static String RESOURCE_KEY = "BusinessException.Security";
  
  /** Creates a new instance of BusinessObjectSecurityException */
  public BusinessObjectSecurityException(User user) {
    super("Security Exception for user " + (user == null ? "" : user.getName()),
            RESOURCE_KEY, new Object[]{(user == null ? "":user.getName())});
  }
  
}
