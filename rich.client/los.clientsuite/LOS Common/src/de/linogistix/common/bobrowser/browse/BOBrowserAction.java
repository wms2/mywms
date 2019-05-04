/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.browse;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.event.ActionEvent;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/**
 * Action which shows BOBrowser component.
 */
public class BOBrowserAction extends SystemAction {

  static final String ICON_PATH = "de/linogistix/common/res/icon/BOBrowser.png";

  public BOBrowserAction() {
    putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(BOBrowserAction.ICON_PATH, true)));
  }

  @Override
  public String getName() {
    return NbBundle.getMessage(CommonBundleResolver.class, "CTL_BOBrowserAction");
  }

  @Override
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  public void actionPerformed(ActionEvent evt) {
    TopComponent win = BOBrowserTopComponent.findInstance();
    win.open();
    win.requestActive();
  }

}
