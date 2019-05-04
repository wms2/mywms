/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.querystockunits;

import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.event.ActionEvent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/**
 * Action which shows QueryStockUnit component.
 */
public class QueryStockUnitAction extends SystemAction {

  @Override
  public String getName() {
    return NbBundle.getMessage(ReportsBundleResolver.class, "CTL_QueryStockUnitAction");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {
    TopComponent win = QueryStockUnitTopComponent.findInstance();
    win.open();
    win.requestActive();
  }
}
