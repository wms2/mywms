/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.object;

/**
 *
 * @author artur
 */
public class LOSAutoFilteringComboBoxNode {
  private String title;
  private Object object;

  public LOSAutoFilteringComboBoxNode(String title, Object object) {
    this.title = title;
    this.object = object;
  }
 
  public String toString() {
    return title;
  }

    public Object getObject() {
        return object;
    } 
}
