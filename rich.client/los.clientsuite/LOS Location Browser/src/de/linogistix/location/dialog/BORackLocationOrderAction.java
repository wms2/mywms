/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.location.dialog;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.los.location.facade.ManageLocationFacade;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.location.StorageLocation;
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

/**
 *
 * @author krane
 */
public final class BORackLocationOrderAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BORackLocationOrderAction.class.getName());
   private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR
    };

    public String getName() {
        return NbBundle.getMessage(LocationBundleResolver.class, "LocationOrderWizard.name");
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

        if (activatedNodes == null || activatedNodes.length < 1 ) {
            return;
        }
        Node node = activatedNodes[0];
        if (!(node instanceof BOMasterNode)) {
            log.warning("Not a BOMasterNodeType: " + node.toString());
            return;
        }

        BODTO<StorageLocation> to = ((BOMasterNode)node).getEntity();
        String rackName = "";

        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        try {
            LOSStorageLocationQueryRemote orderQuery = loc.getStateless(LOSStorageLocationQueryRemote.class);
            StorageLocation location = orderQuery.queryById(((BOMasterNode)node).getEntity().getId());
            rackName = location.getRack();
        } catch (Exception e) {
            return;
        }


        LocationOrderWizard wizard;
        try {
            wizard = new LocationOrderWizard(rackName);
        } catch (InstantiationException ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        }


        try {

            Dialog d = DialogDisplayer.getDefault().createDialog(wizard);
            d.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            d.setVisible(true);

            if (wizard.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                NotifyDescriptor msg = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(LocationBundleResolver.class, "LocationOrderWizard.msgStart", new Object[]{wizard.rack}),
                    NotifyDescriptor.YES_NO_OPTION);
                
                if( DialogDisplayer.getDefault().notify(msg) == NotifyDescriptor.YES_OPTION) {
                    CursorControl.showWaitCursor();
                    ManageLocationFacade facade = loc.getStateless(ManageLocationFacade.class);
                    int indexMax = facade.setLocationOrderIndex(wizard.rack, wizard.valueStart, wizard.valueDiff);

//                    msg = new NotifyDescriptor.Message(
//                        NbBundle.getMessage(LocationBundleResolver.class, "LocationOrderWizard.msgDone", indexMax),
//                        NotifyDescriptor.INFORMATION_MESSAGE);
//                    DialogDisplayer.getDefault().notify(msg);
                }
            }
        }
        catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }

        CursorControl.showNormalCursor();


        BOMasterNode ma = (BOMasterNode)node;
        BO bo = ma.getBo();
        bo.fireOutdatedEvent(ma);
    }

}

