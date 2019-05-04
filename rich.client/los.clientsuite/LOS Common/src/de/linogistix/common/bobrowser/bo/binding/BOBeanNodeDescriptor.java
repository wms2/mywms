/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.binding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author trautm
 */
@XmlRootElement( namespace="http://schema.linogistix.com/bobeandescriptor")
public final class BOBeanNodeDescriptor {
  
  private static final Logger log  =Logger.getLogger(BOBeanNodeDescriptor.class.getName());
  
  private Class forClass;
  
  private PropertyGroupElement[] groups;
  
  private Map<String,PropertyDescriptorElement> descriptions;
  
  public Class getForClass() {
    return forClass;
  }
  
  public void setForClass(Class forClass) {
    this.forClass = forClass;
  }
  
  public PropertyGroupElement[] getGroups() {
    return groups;
  }
  
  public void setGroups(PropertyGroupElement[] groups) {
    this.groups = groups;
  }
  
  public Map<String, PropertyDescriptorElement> getDescriptions() {
    return descriptions;
  }
  
  public void setDescriptions(Map<String, PropertyDescriptorElement> descriptions) {
    this.descriptions = descriptions;
  }
  
  public void pack(BOBeanNodeDescriptor p){
    log.log(Level.INFO,"packing/merging");
    List<PropertyGroupElement> l = new ArrayList();
    if (getGroups() != null){
      for (PropertyGroupElement g : getGroups()) {
//        log.log(Level.INFO, "preserve group: "  + g.getName());
        l.add(g);
      }
    }
    if (p.getGroups() != null){
      for (PropertyGroupElement n : p.getGroups()){
        if ( ! l.contains(n)){
//          log.log(Level.INFO, "add group: "  + n.getName());
          l.add(n);
        }
      }
    }
    setGroups(l.toArray(new PropertyGroupElement[0]));
    
    for (String key : p.getDescriptions().keySet()) {
      if (! getDescriptions().containsKey(key)){
//        log.log(Level.INFO, "add PropertyDescription for "  + key);
        this.descriptions.put(key, p.getDescriptions().get(key));
      }
    }
    
    
  }
  
  
}



