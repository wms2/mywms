/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.los.query.BODTO;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.mywms.model.BasicEntity;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.Node;

/**
 *
 * @author trautm
 */
public class BOInplaceEditorAutoFiltering implements InplaceEditor{

    PropertyModel model;
    
    private PropertyEditor editor = null;

    private BOAutoFilteringComboBox picker = null;

    public BOInplaceEditorAutoFiltering() {
    }

    public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
        editor = propertyEditor;
        Node.Property p = (Node.Property)env.getFeatureDescriptor();
        Class typeHint = p.getValueType();   
        picker = new BOAutoFilteringComboBox();
        picker.setBoClass(typeHint);
        reset();
    }

    public JComponent getComponent() {
        return picker.getAutoFilteringComboBox();
    }

    public void clear() {
        editor = null;
        model = null;
    }

    public Object getValue() {
        return picker.getSelectedAsEntity();
    }

    public void setValue(Object arg0) {
        BasicEntity d = (BasicEntity) arg0;
        BODTO dto = new BODTO(d.getId(), d.getVersion(), d.toUniqueString());
        picker.setSelectedItem(dto);
        
    }

    public boolean supportsTextEntry() {
        return true;
    }

    public void reset() {
      BasicEntity d = (BasicEntity) editor.getValue();
        if (d != null) {
            BODTO dto = new BODTO(d.getId(), d.getVersion(), d.toUniqueString());
            picker.setSelectedItem(dto);
        }

    }

    public void addActionListener(ActionListener arg0) {
        //
    }

    public void removeActionListener(ActionListener arg0) {
       //
    }

    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    public PropertyEditor getPropertyEditor() {
        return this.editor;
    }

    public PropertyModel getPropertyModel() {
        return this.model;
    }

    public void setPropertyModel(PropertyModel model) {
        this.model = model;
    }

    public boolean isKnownComponent(Component component) {
        return component == picker || picker.isAncestorOf(component);
    }

}
