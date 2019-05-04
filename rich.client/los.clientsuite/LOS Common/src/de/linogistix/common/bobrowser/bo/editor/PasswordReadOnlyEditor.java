/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.editor;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author trautm
 */
public class PasswordReadOnlyEditor extends PropertyEditorSupport{
  
  public static final String SHOW_PASSWORD = "**********";
  
  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    //
  }

  public String getAsText() {
    return SHOW_PASSWORD;
  }
}
