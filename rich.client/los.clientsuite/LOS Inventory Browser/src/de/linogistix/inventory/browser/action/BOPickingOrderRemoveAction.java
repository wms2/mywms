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
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.query.dto.LOSPickingOrderTO;
import de.linogistix.los.model.State;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author krane
 */
public final class BOPickingOrderRemoveAction extends NodeAction {

    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),Role.INVENTORY_STR,Role.FOREMAN_STR
    };



    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "remove");
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
                if( order.getState()<State.FINISHED ) {
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
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.Remove.text"),
                NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.Remove.title"),
                NotifyDescriptor.OK_CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        BOMasterNode node = null;
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LOSPickingFacade pickingFacade = loc.getStateless(LOSPickingFacade.class);

            for (Node n : activatedNodes) {
                if( n == null || !(n instanceof BOMasterNode)) {
                    continue;
                }
                node = (BOMasterNode)n;
                LOSPickingOrderTO order = (LOSPickingOrderTO)node.getEntity();
                pickingFacade.removeOrder(order.getId());
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
        
        if( node != null ) {
            BO bo = node.getBo();
            bo.fireOutdatedEvent(node);
        }

    }
}

