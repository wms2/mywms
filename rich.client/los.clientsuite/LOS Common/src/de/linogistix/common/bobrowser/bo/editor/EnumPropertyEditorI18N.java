/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

/**
 *
 * PropertyEditor for enum types using a combobox. 
 *
 * Values are translated via BundleResolve.
 *  
 * @author trautm
 */
public final class EnumPropertyEditorI18N extends PropertyEditorSupport
        implements ExPropertyEditor {

  private Class<? extends Enum> c;
  private Map<String, String> i18NMap;
  private Map<Object, String> n18iMap;
  private boolean ro;
  private PropertyEnv env;
  private Class bundleResolver;

  public EnumPropertyEditorI18N(Class<? extends Enum> c) {
    this.c = c;
    this.i18NMap = new HashMap();
    this.n18iMap = new HashMap();
  }

  public EnumPropertyEditorI18N() {
    this.i18NMap = new HashMap();
    this.n18iMap = new HashMap();
  }

  public String[] getTags() {
    try {
      Object[] values = (Object[]) c.getMethod("values").invoke(null); // NOI18N
      String[] tags = new String[values.length];
      for (int i = 0; i < values.length; i++) {
        //###TODO: get alternative BundleResolver.classes from property argument   
        String s = c.getSimpleName() + "." + values[i].toString();
        tags[i] = BundleResolve.resolve(new Class[]{CommonBundleResolver.class, bundleResolver}, s, new Object[0]);
        this.i18NMap.put(tags[i], values[i].toString());
        this.n18iMap.put(values[i], tags[i]);
      }
      if (ro) {
        return new String[]{n18iMap.get(getValue())};
      } else{
        return tags;
      }
    } catch (Exception x) {
      throw new AssertionError(x);
    }
  }

  public String getAsText() {
    Object o = getValue();
    String s = this.n18iMap.get(o);
    return s != null ? s : "";
  }

  @SuppressWarnings("unchecked")
  public void setAsText(String text) throws IllegalArgumentException {
    if (ro){
      return;
    }
    if (text.length() > 0) {
      setValue(Enum.valueOf(c, i18NMap.get(text)));
    } else {
      setValue(null);
    }
    firePropertyChange();
  }

  public String getJavaInitializationString() {
    Enum e = (Enum) getValue();
    return e != null ? c.getName().replace('$', '.') + '.' + e.name() : "null"; // NOI18N
  }

  public void attachEnv(PropertyEnv env) {
    this.env = env;
    Node.Property p = (Node.Property) env.getFeatureDescriptor();
    this.c = p.getValueType();
    bundleResolver = (Class) p.getValue("bundleResolver");
    String access = (String) p.getValue(PropertyDescriptorElement.VALUE_ACCESS);
    if (access != null){
        this.ro = access.equals(PropertyDescriptorElement.VALUE_ACCESS_READONLY);
    } else{
        this.ro = !p.canWrite() ;
    }
  }
}
