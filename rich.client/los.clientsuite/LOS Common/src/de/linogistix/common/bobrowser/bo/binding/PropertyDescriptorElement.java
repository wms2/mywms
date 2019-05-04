/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.binding;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author trautm
 */
@XmlRootElement( namespace="http://schema.linogistix.com/bobeandescriptor")
public final class PropertyDescriptorElement {
  
  public static final String VALUE_TYPE_HINT = "typeHint";
  
  public static final String VALUE_POSITION = "position";
  
  public final static String VALUE_PERSISTENT_FIELD = "persistentField";
    
  public static String VALUE_I18N = "i18n";
  
  public static String VALUE_ACCESS = "access";
  
  public static String VALUE_ACCESS_READONLY = "readOnly";
  
  private boolean hidden;
  
  private boolean i18n;
  
  private PropertyGroupElement group;
  
  private int position;
  
  /** For POJO Objects associated with the Bean*/ 
  private boolean inlineObject;
  
  /*suitable for determining type of collection elements*/
  private Class typeHint;
  
  /**If true this attribute is directly mapped to database.
   * 
   * If false, it won't e.g. be used in order by clauses*/
  private boolean persistentField = true;
    
  public boolean isHidden() {
    return hidden;
  }
  
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  public PropertyGroupElement getGroup() {
    return group;
  }
  
  public void setGroup(PropertyGroupElement group) {
    this.group = group;
  }
  
  public int getPosition() {
    return position;
  }
  
  public void setPosition(int position) {
    this.position = position;
  }

  public Class getTypeHint() {
    return typeHint;
  }

  public void setTypeHint(Class typeHint) {
    this.typeHint = typeHint;
  }

  public boolean isInlineObject() {
    return inlineObject;
  }

  public void setInlineObject(boolean inlineObject) {
    this.inlineObject = inlineObject;
  }

  public boolean isPersistentField() {
    return persistentField;
  }

  public void setPersistentField(boolean persistentField) {
    this.persistentField = persistentField;
  }

  public boolean isI18n() {
    return i18n;
  }

  public void setI18n(boolean i18n) {
    this.i18n = i18n;
  }

  
}
