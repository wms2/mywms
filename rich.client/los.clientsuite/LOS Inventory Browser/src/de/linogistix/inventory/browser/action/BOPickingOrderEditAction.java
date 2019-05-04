/*
 * Copyright (c) 2009 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.dialog.PickingOrderEditDialog;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSPickingOrderTO;
import de.linogistix.los.model.State;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author krane
 */
public final class BOPickingOrderEditAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOPickingOrderEditAction.class.getName());
    J2EEServiceLocator loc = null;
    LOSPickingOrderQueryRemote orderQuery = null;
    LOSPickingFacade pickingFacade = null;


   private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),Role.INVENTORY_STR,Role.FOREMAN_STR
   };

    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "PickingOrderEditDialog.action");
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
        boolean allowed =  login.checkRolesAllowed(allowedRoles);
        if( !allowed ) {
            return false;
        }

        try {
            for (Node n : activatedNodes) {
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    continue;
                }

                LOSPickingOrderTO order = (LOSPickingOrderTO)((BOMasterNode)n).getEntity();
                if( order.getState()>=State.STARTED ) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes == null) {
            return;
        }

        PickingOrderEditDialog dialog = null;
        LOSPickingOrderTO to = null;
        BOMasterNode node = null;
        try {
            List<LOSPickingOrderTO> orders = new ArrayList<LOSPickingOrderTO>();
            for (Node n : activatedNodes) {
                if( n == null || !(n instanceof BOMasterNode)) {
                    continue;
                }

                node = (BOMasterNode)n;
                to = (LOSPickingOrderTO)node.getEntity();
                orders.add(to);
            }

            dialog = new PickingOrderEditDialog(orders);
            dialog.setVisible(true);

        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }

        if( node != null && dialog != null && dialog.isChanged() ) {
            BO bo = node.getBo();
            bo.fireOutdatedEvent(node);
        }

    }
}

