/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.changeamount;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.Dialog;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.WindowManager;

public final class BOStockUnitChangeAmountAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOStockUnitChangeAmountAction.class.getName());
    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(), Role.FOREMAN_STR, Role.INVENTORY.toString()
    };

    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "changeAmount");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Action.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(allowedRoles);
    }

    protected void performAction(Node[] activatedNodes) {

        if (activatedNodes == null || activatedNodes.length==0) {
            return;
        }
        try {
            if (!(activatedNodes[0] instanceof BOMasterNode)) {

                log.warning("Not a BOMasterNodeType: ");
                return;
            }
            BOMasterNode n = (BOMasterNode) activatedNodes[0];
            
            ChangeAmountWizard w = new ChangeAmountWizard(n.getEntity());
            
            Dialog d= DialogDisplayer.getDefault().createDialog(w);
            d.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            d.setVisible(true);

            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                CursorControl.showWaitCursor();

                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                ManageInventoryFacade m = loc.getStateless(ManageInventoryFacade.class);
                m.changeAmount(w.getSu(), w.getAmount(), w.getReserveAmount(), w.getPackagingUnit(), w.getInfo());

                BO bo = n.getBo();
                bo.fireOutdatedEvent(n);

            }
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

