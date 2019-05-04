/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import de.linogistix.common.gui.component.windows.AppModalDialog;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class ServerDown {

    static boolean showDownModal = true;

    class ServerDownThread implements Runnable {

        boolean down;

        public ServerDownThread(final boolean visible) {
            this.down = visible;
            new Thread(this).start();
        }

        public void run() {
            showServerDownDialog(down && showDownModal);
        }
    }

    public void processServerDown(final boolean connected) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (connected == false) {
                    if (showDownModal && AppModalDialog.getInstance().isVisible() == false) {
                        new ServerDownThread(!connected);
                    }
                } else {
                    showServerDownDialog(false);
                }
            }
        });


    }

    private void showServerDownDialog(final boolean visible) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                AppModalDialog d = AppModalDialog.getInstance();
                d.setTitleText(NbBundle.getMessage(CommonBundleResolver.class, "Server unreachable"));
                d.setVisible(visible);
            }
        });
    }
}
