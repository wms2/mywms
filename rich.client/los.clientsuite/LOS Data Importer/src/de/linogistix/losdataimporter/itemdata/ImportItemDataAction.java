/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.losdataimporter.itemdata;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.losdataimporter.res.BundleResolver;
import java.awt.Dialog;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ImportItemDataAction extends CallableSystemAction {

    private J2EEServiceLocator loc;
    
    private LoginService login;
    
    private static final String[] allowedRoles = new String[]{
        Role.ADMIN_STR
    };
    
    public ImportItemDataAction(){
  
        login = Lookup.getDefault().lookup(LoginService.class);

    }
    
    @Override
    public boolean isEnabled() {
 
        if (login == null) return false;
        
        return login.checkRolesAllowed(allowedRoles);
        
    }

    
    
    public void performAction() {

        ImportItemDataWizard w;
        try {
            w = new ImportItemDataWizard(null);
        } catch (InstantiationException ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        }

        try {

            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);


        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }

    public String getName() {
        return NbBundle.getMessage(BundleResolver.class, "CTL_DataImportItemData");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
