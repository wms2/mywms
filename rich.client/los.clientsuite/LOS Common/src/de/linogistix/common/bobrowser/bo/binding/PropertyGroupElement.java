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
public final class PropertyGroupElement {
  
  private String name;
  
  private boolean hidden;
  
  private int groupPosition;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public boolean isHidden() {
    return hidden;
  }
  
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  public int getGroupPosition() {
    return groupPosition;
  }
  
  public void setGroupPosition(int position) {
    this.groupPosition = position;
  }

  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (this == obj) return true;
    if (!(obj instanceof PropertyGroupElement) ) return false;
    
    PropertyGroupElement e = (PropertyGroupElement)obj;
    if (this.getName().equals(e.getName())) return true;
    
    return false;
    
  }
  

  
}
