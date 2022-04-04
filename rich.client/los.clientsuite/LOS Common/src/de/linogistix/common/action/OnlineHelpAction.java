/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.action;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

public class OnlineHelpAction extends SystemAction {

  public OnlineHelpAction() {
    setEnabled(true);
  }

  @Override
  public String getName() {
    return NbBundle.getMessage(CommonBundleResolver.class, "OnlineHelpAction.name");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {
    URL url;
    try {
      url = new URL(NbBundle.getMessage(CommonBundleResolver.class, "onlineHelpUrl"));
    } catch (Throwable t) {
      Exceptions.printStackTrace(t);
      try {
        url = new URL("https://mywms.org/en/community/");
      } catch (MalformedURLException ex) {
        Exceptions.printStackTrace(ex);
        return;
      }
    }
    URLDisplayer.getDefault().showURL(url);
  }
}
