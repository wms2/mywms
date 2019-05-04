/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reference.about;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.openide.util.NbBundle;

/**
 * Action which shows StockTakingTopComponent component.
 */
public class AboutBoxAction extends AbstractAction {


    public AboutBoxAction() {
        super(NbBundle.getMessage(AboutBox.class, "AboutBox.ActionName"));
        setEnabled(true);
    }

    public void actionPerformed(ActionEvent evt) {
        JDialog dialog = new AboutBox(null,true);
        dialog.setVisible(true);
    }
}
