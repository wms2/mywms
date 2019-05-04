/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.runtime;

import javax.ejb.Stateless;

/**
 *
 * @author trautm
 */
@Stateless
public class RuntimeServicesBean implements de.linogistix.los.runtime.RuntimeServicesRemote {
  
  /** Creates a new instance of RuntimeServicesBean */
  public RuntimeServicesBean() {
  }

  public void ping() {
    return;
  }
  
}
