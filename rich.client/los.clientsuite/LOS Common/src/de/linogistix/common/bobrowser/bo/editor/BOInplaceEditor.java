/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.editor;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.mywms.model.BasicEntity;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author trautm
 */
public class BOInplaceEditor implements InplaceEditor{
  
  private BOInplaceEditorPanel panel;
  
  private BasicEntity entity;
  
  private List<ActionListener> listeners = new ArrayList();
  
  private BOEditorReadOnly editor;
  
  PropertyModel model;
  
  /** Creates a new instance of BOInplaceEditor */
  public BOInplaceEditor() {
    return;
  }

  public void connect(PropertyEditor propertyEditor, PropertyEnv propertyEnv) {
    panel = new BOInplaceEditorPanel();
    editor = (BOEditorReadOnly)propertyEditor;
    panel.setEntity(editor.getEntity());
  }

  public JComponent getComponent() {
    return panel;
  }

  public void clear() {
    panel = null;
    entity = null;
    editor = null;
    model = null;
  }

  public Object getValue() {
    return entity;
  }

  public void setValue(Object object) {
    this.entity =  (BasicEntity)object;
    panel.setEntity(entity);
  }

  public boolean supportsTextEntry() {
    return false;
  }

  public void reset() {

  }

  public void addActionListener(ActionListener actionListener) {
    listeners.add(actionListener);
  }

  public void removeActionListener(ActionListener actionListener) {
    listeners.remove(actionListener);
  }

  public KeyStroke[] getKeyStrokes() {
    return new KeyStroke[0];
  }

  public PropertyEditor getPropertyEditor() {
    return editor;
  }

  public PropertyModel getPropertyModel() {
    return model;
  }

  public void setPropertyModel(PropertyModel propertyModel) {
    this.model = propertyModel;
  }

  public boolean isKnownComponent(Component component) {
    if (component == this.panel || this.panel.isAncestorOf(component)){
      return true;
    }
    return false;
  }
  
}
