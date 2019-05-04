/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.unitloadtransfer;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.Dialog;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.WindowManager;

public final class BOUnitLoadTransferAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOUnitLoadTransferAction.class.getName());
   private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR
    };

    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "transferUnitLoad");
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

        BasicEntity e;

        if (activatedNodes == null || activatedNodes.length < 1 ) {
            return;
        }
        Node node = activatedNodes[0];
        if (!(node instanceof BOMasterNode)) {
            log.warning("Not a BOMasterNodeType: " + node.toString());
            return;
        }
        BODTO to = ((BOMasterNode)node).getEntity();
        if( actionPerformed(to) ) {
            BOMasterNode ma = (BOMasterNode)node;
            BO bo = ma.getBo();
            bo.fireOutdatedEvent(ma);
        }

    }
  
    public boolean actionPerformed(BODTO unitLoadTo) {
        UnitLoadTransferWizard w;
        try {
            w = new UnitLoadTransferWizard(unitLoadTo);
        } catch (InstantiationException ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        return transferUnitLoad(w);
    }

    private boolean transferUnitLoad(UnitLoadTransferWizard w) {
        boolean redo = true;
        while (redo) {
            try {

                Dialog d = DialogDisplayer.getDefault().createDialog(w);
                d.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
                d.setVisible(true);

                if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                    CursorControl.showWaitCursor();
                    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                    ManageInventoryFacade m = loc.getStateless(ManageInventoryFacade.class);
                    m.transferUnitLoad(w.getStorageLocationTO(), w.getUnitLoadTO(), -1, w.isIgnoreLock(), w.getHint());
                    CursorControl.showNormalCursor();
                    return true;
                }

                redo = false;

            } catch (FacadeException ex) {
                ExceptionAnnotator.annotate(ex);
                UnitLoadTransferWizard tmp;
                try {
                    tmp = new UnitLoadTransferWizard(null);
                    tmp.setHint(w.getHint());
                    tmp.setStorageLocationTO(w.getStorageLocationTO());
                    tmp.setUnitLoadTO(w.getUnitLoadTO());
                    tmp.setIgnoreLock(w.isIgnoreLock());
                    w = tmp;
                } catch (InstantiationException ex1) {
                    ExceptionAnnotator.annotate(ex);
                    redo = false;
                }
                redo = true;
            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
                redo = false;
            }
        }
        CursorControl.showNormalCursor();
        return false;
    }
}

