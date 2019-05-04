/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.editor.BOCollectionEditorReadOnly;
import de.linogistix.common.bobrowser.bo.editor.BOEditorReadOnly;
import de.linogistix.common.bobrowser.bo.editor.BOLockEditorI18N;
import de.linogistix.common.bobrowser.bo.editor.ComboPropertyEditor;
import de.linogistix.common.bobrowser.bo.editor.EnumPropertyEditorI18N;
import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.util.Collection;
import org.mywms.model.BasicEntity;
import org.openide.nodes.PropertySupport;

/**
 * A special {@link BOEntityNodeReadOnly} for representation a query result.
 * 
 * 
 * @author trautm
 */
public class BOEntityQueryNode extends BOEntityNodeReadOnly{
  
  /** Creates a new instance of BOEntityQueryNode */
  public BOEntityQueryNode(BasicEntity e) throws IntrospectionException {
    super(e);
  }
  
  /**
   * Determines PropertyEditor for given PropertyDescriptor. To realize read-only mode  #propertyEditorClass is overwritten to return read
   * only editors.
   */
    @Override
  protected Class propertyEditorClass(java.beans.PropertyDescriptor p,FeatureDescriptor support, Class defaultEditorClass){
//    log.info("determining propertyEditorClass for " + p.getPropertyType());
    if (BasicEntity.class.isAssignableFrom(p.getPropertyType())){
      return (BOEditorReadOnly.class);
    } else if (Collection.class.isAssignableFrom(p.getPropertyType())){
      return BOCollectionEditorReadOnly.class;
    }else if (p.getPropertyType().isEnum()) {
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
   * Takes the version attribute into account.
   */
  public boolean equals(Object obj) {

    if (this == obj) return true;
    if (obj == null) return false;  
    if (! (obj instanceof BOEntityQueryNode)) return false;
    
    BOEntityQueryNode node = (BOEntityQueryNode)obj;
    
    if (getBo().getId() == node.getBo().getId()){
      if (getBo().getVersion() == getBo().getVersion()){
        return true;
      }
    }
    
    return false;
  
  }
  
  
  
}
