/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 *
 * @author artur
 */
class UI extends MetalComboBoxUI {
        private CommonObject co;

        public UI(CommonObject co) {
            this.co = co;
        }
    
        @Override
        protected JButton createArrowButton() {
            JButton button = super.createArrowButton();
            button.setVisible(false);
            return button;
        }

        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {

                @Override
                public void show() {
                    if (co.isAllowPopup()) {
                        super.show();
                    }
                }

                @Override
                public void hide() {
                    super.hide();
                }
            };
            return popup;
        }


}
