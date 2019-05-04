/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.editor;

import org.mywms.facade.FacadeException;

/**
 *
 * @author trautm
 */
public class BOEditorTypeException  extends FacadeException{
  
  
  /** Creates a new instance of BusinessObjectRemovedException */
  public BOEditorTypeException() {
    super("Cannot resolve type", "BusinessException.typeNotReflectable",new Object[]{});
  }
  
}
