/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.bobrowser.util.TypeResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.NbBundle;

/**
 * Model for JComboBox showing BOBeanProperties.
 *
 * @author trautm
 */
public class PropertiesComboBoxModel extends DefaultComboBoxModel{
  
  private static final Logger log = Logger.getLogger(PropertiesComboBoxModel.class.getName());
  
  Vector<BOBeanPropertyEntry> properties;
  int selectedIndex = 0;
  
  public PropertiesComboBoxModel(BOEntityNodeReadOnly boBeanNode){
    
    int pSetIndex = 0;
    int pIndex = 0;
    properties = new Vector();
    Object attr;
    
    try {
      properties.add(new BOBeanPropertyEntry());
      if (boBeanNode.getPropertySets() == null){
        log.log(Level.SEVERE, "no property sets defined for " + boBeanNode.getBo().toString());
        return;
      }
      for (pSetIndex=0; pSetIndex < boBeanNode.getPropertySets().length; pSetIndex++) {
        PropertySet set = boBeanNode.getPropertySets()[pSetIndex];
        properties.add(new BOBeanPropertyEntry("- " + set.getDisplayName() ));
        for (pIndex=0; pIndex<set.getProperties().length; pIndex++){
          Property p = set.getProperties()[pIndex];
          attr =  p.getValue(PropertyDescriptorElement.VALUE_PERSISTENT_FIELD);
          if ( attr != null && (!((Boolean)attr).booleanValue())){
            // Not a field in a database table
            continue;
          }
          if (Collection.class.isAssignableFrom(p.getValueType())){
            BOBeanPropertyEntry entry = new BOBeanPropertyEntry(p, pSetIndex, pIndex);
            entry.setDisabled(false);
            properties.add(entry );
          } else if ((p.getValueType().isArray())){
            BOBeanPropertyEntry entry = new BOBeanPropertyEntry(p, pSetIndex, pIndex);
            entry.setDisabled(true);
            properties.add(entry );
          } else if (BasicEntity.class.isAssignableFrom(p.getValueType())){
            properties.add(new BOBeanPropertyEntry(p, pSetIndex, pIndex));
          } else if (TypeResolver.isPrimitiveType(p.getValueType())){
              properties.add(new BOBeanPropertyEntry(p, pSetIndex, pIndex));
          }  else if (TypeResolver.isDateType(p.getValueType())){
              properties.add(new BOBeanPropertyEntry(p, pSetIndex, pIndex));
          } else{
            BOBeanPropertyEntry entry = new BOBeanPropertyEntry(p, pSetIndex, pIndex);
            entry.setDisabled(false);
            properties.add(entry );
          }
          
        }
      }
    } catch (Throwable ex) {
      ExceptionAnnotator.annotate(ex);
    }
  }
  
  public void setSelectedItem(Object anItem) {
    BOBeanPropertyEntry e = (BOBeanPropertyEntry)anItem;
    if (e.isSeparator() || e.isDisabled()){
      selectedIndex = 0;
    } else{
      selectedIndex = properties.indexOf(anItem);
    }
  }
  
  public Object getSelectedItem() {
    return properties.elementAt(selectedIndex);
  }
  
  public int getSize() {
    return properties.size();
  }
  
  public Object getElementAt(int index) {
    return properties.elementAt(index);
  }
  
  //----------------------------------------------------------------------------
  
  /**
   * Helper class for wrapping BOBeanProeprties and showing them in a ComboBox
   */
  public static final class BOBeanPropertyEntry {
    String displayName;
    int propertySetIndex;
    int propertyIndex;
    boolean separator;
    private boolean doNotSort;
    private boolean disabled;
            
    BOBeanPropertyEntry(Property p, int propertySetIndex, int propertyIndex){
      
      if (p.getDisplayName() != null){
        this.displayName = p.getDisplayName();
      } else{
        this.displayName = p.getName();
      }
      this.propertySetIndex  = propertySetIndex;
      this.propertyIndex = propertyIndex;
      this.separator = false;
      this.doNotSort = false;
      this.setDisabled(false);
    }
    
    BOBeanPropertyEntry(String displayName){
      this.displayName = displayName;
      this.separator = true;
      this.doNotSort = true;
      this.setDisabled(false);
    }
    
    BOBeanPropertyEntry(){
      this.displayName = "<" + NbBundle.getMessage(CommonBundleResolver.class,"unspecified") + ">";
      this.separator = false;
      this.doNotSort = true;
      this.setDisabled(false);
    }
    
    public String toString() {
      return getDisplayName();
    }
    
    public String getDisplayName() {
      if (!disabled){
        return displayName;
      } else{
        return displayName;
      }
    }
    
    public int getPropertySetIndex() {
      return propertySetIndex;
    }
    
    public int getPropertyIndex() {
      return propertyIndex;
    }
    
    public boolean isSeparator() {
      return separator;
    }

    public boolean isDoNotSort() {
      return doNotSort;
    }

    public void setDoNotSort(boolean doNotSort) {
      this.doNotSort = doNotSort;
    }

    public boolean isDisabled() {
      return disabled;
    }

    public void setDisabled(boolean disabled) {
      this.disabled = disabled;
    }
  }
  
  //----------------------------------------------------------------------------
  
  /**
   * Renderer for JComboBox showing BOBeanProperties
   */
 public static final class BOBeanPropertiesBoxRenderer extends DefaultListCellRenderer {
     ListCellRenderer renderer = null;
     public BOBeanPropertiesBoxRenderer() {
         super();
     }
     public BOBeanPropertiesBoxRenderer(ListCellRenderer renderer) {
         this.renderer = renderer;
     }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      BOBeanPropertyEntry e = (BOBeanPropertyEntry)value;
      Component ret;

      if( renderer == null ) {
        ret = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
      }
      else {
        ret = renderer.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
      }

      if (e.isSeparator() ){
        ret.setBackground(Color.LIGHT_GRAY);
        ret.setForeground(Color.BLACK);
        ret.setFocusable(false);
      } else if (e.isDisabled()){
        ret.setFocusable(false);
        ret.setForeground(Color.GRAY);
      }
      return ret;
    }
    
  }
}
