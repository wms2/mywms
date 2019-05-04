/*
 * BOEditAction.java
 *
 * Editd on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.crud.gui.component.BOLockWizard;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;

import de.linogistix.los.query.BODTO;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLockAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOLockAction.class.getName());
   private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),
        Role.FOREMAN_STR.toString(),
        Role.INVENTORY.toString()
    };

    protected boolean enable(Node[] node) {
        boolean ret = false;

        if ((node == null) || (node.length == 0)) {
            //System.out.println("--> BONode " + node.length);
            return false;
        }

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        if (!login.checkRolesAllowed(allowedRoles)) {
            return false;
        }
        
        return true;
    }

    protected void performAction(Node[] node) {

        try {
            BOQueryNode parent = (BOQueryNode) (node[0].getParentNode()); 
            BO bo = parent.getModel().getBoNode().getBo(); 
            BusinessObjectCRUDRemote crud = bo.getCrudService();
            BOLockWizard w = new BOLockWizard(bo);
            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);

            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                CursorControl.showWaitCursor();
                try {
                    List<BODTO<BasicEntity>> l = new ArrayList();
                    for (Node n : node) {
                        l = new ArrayList();
                        if (n == null) {
                            continue;
                        }
                        if (!(n instanceof BOMasterNode)) {
                            log.warning("Not a BOMasterNodeType: " + n.toString());
                        }
                        l.add(((BOMasterNode) n).getEntity());
                        BasicEntity entity = parent.update(((BOMasterNode) n).getEntity().getId());
                        crud.lock(
                                entity,
                                w.getLock(), 
                                w.getLockCause());
                    }
                } catch (Throwable t) {
                    ExceptionAnnotator.annotate(t);
                } finally {
                    CursorControl.showNormalCursor();
                }
                
                if( node.length>0 ) {
                    Node n = node[0];
                    if (n instanceof BOMasterNode) {
                        BOMasterNode ma = (BOMasterNode)n;
                        BO bo1 = ma.getBo();
                        bo1.fireOutdatedEvent(ma);
                    }
                }
            }
        } catch (Throwable t) {
               ExceptionAnnotator.annotate(t);
        }
    }
      

     public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "lockEntity");
    }

    protected boolean asynchronous() {
        return false;
    }
}


