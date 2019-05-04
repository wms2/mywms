/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.query.*;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.logging.Logger;
import javax.swing.Action;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class RefreshBOBeanNodeAction extends NodeAction {

    private static final Logger log = Logger.getLogger(RefreshBOBeanNodeAction.class.getName());

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "refresh");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Reload.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
//        log.log(Level.INFO, "################### enable");
        return true;

    }

    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

//        log.log(Level.INFO, "########## refreshed");

        if (activatedNodes == null) {
            return;
        }

        for (Node n : activatedNodes) {
            if (n == null) {
                continue;
            }

            if (n.getParentNode() instanceof BOQueryNode) {
                BOQueryNode parent = (BOQueryNode) n.getParentNode();
                BusinessObjectQueryRemote service = parent.getModel().getBoNode().getQueryService();
                try {
                    if (n instanceof BOEntityNodeReadOnly) {
                        BOEntityNodeReadOnly b = (BOEntityNodeReadOnly) n;
                        parent.update(b.getBo().getId());
                    } else if (n instanceof BOMasterNode) {
                        BOMasterNode b = (BOMasterNode) n;
                        parent.update(b.getId());
                    }
                } catch (FacadeException ex) {
                    ExceptionAnnotator.annotate(ex);
                }
            }

        }
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return super.createContextAwareInstance(actionContext);
    }
}

