/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxEditor;

/**
 *
 * @author artur
 */
public final class UIComboBoxEditor implements ComboBoxEditor {


        UIPanel panel;
        CommonObject co;
        
        public UIComboBoxEditor(CommonObject co, UIPanel panel) {
            this.co = co;
            this.panel = panel;
        }

        /**
         * 
         * @return editor
         */
        public Component getEditorComponent() {
            return panel;
        }

        /**
         * Here, you can handle the textfield edit entry, which will be shown to 
         * the user
         * @param item
         */
        public void setItem(Object item) {
            if (co.isAllowSetText()) {
                return;
            }
            //needed by mousepressed in the combobox popup
            String newText = (item == null) ? "" : item.toString();
            panel.textField.setText(newText);
        }

        /**
         * 
         * @return item
         */
        public Object getItem() {
            return panel.textField.getText();
        }

        /**
         * select all text
         */
        public void selectAll() {
            panel.textField.selectAll();
        }

        /**
         * 
         * @param l
         */
        public void addActionListener(ActionListener l) {
            panel.textField.addActionListener(l);
        }

        /**
         * 
         * @param l
         */
        public void removeActionListener(ActionListener l) {
            panel.textField.removeActionListener(l);
        }
    
}
