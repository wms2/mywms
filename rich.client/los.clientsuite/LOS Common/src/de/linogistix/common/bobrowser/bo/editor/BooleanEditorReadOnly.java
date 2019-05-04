/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.common.bobrowser.bo.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author andreas
 */
public class BooleanEditorReadOnly  extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory  {

        public BooleanEditorReadOnly(){
            super();
        }


        @Override
        public void setAsText(String s) {
            setValue(new Boolean(s));
        }

        @Override
        public String getAsText() {
            Boolean type = ((Boolean) getValue());
            if (type != null) {
                return "" + type;
            }
            return "-";
        }

        @Override
        public String[] getTags() {
            return new String[] {};
        }

        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
        }

        private InplaceEditor ed = null;

        public InplaceEditor getInplaceEditor() {
            if (ed == null) {
                ed = new Inplace();
            }
            return ed;
        }

        private static class Inplace implements InplaceEditor, ActionListener {

            private JCheckBox checkbox = new JCheckBox();

            private PropertyEditor editor = null;

            private PropertyModel model;

            private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

            public Inplace(){
                super();
            }


            public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
                editor = propertyEditor;
                reset();
            }

            public JComponent getComponent() {
                return checkbox;
            }

            public void clear() {
                editor = null;
                model = null;
            }

            @Override
            public void addActionListener(ActionListener actionListener) {
                actionListeners.add(actionListener);
            }

            @Override
            public KeyStroke[] getKeyStrokes() {
                return new KeyStroke[0];
            }

            @Override
            public PropertyEditor getPropertyEditor() {
                return editor;
            }

            @Override
            public PropertyModel getPropertyModel() {
                return model;
            }

            @Override
            public Object getValue() {
                return checkbox.isSelected();
            }

            @Override
            public boolean isKnownComponent(Component component) {
                return component == checkbox || checkbox.isAncestorOf(component);
            }

            @Override
            public void removeActionListener(ActionListener actionListener) {
                this.actionListeners.remove(actionListener);

            }

            @Override
            public void reset() {
                Boolean selected = (Boolean) editor.getValue();

                if (selected != null) {
                    checkbox.setSelected(selected);
                }
            }

            @Override
            public void setPropertyModel(PropertyModel propertyModel) {
                this.model = propertyModel;

            }

            @Override
            public void setValue(Object selected) {
                this.checkbox.setSelected((Boolean) selected);

            }

            @Override
            public boolean supportsTextEntry() {
                return false;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Boolean) this.getValue()) {
                    this.setValue(false);
                } else {
                    this.setValue(true);
                }

            }

        }
    }

