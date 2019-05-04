/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.location.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.location.facade.ManageLocationFacade;
import de.linogistix.los.location.model.LOSStorageLocation;
import java.util.ArrayList;
import java.util.List;
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

public final class BOStorageLocationReleaseAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOStorageLocationReleaseAction.class.getName());
   private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),
        Role.FOREMAN.toString(), Role.INVENTORY_STR
    };

    public String getName() {
        return NbBundle.getMessage(LocationBundleResolver.class, "release");
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

        if (activatedNodes == null) {
            return;
        }

        try {
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(LocationBundleResolver.class, "NotifyDescriptor.ReallyRelease"),
                    NbBundle.getMessage(LocationBundleResolver.class, "release"),
                    NotifyDescriptor.OK_CANCEL_OPTION);

            if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
                return;
            }

            List<BODTO<LOSStorageLocation>> l = new ArrayList();
            for (Node n : activatedNodes) {
                l = new ArrayList();
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    log.warning("Not a BOMasterNodeType: " + n.toString());
                }
                l.add(((BOMasterNode)n).getEntity());
                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                ManageLocationFacade m = loc.getStateless(ManageLocationFacade.class);
                m.releaseReservations(l);
            }

            if( activatedNodes.length>0 ) {
                Node n = activatedNodes[0];
                if (n instanceof BOMasterNode) {
                    BOMasterNode ma = (BOMasterNode)n;
                    BO bo = ma.getBo();
                    bo.fireOutdatedEvent(ma);
                }
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

