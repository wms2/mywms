/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.gui.object.BOQueryByTemplateWrapper;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 * An {@link InplaceEditor} for {@link BOQueryByTemplateProperty}
 *
 * @author trautm
 */
public class BOQueryByInplaceEditor implements InplaceEditor {

    private static final Logger log = Logger.getLogger(BOQueryByInplaceEditor.class.getName());
    private BOQueryByInplacePanel panel;
    private BOQueryByEditor ed;
    private PropertyEnv env;
    PropertyModel model;

    public void addActionListener(java.awt.event.ActionListener al) {
    //
    }

    public void clear() {

        if (this.ed != null){
            this.ed.removePropertyChangeListener(this.panel);
        }
        this.panel = null;
        this.env = null;
        this.ed = null;
        this.model = null;
    }

    public void connect(java.beans.PropertyEditor pe, PropertyEnv env) {
        if (this.ed == pe) {
            return;
        }

        this.ed = (BOQueryByEditor) pe;
        this.env = env;

        this.panel = new BOQueryByInplacePanel((BOQueryByTemplateWrapper) this.ed.getValue());
        this.ed.addPropertyChangeListener(this.panel);
        reset();
    }

    public javax.swing.JComponent getComponent() {
        if (this.panel == null) {
            return new JTextField();
        }

        return this.panel;
    }

    public KeyStroke[] getKeyStrokes() {
        return new KeyStroke[0];
    }

    public java.beans.PropertyEditor getPropertyEditor() {
        return this.ed;
    }

    public PropertyModel getPropertyModel() {
        return model;
    }

    public Object getValue() {
        BOQueryByTemplateWrapper ret;
        ret = this.panel.getValue();
        log.info("+++" + ret);
        return ret;
    }

    public boolean isKnownComponent(java.awt.Component c) {
        return ((c == panel) || panel.isAncestorOf(c));
    }

    public void removeActionListener(java.awt.event.ActionListener al) {
    //
    }

    public void reset() {

        this.panel.invalidate();
        this.panel.validate();
    }

    public void setPropertyModel(PropertyModel pm) {
        this.model = pm;
    }

    public void setValue(Object o) {
        this.ed.setValue(o);
    }

    public boolean supportsTextEntry() {
        return false;
    }
//  void updateInner(){
//    this.panel.updateInner();
//  }
}
