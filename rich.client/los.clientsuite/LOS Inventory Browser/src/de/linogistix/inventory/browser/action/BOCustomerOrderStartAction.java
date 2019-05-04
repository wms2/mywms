/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.dialog.CustomerOrderStartWizard;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import org.mywms.facade.FacadeException;
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
 * @author krane
 *
 */
public final class BOCustomerOrderStartAction extends NodeAction {

    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),Role.INVENTORY_STR,Role.FOREMAN_STR
    };


    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "start");
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

        for (Node n : activatedNodes) {
            if (n == null) {
                continue;
            }
            if (!(n instanceof BOMasterNode)) {
                continue;
            }
            BODTO bodto = ((BOMasterNode)n).getEntity();
            if( bodto instanceof LOSCustomerOrderTO ) {
                LOSCustomerOrderTO order = (LOSCustomerOrderTO)bodto;
                if( order.getState()>=State.PICKED ) {
                    return false;
                }
            }
            else {
                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                LOSCustomerOrderQueryRemote orderQuery;
                LOSCustomerOrder r;
                try {
                    orderQuery = loc.getStateless(LOSCustomerOrderQueryRemote.class);
                    r = orderQuery.queryById(((BOMasterNode)n).getEntity().getId());
                } catch (Exception e) {
                    return false;
                }
                if( r.getState()>=State.PICKED ) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes == null) {
            return;
        }

        CustomerOrderStartWizard wizard = null;
        BOMasterNode node = null;
        try {

            List<LOSCustomerOrderTO> orderList = new ArrayList<LOSCustomerOrderTO>();
            for (Node n : activatedNodes) {
                if( n == null || !(n instanceof BOMasterNode)) {
                    continue;
                }
                node = (BOMasterNode)n;
                LOSCustomerOrderTO order = (LOSCustomerOrderTO)node.getEntity();
                orderList.add(order);
            }
            wizard = new CustomerOrderStartWizard(orderList);
            Dialog d = DialogDisplayer.getDefault().createDialog(wizard);
            d.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            d.setVisible(true);

            if( !wizard.getValue().equals(NotifyDescriptor.OK_OPTION) ) {
                return;
            }


            CursorControl.showWaitCursor();

            LOSPickingFacade pickingFacade;
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            try {
                pickingFacade = (LOSPickingFacade) loc.getStateless(LOSPickingFacade.class);
            } catch (J2EEServiceLocatorException ex) {
                ExceptionAnnotator.annotate(ex);
                return;
            }

            try {
                if( wizard.createOne ) {
                    List<Long> orderIdList = new ArrayList<Long>();
                    for( LOSCustomerOrderTO order : orderList ) {
                        orderIdList.add(order.getId());
                    }
                    pickingFacade.createOrders(orderIdList, true, wizard.prio, wizard.destinationName, wizard.release, wizard.userName, wizard.hint );
                }
                else {
                    for( LOSCustomerOrderTO order : orderList ) {
                        pickingFacade.createOrders(order.getId(), true, wizard.createOnePerOrder, wizard.createStrat, wizard.prio, wizard.destinationName, wizard.release, wizard.userName, wizard.hint );
                    }
                }
            } catch (FacadeException ex) {
                ExceptionAnnotator.annotate(ex);
                return;
            }

        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
        
        if( node != null && wizard != null ) {
            BO bo = node.getBo();
            bo.fireOutdatedEvent(node);
        }

    }
}

