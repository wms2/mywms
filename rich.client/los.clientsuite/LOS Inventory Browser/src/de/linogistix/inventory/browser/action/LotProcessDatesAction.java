/*
 * LotProcessDatesAction.java
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSExtinguishFacade;
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
public class LotProcessDatesAction extends SystemAction {

  public String[] getAllowedRoles() {
    return new String[]{Role.ADMIN.toString(), Role.INVENTORY_STR};
  }
  
  @Override
  public String getName() {
    return NbBundle.getMessage(InventoryBundleResolver.class, "processLotDates");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {

    NotifyDescriptor d = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.ReallyProcessLotDates"),
            NbBundle.getMessage(InventoryBundleResolver.class, "processLotDates"),
            NotifyDescriptor.OK_CANCEL_OPTION);

    if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
      return;
    }

    CursorControl.showWaitCursor();
    try {
      J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);

      LOSExtinguishFacade extFacade = loc.getStateless(LOSExtinguishFacade.class);
      extFacade.calculateLotLocks();

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
