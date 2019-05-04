/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.carriertransfer;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author krane
 */
public final class BOCarrierTransferAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOCarrierTransferAction.class.getName());
   private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR
    };

    public String getName() {
        return NbBundle.getMessage(WMSProcessesBundleResolver.class, "Carrier.ActionName");
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
        if( new CarrierTransferAction().actionPerformed(to) ) {
            BOMasterNode ma = (BOMasterNode)node;
            BO bo = ma.getBo();
            bo.fireOutdatedEvent(ma);
        }

    }
}

