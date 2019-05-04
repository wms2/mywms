/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.windows;

import de.linogistix.common.gui.gui_builder.windows.AbstractAppModalDialog;
import java.awt.Frame;
import org.openide.windows.WindowManager;

/**
 *
 * @author artur
 */
public class AppModalDialog extends AbstractAppModalDialog {

    private static AppModalDialog instance = null;
    
    /**
     * To defeat instantiation
     * @param parent
     * @param modal
     */
    private AppModalDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    
    public synchronized static AppModalDialog getInstance() {
        if (instance == null) {
            instance = new AppModalDialog(WindowManager.getDefault().getMainWindow(), true);
        }
        return instance;
    }
}
