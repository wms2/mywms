/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.crud;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
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

public final class BODeleteAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BODeleteAction.class.getName());

    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString()
    };

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "delete");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Delete.png";
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

        if (activatedNodes == null || activatedNodes.length < 1) {
            return;
        }

        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(CommonBundleResolver.class, "NotifyDescriptor.ReallyDelete"),
                NbBundle.getMessage(CommonBundleResolver.class, "Delete"),
                NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        try {
            CursorControl.showWaitCursor();

            for (Node node : activatedNodes) {
                if (node == null) {
                    continue;
                }
                BO bo;
                Long id;
                if (node instanceof BOEntityNodeReadOnly) {
                    BOEntityNodeReadOnly entityNode = (BOEntityNodeReadOnly) node;
                    BOLookup lookup = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
                    bo = (BO) lookup.lookup(entityNode.getBo().getClass());
                    id = entityNode.getBo().getId();
                } else if (node instanceof BOMasterNode) {
                    BOMasterNode masterNode = (BOMasterNode) node;
                    bo = masterNode.getBo();
                    id = masterNode.getId();
                } else {
                    return;
                }
                BusinessObjectCRUDRemote crud = bo.getCrudService();
                BusinessObjectQueryRemote query = bo.getQueryService();

                BasicEntity entity = query.queryById(id);
                crud.delete(entity);
                bo.fireOutdatedEvent(node);
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }

    }

}
