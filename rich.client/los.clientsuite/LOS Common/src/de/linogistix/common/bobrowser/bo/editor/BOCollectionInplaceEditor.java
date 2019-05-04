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
import java.util.Collection;
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
public class BOCollectionInplaceEditor implements InplaceEditor{
  
  private BOCollectionInplaceEditorPanel panel;
  
  private Collection<BasicEntity> entities;
  
  private List<ActionListener> listeners = new ArrayList();
  
  private BOCollectionEditorReadOnly editor;
  
  PropertyModel model;
  
  /** Creates a new instance of BOInplaceEditor */
  public BOCollectionInplaceEditor() {
  }

  public void connect(PropertyEditor propertyEditor, PropertyEnv propertyEnv) {
    panel = (BOCollectionInplaceEditorPanel) getComponent();
    editor = (BOCollectionEditorReadOnly)propertyEditor;
    panel.setEntities(editor.getEntities());
  }

  public JComponent getComponent() {
    if (panel == null){
        panel = new BOCollectionInplaceEditorPanel();
    }
      return panel;
  }

  public void clear() {
    panel = null;
    entities = null;
    editor = null;
    model = null;
  }

  public Object getValue() {
    return entities!=null?entities:new ArrayList<BasicEntity>();
  }

  public void setValue(Object object) {
    this.entities =  (Collection<BasicEntity>)object;
    panel.setEntities(entities);
  }

  public boolean supportsTextEntry() {
    return false;
  }

  public void reset() {
//    panel.setEntity(entity);
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
