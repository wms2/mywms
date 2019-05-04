/*
 * BOBeanNode.java
 *
 * Created on 6. Oktober 2006, 23:04
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.annotation.PropertyDescriptor;
import de.linogistix.annotation.PropertyGroup;
import de.linogistix.annotation.PropertyGroups;
import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.binding.BOBeanNodeDescriptor;
import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.bobrowser.bo.editor.EnumPropertyEditorI18N;
import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.common.util.ExceptionAnnotator;
import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Transient;
import javax.swing.Action;
import org.mywms.model.BasicEntity;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Node for representing BusinessObjects in a PropertySheet.
 *
 * #createProperties() is overridden to extend {@link BeanNode} capabilities. These are
 *<ul>
 *<li> configuration of properties via xml file
 *     (see {@link de.linogistix.bobrowser.bo.binding.BOBeanNodeDescriptor}) or annotation
 *     (see {@link de.linogistix.annotation.PropertyDescriptor})
 *<li> Support for i18n
 *</ul
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOBeanNode extends BeanNode {

  private static final Logger log = Logger.getLogger(BOBeanNode.class.getName());
  private Class bundleResolver;
  protected List<Action> actions = new ArrayList();
  private Object beanObject;
  protected BeanInfo beanInfo;
  protected BO bo;
  
  /** Creates a new instance of BOBeanNode */
  public BOBeanNode(Object object) throws IntrospectionException {
    super(object);
    this.beanObject = object;
    initActions();

    bo = lookupBO(object);
    if (bo != null) {
      if (bo.getIconPathWithExtension() != null) {
        setIconBaseWithExtension(bo.getIconPathWithExtension());
      }
    }
    else{
        log.log(Level.WARNING, "--- NO BO FOUND ---");
    }
  }

  protected void initActions() {
  //
  }
  
  

  /**
   * Parses {@link BOBeanNodeDescriptor} or
   * {@link PropertyDescriptor} annotations for grouping BOBeanNode properties.
   *
   * {@link BOBeanNodeDescriptor} takes predecence.
   *
   *@see PropertyDescriptor
   *@see
   *@see BOBeanNode
   */
    @Override
  protected void createProperties(Object object, BeanInfo info) {

    Sheet sheet;
    Sheet.Set set;
    Method m;
    PropertyDescriptor pDescriptor = null;
    PropertyGroups pGroups;
    PropertyGroup[] pGroup;
    Property support;
    Map<String, Sheet.Set> nameMap = new HashMap();
    String s;
    BOBeanNodeDescriptor descr = null;

    try {

      this.beanInfo = info;

      try {
        bo = lookupBO(object);
        if (bo != null) {
          this.bundleResolver = bo.getBundleResolver();
          descr = bo.getDescriptor();
        } else {
          log.log(Level.WARNING, "lookup BO failes for " + object.toString());
          descr = BO.initBeanNodeDescriptor(object);
        }
      } catch (RuntimeException ex) {
        log.log(Level.WARNING, ex.getMessage(), ex);
      }
      
      sheet = Sheet.createDefault();
      
      //default
      set = new Sheet.Set();
      set.setValue("groupPosition", PropertyGroups.GROUP_POSITION_DEFAULT);
      set.setName(PropertyGroups.GROUP_MAIN);
      set.setDisplayName(NbBundle.getMessage(CommonBundleResolver.class, PropertyGroups.GROUP_MAIN));
      set.setShortDescription(NbBundle.getMessage(CommonBundleResolver.class, PropertyGroups.GROUP_MAIN + "_DESCR"));

      nameMap.put(set.getName(), set);

      pGroups = (PropertyGroups) getBean().getClass().getAnnotation(PropertyGroups.class);

      // resolve groups
      if (descr != null) {
        if (descr.getGroups() != null) {
          for (int i = 0; i < descr.getGroups().length; i++) {
            set = new Sheet.Set();
            set.setValue("groupPosition", descr.getGroups()[i].getGroupPosition());
            set.setName(descr.getGroups()[i].getName());
            s = BundleResolve.resolve(
                    new Class[]{getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
                    descr.getGroups()[i].getName(),
                    new Object[0]);
            set.setDisplayName(s);
            s = BundleResolve.resolve(
                    new Class[]{getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
                    descr.getGroups()[i].getName() + "_DESCR",
                    new Object[0],
                    false);
            set.setShortDescription(s);
            nameMap.put(set.getName(), set);
          }
        }
      } else if (pGroups != null) {
        pGroup = pGroups.value();
        for (int i = 0; i < pGroup.length; i++) {
          set = new Sheet.Set();
          set.setValue("groupPosition", pGroup[i].groupPosition());
          set.setName(pGroup[i].name());
          s = BundleResolve.resolve(
                  new Class[]{getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
                  pGroup[i].name(),
                  new Object[0]);
          set.setDisplayName(s);
          s = BundleResolve.resolve(
                  new Class[]{getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
                  pGroup[i].name() + "_DESCR",
                  new Object[0]);
          set.setShortDescription(s);
          nameMap.put(set.getName(), set);
        }
      }

      java.beans.PropertyDescriptor[] propertyDescriptor = info.getPropertyDescriptors();

      //ierate over reflected Properties
      for (java.beans.PropertyDescriptor p : propertyDescriptor) {

        support = createProperty(object, p);

        if (support == null) {
          continue;
        }

        m = p.getReadMethod();
        if (m != null) {
          pDescriptor = (PropertyDescriptor) m.getAnnotation(PropertyDescriptor.class);
        }

        set = nameMap.get(PropertyGroups.GROUP_MAIN);
        support.setValue("position", PropertyGroups.GROUP_POSITION_DEFAULT);

        //resolve properties
        if (descr != null) {
          if (descr.getDescriptions() != null && descr.getDescriptions().size() > 0) {
            PropertyDescriptorElement e = descr.getDescriptions().get(p.getName());
            if (e != null) {
              if (e.isHidden()) {
                continue;
              } else if (e.isInlineObject()) {
               inlineInnerObject(p, nameMap, e.getGroup().getName(), object);
                continue;
              } else {
                set = nameMap.get(e.getGroup().getName());
                support.setValue(PropertyDescriptorElement.VALUE_POSITION, e.getPosition());
                if (e.getTypeHint() != null) {
                  support.setValue(PropertyDescriptorElement.VALUE_TYPE_HINT, e.getTypeHint());
                }
                support.setValue(PropertyDescriptorElement.VALUE_PERSISTENT_FIELD, e.isPersistentField());
                support.setValue(PropertyDescriptorElement.VALUE_I18N, e.isI18n());
              }
            }
          }
        } else if (pDescriptor != null) {
          if (pDescriptor.hidden()) {
            continue;
          } else if (pDescriptor.inline()){ 
            inlineInnerObject(p, nameMap, pDescriptor.group(), object);
            continue;
          }
          else {
            set = nameMap.get(pDescriptor.group());
            support.setValue("position", pDescriptor.position());
            support.setValue("i18n", pDescriptor.i18n());

          }
        }


        if (set != null && support != null) {
          set.put(support);
        } else {
          log.warning("unspecified PropertyGroup, Set or PropertySupport ");
          set = nameMap.get(PropertyGroups.GROUP_MAIN);
        }
//        log.log(Level.INFO,"put: " + support.getName());
      }

      SortedSet<Sheet.Set> ss = new TreeSet(new GroupComparator());
      ss.addAll(nameMap.values());

      for (Sheet.Set elem : ss) {
        Arrays.sort(elem.getProperties(), new PropertyComparator());
        sheet.put(elem);
      }
      
      setSheet(sheet);
      
    } catch (Throwable t) {
      log.log(Level.SEVERE, t.getMessage());
      ExceptionAnnotator.annotate(t);
    }
  }

    public final Property createPropertyDelegate(Object bean, java.beans.PropertyDescriptor p){
        return createProperty(bean, p);
    }

  protected void inlineInnerObject(java.beans.PropertyDescriptor p, Map<String, Sheet.Set> nameMap, String groupName, Object bean) {
    try {
      //handle Properties that are plain Objects (Beans)
      BOBeanNode inner;
      Sheet.Set set;
      Object innerObject = p.getReadMethod().invoke(bean, new Object[0]);
      if (innerObject == null) {
        innerObject = p.getPropertyType().newInstance();
        if (p.getWriteMethod() != null ){
            p.getWriteMethod().invoke(bean,innerObject);
        }
      }
      final Class upperBundleResolver = getBundleResolver();

      inner = new BOBeanNode(innerObject) {

            @Override
            public Class getBundleResolver() {
                return upperBundleResolver;
            }

            @Override
            protected Property createProperty(Object bean, java.beans.PropertyDescriptor p) {
                try{
                    return createPropertyDelegate(bean, p);
                } catch (Throwable t){
                    return super.createProperty(bean, p);
                }
            }
      };

      Sheet innerSheet = inner.getSheet();
      PropertySet[] innerSets = innerSheet.toArray();
      for (int i = 0; i < innerSets.length; i++) {
        set = nameMap.get(groupName);
        if (set == null) {
          log.warning("unspecified PropertyGroup: " + groupName);
          set = nameMap.get(PropertyGroups.GROUP_MAIN);
        }
        set.put(innerSets[i].getProperties());
      }
    } catch (Throwable ex) {
      Exceptions.printStackTrace(ex);
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

    if (p.getReadMethod().getAnnotation(Transient.class) != null){
        support.setValue(PropertyDescriptorElement.VALUE_PERSISTENT_FIELD, Boolean.FALSE);
    }
    
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
    support.setPropertyEditorClass(propertyEditorClass(p,support, editor != null ? editor.getClass() : null));
    for (Enumeration e = p.attributeNames(); e.hasMoreElements();) {
      String aname = (String) e.nextElement();
      support.setValue(aname, p.getValue(aname));
    }

    // Propagate helpID's.
    Object help = p.getValue("helpID"); // NOI18N

    if ((help != null) && (help instanceof String)) {
      support.setValue("helpID", help); // NOI18N
    }

    return support;
  }

  /**
   * Determines PropertyEditor for given PropertyDescriptor
   */
  protected Class propertyEditorClass(java.beans.PropertyDescriptor p,FeatureDescriptor suppoer,Class defaultEditorClass) {
    Class clazz;

    if (p.getPropertyType().isEnum()) {
      clazz = EnumPropertyEditorI18N.class;
    } else {
      if (defaultEditorClass == null) {
        clazz = PlainObjectReadOnlyEditor.class;
      } else {
        clazz = defaultEditorClass;
      }
    }
    return clazz;
  }

  public String getBOSimpleClassname() {
    return getBean().getClass().getSimpleName();
  }

  public boolean canDestroy() {
    return true;
  }

  public Object getBeanObject() {
    return beanObject;
  }



  private class GroupComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      Sheet.Set s1;
      Sheet.Set s2;

      if (o1 == null) {
        throw new IllegalArgumentException("o1 must not be null");
      }
      if (o2 == null) {
        throw new IllegalArgumentException("o2 must not be null");
      }
      if (!(o1 instanceof Sheet.Set)) {
        throw new IllegalArgumentException("o1 must be of type Sheet.Set");
      }
      if (!(o2 instanceof Sheet.Set)) {
        throw new IllegalArgumentException("o2 must be of type Sheet.Set");
      }

      s1 = (Sheet.Set) o1;
      s2 = (Sheet.Set) o2;

      if (s1 == s2) {
        return 0;
      }
      Integer i1 = (Integer) s1.getValue("groupPosition");
      Integer i2 = (Integer) s2.getValue("groupPosition");

      if (i1.equals(i2)) {
        return s1.getName().compareTo(s2.getName());
      } else {
        return i1.compareTo(i2);
      }
    }
  }

  private class PropertyComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      Property p1;
      Property p2;

      if (o1 == null) {
        throw new IllegalArgumentException("o1 must not be null");
      }
      if (o2 == null) {
        throw new IllegalArgumentException("o2 must not be null");
      }
      if (!(o1 instanceof Property)) {
        throw new IllegalArgumentException("o1 must be of type Property");
      }
      if (!(o2 instanceof Property)) {
        throw new IllegalArgumentException("o2 must be of type Property");
      }

      p1 = (Property) o1;
      p2 = (Property) o2;

      if (p1 == p2) {
        return 0;
      }

      Integer i1 = (Integer) p1.getValue("position");
      Integer i2 = (Integer) p2.getValue("position");

      if (i1.equals(i2)) {
        return p1.getName().compareTo(p2.getName());
      } else {
        return i1.compareTo(i2);
      }
    }
  }

  public Action getPreferredAction() {
    if (actions == null || actions.size() < 1) {
      return null;
    }
    return actions.get(0);
  }

  public Action[] getActions(boolean context) {
    return actions.toArray(new Action[0]);
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BOBeanNode)) {
      return false;
    }

    BOBeanNode b = (BOBeanNode) obj;

    return b.getBean().equals(getBean());
  }

  public BO lookupBO(Object o) {
    return lookupBO(o.getClass());
  }

  public BO lookupBO(Class c) {

    BO ret;

    BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
    ret = (BO) l.lookup(c);
    if (ret == null) {
//      RuntimeException ex = new RuntimeException("cannot lookup " + c);
//      log.log(Level.SEVERE,ex.getMessage(), ex);
//      ExceptionAnnotator.annotate(ex);
    }

    return ret;

  }

  public Class getBundleResolver() {
    return bundleResolver;
  }

  public int hashCode() {
    int retValue;

    retValue = getBean().hashCode();

    return retValue;
  }

  public Object getBean() {
    Object retValue;

    retValue = super.getBean();
    return retValue;
  }
}
