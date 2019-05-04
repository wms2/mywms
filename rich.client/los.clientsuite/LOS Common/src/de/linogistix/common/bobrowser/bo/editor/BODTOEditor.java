/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.los.query.BODTO;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author trautm
 */
public class BODTOEditor extends PropertyEditorSupport implements ExPropertyEditor{

  @Override
  public String getAsText() {
    if (getValue() == null){
      return "";
    } else{
      BODTO dto = (BODTO)getValue();
      return dto.getName();
    }
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    return;
  }
  
  @Override
  public boolean supportsCustomEditor() {
    return false;
  }

  public void attachEnv(PropertyEnv arg0) {
    //
  }
  
  
 
}
