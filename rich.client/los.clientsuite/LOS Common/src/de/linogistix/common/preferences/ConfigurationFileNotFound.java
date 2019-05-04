/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.preferences;

import org.mywms.facade.FacadeException;

/**
 *
 * @author trautm
 */
public class ConfigurationFileNotFound extends FacadeException{
  
  /** Creates a new instance of ConfigurationFileNotFound */
  public ConfigurationFileNotFound(String forE) {
    super("no configuration file found","BusinessException.ConfigurationFileNotFound",new Object[]{forE});
  }
  
}
