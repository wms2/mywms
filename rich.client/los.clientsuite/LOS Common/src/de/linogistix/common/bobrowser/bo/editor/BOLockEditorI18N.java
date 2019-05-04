/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.los.entityservice.BusinessObjectLock;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Logger;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * PropertyEditor for enum types using a combobox. 
 *
 * Values are translated via BundleResolve.
 *  
 * @author trautm
 */
public final class BOLockEditorI18N extends PropertyEditorSupport
        implements ExPropertyEditor {

  private static final Logger log = Logger.getLogger(BOLockEditorI18N.class.getName());
  private Map<String, Integer> i18NMap;
  private Map<Integer, String> n18iMap;
  private boolean ro;
  private PropertyEnv env;
  
  private List<BusinessObjectLock> lockStates;
  private Class bundleResolver;
 

  public BOLockEditorI18N() {
    this.i18NMap = new HashMap();
    this.n18iMap = new HashMap();
  }

  public String[] getTags() {
    try {
      String[] tags = new String[lockStates.size()];
      int i = 0;
      for (BusinessObjectLock state : lockStates) {  
          String s;
          try {        
              s = NbBundle.getMessage(state.getBundleResolver(), state.getMessageKey());
            }
            catch( MissingResourceException e ) {
                log.warning("Can't resolve key: " + state.getMessageKey() + " ->" + e.getMessage());
                s = state.toString();
            }
            
        tags[i] = s;
        this.i18NMap.put(tags[i], state.getLock());
        this.n18iMap.put(state.getLock(), tags[i]);
        i++;
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
    Integer o = (Integer) getValue();
    String s = this.n18iMap.get(o);
    if (s == null && o != null){
        s = o.toString();
    }
    return s != null ? s : "";
  }

  @SuppressWarnings("unchecked")
  public void setAsText(String text) throws IllegalArgumentException {
    if (ro){
      return;
    }
    
    Integer i;
    try{
        i = i18NMap.get(text);
        if (i == null){
            i = Integer.parseInt(text);
        }
    } catch (NumberFormatException ex){
       throw ex;  
    }
    
    if (text.length() > 0) {
      setValue(i);
    } else {
      setValue(null);
    }
    firePropertyChange();
  }

  public String getJavaInitializationString() {
    Enum e = (Enum) getValue();
    //return e != null ? c.getName().replace('$', '.') + '.' + e.name() : "null"; // NOI18N
    return null;
  }

  public void attachEnv(PropertyEnv env) {
    this.env = env;
    Node.Property p = (Node.Property) env.getFeatureDescriptor();
    lockStates = (List<BusinessObjectLock>) p.getValue("lockStates");
    bundleResolver = (Class) p.getValue("bundleResolver");
    String access = (String) p.getValue(PropertyDescriptorElement.VALUE_ACCESS);
    if (access != null){
        this.ro = access.equals(PropertyDescriptorElement.VALUE_ACCESS_READONLY);
    } else{
        this.ro = !p.canWrite();
    }
    env.getFeatureDescriptor().setValue( "canEditAsText", Boolean.TRUE );

  }
}
