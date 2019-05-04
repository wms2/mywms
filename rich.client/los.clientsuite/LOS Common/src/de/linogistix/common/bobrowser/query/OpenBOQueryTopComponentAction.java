/*
 * OpenBOQueryTopComponentAction.java
 *
 * Created on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.api.BOQueryTopComponentLookup;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class OpenBOQueryTopComponentAction extends NodeAction {

    protected final static Logger log = Logger.getLogger(OpenBOQueryTopComponentAction.class.getName());
    private String[] roles = new String[]{
        Role.ADMIN.toString(),
        Role.OPERATOR.toString(),
        Role.FOREMAN.toString(),
        Role.INVENTORY.toString(),
        Role.CLEARING.toString()
    };
    private String rolesWarnLog = "Roles authorisation failed. Nodes would be disabled";

    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length != 1)) {
            //System.out.println("--> BONode " + node.length);
            log.warning(rolesWarnLog);
            return false;
        }
        return checkRoles();
    }

    /**
     * Checks whether the logged in user is allowed to see this node.
     */
    public boolean checkRoles() {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getRoles());
        if (result == false) {
            log.warning(rolesWarnLog);
        }
        return result;
    }

    protected void performAction(Node[] node) {
        BONode n;
        if (node.length == 1) {
            try {
                n = (BONode) node[0];
            } catch (ClassCastException ex) {
                throw new RuntimeException("Unexpected node: " + node[0]);
            }
        } else {
            throw new RuntimeException("Expected 1 key but got " + node.length);
        }

        try {
            
            BOQueryTopComponentLookup tclookup = Lookup.getDefault().lookup(BOQueryTopComponentLookup.class);
            BOQueryTopComponent win = tclookup.findInstance(n);
            
            Mode mode = WindowManager.getDefault().findMode("editor");
            
            if(mode != null){
                mode.dockInto(win);
            }
            
            win.open();
            win.requestActive();

        } catch (Throwable t) {
            ErrorManager em = ErrorManager.getDefault();
            //em.annotate(t, t.getMessage());
            em.notify(t);
        }

    }

    public HelpCtx getHelpCtx() {
        return null;
    }

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "query");
    }

    protected boolean asynchronous() {
        return false;
    }

    public String[] getRoles() {
        return roles;
    }
}


