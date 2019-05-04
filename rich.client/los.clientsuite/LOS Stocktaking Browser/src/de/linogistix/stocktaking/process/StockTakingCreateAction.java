/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.stocktaking.process.gui.StocktakingCreateTopComponent;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.awt.event.ActionEvent;
import org.mywms.globals.Role;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/**
 * Action which shows StockTakingTopComponent component.
 */
public class StockTakingCreateAction extends SystemAction {

  public String[] getAllowedRoles() {
    return new String[]{Role.ADMIN_STR, Role.FOREMAN_STR, Role.INVENTORY_STR};
  }

  @Override
  public String getName() {
    return NbBundle.getMessage(StocktakingBundleResolver.class, "CTL_StockTakingTopComponentAction");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {
    TopComponent win = StocktakingCreateTopComponent.findInstance();
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
