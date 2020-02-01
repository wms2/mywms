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
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.AdviceFacade;
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.advice.AdviceLine;
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

public final class BOAdviceRemoveAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOAdviceRemoveAction.class.getName());
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
        boolean ret = false;
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        ret =  login.checkRolesAllowed(allowedRoles);
        
        return ret;
    }

    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }

        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.ReallyRemove"),
                NbBundle.getMessage(InventoryBundleResolver.class, "remove"),
                NotifyDescriptor.OK_CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }
        
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            AdviceFacade facade = loc.getStateless(AdviceFacade.class);
            
            for (Node n : activatedNodes) {
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    log.warning("Not a BOMasterNodeType: " + n.toString());
                }
                BODTO<AdviceLine> adviceTo = (((BOMasterNode)n).getEntity());
                facade.removeAdvise(adviceTo);
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
        
        if( activatedNodes.length>0 ) {
            Node n = activatedNodes[0];
            if (n instanceof BOMasterNode) {
                BOMasterNode ma = (BOMasterNode)n;
                BO bo = ma.getBo();
                bo.fireOutdatedEvent(ma);
            }
        }
    }
}

