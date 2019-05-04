/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.action;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import org.netbeans.api.javahelp.Help;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author trautm
 */
public class OpenHelpAction extends SystemAction {
    
     static final String ICON_PATH = "de/linogistix/common/res/icon/help-browser.png";
     
     public OpenHelpAction(){
       //dgrys portierung 
        //putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(OpenHelpAction.ICON_PATH, true)));
        //setIcon(new ImageIcon(Utilities.loadImage(OpenHelpAction.ICON_PATH, true)));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(OpenHelpAction.ICON_PATH, true)));
        setIcon(new ImageIcon(ImageUtilities.loadImage(OpenHelpAction.ICON_PATH, true)));
        setEnabled(true);
     }
     
    @Override
    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "OpenHelpAction.name");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("de.linogistix.common.about");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        String id = "de.linogistix.common.about";
        Help help = (Help) Lookup.getDefault().lookup(Help.class);

        if (help != null && help.isValidID(id, true).booleanValue()) {
            help.showHelp(new HelpCtx(id));
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

    }
}
