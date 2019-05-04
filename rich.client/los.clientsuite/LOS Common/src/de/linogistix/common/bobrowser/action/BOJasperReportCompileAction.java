/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;

import de.linogistix.los.common.facade.LOSJasperReportFacade;

import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * @author krane
 *
 */
public final class BOJasperReportCompileAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOJasperReportCompileAction.class.getName());
    private static String[] allowedRoles = new String[]{Role.ADMIN_STR};

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "BOLOSJasperReportCompileAction.name");
    }

    @Override
    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Pdf.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        boolean ret = true;

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        if (!login.checkRolesAllowed(allowedRoles)) {
            return false;
        }
        
        return ret;
    }

    protected void performAction(Node[] activatedNodes) {

        if (activatedNodes == null) {
            return;
        }

        CursorControl.showWaitCursor();

        try {
            
            BOMasterNode node = null;

            for (Node n : activatedNodes) {
                
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    log.warning("Not a BOMasterNodeType: " + n.toString());
                    continue;
                }
                node = (BOMasterNode)n;

                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                LOSJasperReportFacade facade = loc.getStateless(LOSJasperReportFacade.class);
                facade.compileReport( node.getId() );

            }

            if( node != null ) {
                BO bo = node.getBo();
                bo.fireOutdatedEvent(node);
            }

        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

