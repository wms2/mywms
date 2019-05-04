/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author artur
 */
class UIPopup extends JPopupMenu {

    public UIPopup(ActionListener listener) {
        JMenuItem mi = new JMenuItem("TestIt ");
        mi.addActionListener(listener);
        add(mi);
        for (int i = 0; i < 3; i++) {
            mi = new JMenuItem("Test " + Integer.toString(i));
            mi.addActionListener(listener);
            add(mi);
        }
    }
}
