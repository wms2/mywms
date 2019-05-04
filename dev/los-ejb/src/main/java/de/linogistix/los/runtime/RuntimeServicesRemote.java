/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.runtime;

import javax.ejb.Remote;


/**
 * This is the business interface for RuntimeServices enterprise bean.
 */
@Remote
public interface RuntimeServicesRemote {
  /**
   * To test whether the application server can be connected.
   */
  void ping();
}
