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
package de.linogistix.common.bobrowser.crud;

import de.linogistix.common.bobrowser.crud.gui.object.BOEditNode;
import de.linogistix.common.bobrowser.crud.gui.component.BOEditWizard;
import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import java.awt.Dialog;
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

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOEditAction extends NodeAction {

    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),
        Role.CLIENT_ADMIN.toString(),
        Role.INVENTORY.toString(),
        Role.OPERATOR.toString()
    };

    protected boolean enable(Node[] node) {

        if ((node == null) || (node.length != 1)) {
            //System.out.println("--> BONode " + node.length);
            return false;
        }

        if ((node[0] instanceof BOEntityNodeReadOnly)) {
            // can handle
        } else if ((node[0] instanceof BOMasterNode)) {
            // can handle
        } else {
            return false;
        }

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(getAllowedRoles());

    }

    protected void performAction(Node[] node) {


        BO bo;
        BasicEntity e;
        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
        
        if (node.length != 1) {
            FacadeException fex = new FacadeException("No entry selected",
                    "BoEditAction.NO_ENTRY_SELECTED",
                    new Object[]{});
            fex.setBundleResolver(de.linogistix.common.res.CommonBundleResolver.class);
            ExceptionAnnotator.annotate(fex);
            return;
        }
        try {
            if (node[0] instanceof BOEntityNodeReadOnly) {
                BOEntityNodeReadOnly n;
                n = (BOEntityNodeReadOnly) node[0];
                BOQueryNode parent = (BOQueryNode) (n.getParentNode());
                bo = (BO) l.lookup(n.getBo().getClass());
                e = n.getBo();
            } else if (node[0] instanceof BOMasterNode) {
                BOMasterNode n;
                n = (BOMasterNode) node[0];
                BOQueryNode parent = (BOQueryNode) (n.getParentNode());
                bo = (BO) l.lookup(n.getBo().getClass());
                e = parent.update(n.getId());               
            } else {
                throw new RuntimeException("Unexpected node: " + node[0]);
            }
            BOEditWizard w = new BOEditWizard(new BOEditNode(e));
            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);
            
            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                BusinessObjectCRUDRemote crud = bo.getCrudService();
                crud.update(w.getNode().getBo());
                bo.fireOutdatedEvent(w.getNode());
            }

        } catch  (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
        }

    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "edit");
    }

    protected boolean asynchronous() {
        return false;
    }
    
    public String[] getAllowedRoles(){
        return allowedRoles;
    }
}


