/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.create_avis;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import org.mywms.globals.Role;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/**
 * Action which shows CreateAvis component.
 */
public class CreateAvisAction extends SystemAction {

  public String[] getAllowedRoles() {
    return new String[]{Role.ADMIN_STR, Role.FOREMAN_STR, Role.INVENTORY_STR};
  }

  @Override
  public String getName() {
    return NbBundle.getMessage(WMSProcessesBundleResolver.class, "CTL_CreateAvisAction");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {
    TopComponent win = CreateAvisTopComponent.findInstance();
    win.open();
    win.requestActive();
  }

  @Override
  public boolean isEnabled() {
    LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
    boolean result = login.checkRolesAllowed(getAllowedRoles());
    return result;
  }

}
