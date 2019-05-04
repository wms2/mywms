/*
 * OpenBOQueryTopComponentAction.java
 *
 * Created on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.los.inventory.facade.LOSReplenishFacade;
import java.awt.event.ActionEvent;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas
 * Trautmann</a>
 */
public class ReplenishIfNeededAction extends SystemAction {

  public String[] getAllowedRoles() {
    return new String[]{Role.ADMIN.toString(), Role.INVENTORY_STR, Role.FOREMAN_STR};
  }

  @Override
  public String getName() {
    return NbBundle.getMessage(CommonBundleResolver.class, "createReplenishmentIfNeeded");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {

    CursorControl.showWaitCursor();
    try {

      NotifyDescriptor d = new NotifyDescriptor.Confirmation(
              NbBundle.getMessage(CommonBundleResolver.class, "NotifyDescriptor.ReallyReplenishIfNeeded"),
              NbBundle.getMessage(CommonBundleResolver.class, "createReplenishmentIfNeeded"),
              NotifyDescriptor.OK_CANCEL_OPTION);

      if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);

        LOSReplenishFacade facade = loc.getStateless(LOSReplenishFacade.class);
        facade.refillFixedLocations();
        //facade.createCronJob();
      }

    } catch (Throwable t) {
      ErrorManager em = ErrorManager.getDefault();
      //em.annotate(t, t.getMessage());
      em.notify(t);
    } finally {
      CursorControl.showNormalCursor();
    }

  }

  @Override
  public boolean isEnabled() {
    LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
    boolean result = login.checkRolesAllowed(getAllowedRoles());
    return result;
  }
}
