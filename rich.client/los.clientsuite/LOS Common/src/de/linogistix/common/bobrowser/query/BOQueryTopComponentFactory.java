/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.api.BOQueryTopComponentLookup;
import de.linogistix.common.bobrowser.bo.BONode;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.ErrorManager;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author trautm
 */
public class BOQueryTopComponentFactory implements BOQueryTopComponentLookup{

    private static Logger log = Logger.getLogger(BOQueryTopComponentFactory.class.getName()); 
    
    public BOQueryTopComponent findInstance(BONode node) {
        log.log(Level.FINE, "Looking for " + node.getName());
        
        TopComponent win = WindowManager.getDefault().findTopComponent(node.getName());
        
        if (win == null) {
            BOQueryTopComponent c = new BOQueryTopComponent(node, true);
            c.setPreferredId(WindowManager.getDefault().findTopComponentID(c));
            return c;
        }
        if (win instanceof BOQueryTopComponent) {
            return (BOQueryTopComponent) win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + node.getName() + "' ID. That is a potential source of errors and unexpected behavior.");
        return new BOQueryTopComponent(node, true);
    }

}
