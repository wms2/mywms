/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.action.RefreshBOBeanNodeAction;
import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.bobrowser.bo.editor.BOCollectionEditor;
import de.linogistix.common.bobrowser.bo.editor.BOEditorChoose;
import de.linogistix.common.bobrowser.bo.editor.BOLockEditorI18N;
import de.linogistix.common.bobrowser.bo.editor.ComboPropertyEditor;
import de.linogistix.common.bobrowser.bo.editor.EnumPropertyEditorI18N;
import de.linogistix.common.bobrowser.crud.BODeleteAction;
import de.linogistix.common.bobrowser.util.TypeResolver;
import de.linogistix.common.gui.component.other.BigDecimalEditor;
import de.linogistix.common.gui.component.other.DatePropertyEditor;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Transient;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

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
public class BOEntityNode extends BOBeanNode {

  BasicEntity entity;
  private static Logger log = Logger.getLogger(BOEntityNode.class.getName());

  /**
   * Creates a new instance of BOEntityNodeReadOnly
   */
  public BOEntityNode(BasicEntity entity) throws IntrospectionException {
    super(entity);
    this.entity = entity;
    try{
        setDisplayName(entity.toUniqueString());
    } catch (Throwable t){
        log.warning("Could not set displayname: " + t.getMessage());
    }
  }

  public BasicEntity getBo() {
    return entity;
  }

  public boolean canDestroy() {
    return true;
  }

  protected void initActions() {
    SystemAction action;

    action = SystemAction.get(RefreshBOBeanNodeAction.class);
    action.setEnabled(true);
    actions.add(action);
//    
//    action = SystemAction.get(BODeleteAction.class);
//    action.setEnabled(true);
//    actions.add(action);

  }

  public void destroy() throws IOException {
    log.log(Level.INFO, "################# destroy");
    BODeleteAction del = (BODeleteAction) SystemAction.get(BODeleteAction.class);
//    del.actionPerformed(new ActionEvent(this,1,""));
    Lookup l = Lookups.singleton(this);
    del.createContextAwareInstance(l).actionPerformed(null);
  }

  /**
   * Determines PropertyEditor for given PropertyDescriptor
   */
  protected Class propertyEditorClass(java.beans.PropertyDescriptor p,FeatureDescriptor suppoer,Class defaultEditorClass) {
    //    log.info("determining propertyEditorClass for " + p.getPropertyType());
    if (BasicEntity.class.isAssignableFrom(p.getPropertyType())) {
//      log.info("propertyEditorClass found " + BOEditorReadOnly.class.getName());
      return (BOEditorChoose.class);
    } else if (Collection.class.isAssignableFrom(p.getPropertyType())) {
      return BOCollectionEditor.class;
    } else if (p.getPropertyType().isEnum()) {
      suppoer.setValue("bundleResolver", this.bo.getBundleResolver());  
      return EnumPropertyEditorI18N.class;
    } else if (p.getName().equals("lock")){
      suppoer.setValue("lockStates", this.bo.getLockStates());
      suppoer.setValue("bundleResolver", this.bo.getBundleResolver());
      return BOLockEditorI18N.class;
    } else if (p.getPropertyType().isPrimitive() && this.bo.getValueList(p.getName())!=null){
      suppoer.setValue("valueList", this.bo.getValueList(p.getName()));
      if(this.bo.getBundlePrefix()!=null) {
        suppoer.setValue("bundlePrefix", this.bo.getBundlePrefix());
      }
      suppoer.setValue("bundleResolver", this.bo.getBundleResolver());
      return ComboPropertyEditor.class;
    } else if (Date.class.isAssignableFrom(p.getPropertyType()) ) {
        return DatePropertyEditor.class;
    } else if (BigDecimal.class.isAssignableFrom(p.getPropertyType()) ) {
        return BigDecimalEditor.class;
    } else {
      return defaultEditorClass;
    }
  }
  
  

  /**
   *Creates and returns a Property for the given PropertyDescriptor. Returns null
   * if this PropertyDescriptor should be ignored. This is the case for the <code>class</code>
   * proeprty because this is regarded as not interesting for an user.
   */
  protected Property createProperty(Object bean, java.beans.PropertyDescriptor p) {
//    BO bo = lookupBO(bean);
    PropertySupport.Reflection support;
    String s;

    if (p.getName() == null || p.getName().equals("")) {
      return null;
    }

    if (p.getName().equals("class")) {
      return null;
    }

    // Note that PS.R sets the method accessible even if it is e.g.
    // defined as public in a package-accessible superclass.
    support = new PropertySupport.Reflection(
            bean, p.getPropertyType(), p.getReadMethod(), p.getWriteMethod());

    support.setName(p.getName());

    s = BundleResolve.resolve(
            new Class[]{getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
            p.getName(),
            new Object[0]);

    support.setDisplayName(s);

    s = BundleResolve.resolve(
            new Class[]{getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
            p.getName() + "_DESCR",
            new Object[0],
            false);
    support.setShortDescription(s);

    //
    PropertyEditor editor = PropertyEditorManager.findEditor(p.getPropertyType());
    support.setPropertyEditorClass(propertyEditorClass(p, support,editor != null ? editor.getClass() : null));

    for (Enumeration e = p.attributeNames(); e.hasMoreElements();) {
      String aname = (String) e.nextElement();
      support.setValue(aname, p.getValue(aname));
    }
    
    if (p.getReadMethod()!=null && p.getReadMethod().getAnnotation(Transient.class) != null){
        support.setValue(PropertyDescriptorElement.VALUE_PERSISTENT_FIELD, Boolean.FALSE);
    }

    // Propagate helpID's.
    Object help = p.getValue("helpID"); // NOI18N

    if ((help != null) && (help instanceof String)) {
      support.setValue("helpID", help); // NOI18N
    }

    if (BasicEntity.class.isAssignableFrom(p.getPropertyType())) {
//      support.setValue("inplaceEditor", new BOInplaceEditorAutoFiltering());
      support.setValue(PropertyDescriptorElement.VALUE_TYPE_HINT, p.getPropertyType());
    } else if (Collection.class.isAssignableFrom(p.getPropertyType())) {
      //support.setValue("inplaceEditor", new BOCollectionInplaceEditor());
    }
    
    if (TypeResolver.isStringType(support.getValueType())){
      support.setValue("nullValue", "");
    }
    
    return support;
  }

  /**
   * updates bean and rebuilds Sheet
   */
  public void updateBO(BasicEntity entity) {
    this.entity = entity;
//    log.info("updateBO");
    createProperties(this.entity, this.beanInfo);
    firePropertySetsChange(null, null);
  }
  
  
}
