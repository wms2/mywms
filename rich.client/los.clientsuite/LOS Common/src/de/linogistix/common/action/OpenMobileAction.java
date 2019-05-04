/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.action;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author trautm
 */
public class OpenMobileAction extends SystemAction {
    private static final Logger log = Logger.getLogger(OpenMobileAction.class.getName());
    private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,
        Role.INVENTORY_STR,
        Role.FOREMAN_STR,
        Role.OPERATOR_STR
    };

     
    @Override
    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "OpenMobileAction.name");
    }

    @Override
    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Action.png";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean enable(Node[] activatedNodes) {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(allowedRoles);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        String hostName = loc.getHostName();
        int end = hostName.indexOf(":");
        if( end > 0 )
            hostName = hostName.substring(0, end);

        String urlName = "http://" + hostName + ":8080/los-mobile";
        log.info("Start URL: " + urlName);
        
        URL url;
        try {
            url = new URL(urlName);
        } catch (MalformedURLException ex) {
            NotifyDescriptor d = new NotifyDescriptor.Message(
                    NbBundle.getMessage(CommonBundleResolver.class, "FAILURE") + ": (" + ex.getLocalizedMessage() + ")",
                    NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return;
        }
        URLDisplayer.getDefault().showURL(url);
    }
}
