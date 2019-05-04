/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import javax.swing.JTextField;

/**
 *
 * @author artur
 */
class UITextField extends JTextField {

    CommonObject co;

    public UITextField(CommonObject co) {
        super();
        this.co = co;
    }

    public UITextField(int columns, CommonObject co) {
        super(columns);
        this.co = co;
    }

    @Override
    public void setText(String t) {
        if (co.isEditing()) {
            super.setText(t);
        }
    }
}

