/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.bobrowser.bo.editor.BOCollectionEditorReadOnly;
import de.linogistix.common.bobrowser.bo.editor.BOEditorReadOnly;
import de.linogistix.common.bobrowser.bo.editor.BOLockEditorI18N;
import de.linogistix.common.bobrowser.bo.editor.EnumPropertyEditorI18N;
import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;
import de.linogistix.common.bobrowser.action.RefreshBOBeanNodeAction;
import de.linogistix.common.bobrowser.bo.editor.ComboPropertyEditor;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node.Property;
import org.openide.util.actions.SystemAction;

/**
 * A special {@link BOBeanNode} representing a {@link BasicEntity}. As such
 * the BasicEntity should be read only.
 *
 * To realize read-only mode  #propertyEditorClass is overwritten to return read
 * only editors. See {@link de.linogistix.bobrowser.crud.BOEditNode} for editable
 * BasicEntities.
 *
 * @author trautm
 */
public class BOEntityNodeReadOnly extends BOEntityNode{
  
  
  private static Logger log = Logger.getLogger(BOEntityNodeReadOnly.class.getName());
  /**
   * Creates a new instance of BOEntityNodeReadOnly
   */
  public BOEntityNodeReadOnly(BasicEntity entity) throws IntrospectionException {
    super(entity);
    this.entity = entity;
  }
 
  public BasicEntity getBo() {
    return entity;
  }
  
  public boolean canDestroy() {
    return true;
  }
  
  
  protected void initActions(){
    SystemAction action;
    
    action = SystemAction.get(RefreshBOBeanNodeAction.class);
    action.setEnabled(true);
    actions.add(action);
//    
//    action = SystemAction.get(BODeleteAction.class);
//    action.setEnabled(true);
//    actions.add(action);
    
  }
  
  /**
   * Determines PropertyEditor for given PropertyDescriptor
   */
  protected Class propertyEditorClass(java.beans.PropertyDescriptor p,FeatureDescriptor support, Class defaultEditorClass){
//    log.info("determining propertyEditorClass for " + p.getPropertyType());
      if( this.bo == null ) {
          log.log(Level.WARNING, "No BO provided BO=NULL");
          return null;
      }
    if (BasicEntity.class.isAssignableFrom(p.getPropertyType())){
      return (BOEditorReadOnly.class);
    } else if (Collection.class.isAssignableFrom(p.getPropertyType())){
      return BOCollectionEditorReadOnly.class;
    } else if (p.getPropertyType().isEnum()) {
      support.setValue("bundleResolver", this.bo.getBundleResolver());  
      return EnumPropertyEditorI18N.class;
    } else if (p.getName().equals("lock")){
      support.setValue("lockStates", this.bo.getLockStates());
      support.setValue("bundleResolver", this.bo.getBundleResolver());
      return BOLockEditorI18N.class;
    } else if (p.getPropertyType().isPrimitive() && this.bo.getValueList(p.getName())!=null){
      support.setValue("valueList", this.bo.getValueList(p.getName()));
      support.setValue("bundleResolver", this.bo.getBundleResolver());
      return ComboPropertyEditor.class;
    }  else{
      return PlainObjectReadOnlyEditor.class;
    }
  }
  
  /**
   *Creates and returns a Property for the given PropertyDescriptor. Returns null
   * if this PropertyDescriptor should be ignored. This is the case for the <code>class</code>
   * proeprty because this is regarded as not interesting for an user.
   */
  protected Property createProperty(Object bean, java.beans.PropertyDescriptor p){
//    BO bo = lookupBO(bean);
      if (Boolean.class.isAssignableFrom(p.getPropertyType())){
            try {
                p.setWriteMethod(null);
        
            } catch (IntrospectionException ex) {
                log.log(Level.INFO, ex.getMessage(), ex);
            }
      }
      Property prop = super.createProperty(bean, p); 
      if (prop != null) prop.setValue(PropertyDescriptorElement.VALUE_ACCESS, PropertyDescriptorElement.VALUE_ACCESS_READONLY);
      return prop;
  } 
  
}
