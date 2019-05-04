/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.listener;

import de.linogistix.common.system.AuthorizedResolver;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author artur
 */
public class TopComponentListenerImpl {

    /** Creates a new instance of TopComponentImpl */
    public TopComponentListenerImpl() {
    }

    public void informComponents(Container aTop, final InformType informType) {
        informRegisteredComponents(aTop, informType);
    }

    
    public void informRegisteredComponents(Container aTop, final InformType informType) {
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i] instanceof TopComponentListener) {
                    informComponentListener(informType, (TopComponentListener)comp[i]);
                }
                if (comp[i] instanceof JPanel) {
                    informRegisteredComponents((JPanel) comp[i], informType);
                }
            }
        }
    }

    public void informComponentListener(InformType informType, TopComponentListener comp) {
        if (informType == informType.COMPONENT_OPENED) {
                componentOpened(comp);
            } else if (informType == informType.COMPONENT_CLOSED) {
            componentClosed(comp);
        } else if (informType == informType.COMPONENT_ACTIVATED) {
            componentActivated(comp);
        } else if (informType == informType.COMPONENT_SHOWING) {
            componentShowing(comp);
        } else if (informType == informType.COMPONENT_HIDDEN) {
            componentHidden(comp);
        } else if (informType == informType.COMPONENT_DEACTIVATED) {
            componentDeactivated(comp);
        }
    }

    private void componentOpened(TopComponentListener comp) {
        if (AuthorizedResolver.getInstance().isAuthorized()) {
            comp.componentOpened();
        }
    }

    private void componentClosed(TopComponentListener comp) {
        if (AuthorizedResolver.getInstance().isAuthorized()) {
            comp.componentClosed();
        }
    }

    private void componentActivated(TopComponentListener comp) {
        if (AuthorizedResolver.getInstance().isAuthorized()) {
            comp.componentActivated();
        }
    }

    private void componentShowing(TopComponentListener comp) {
        if (AuthorizedResolver.getInstance().isAuthorized()) {
            comp.componentShowing();
        }
    }

    private void componentHidden(TopComponentListener comp) {
        if (AuthorizedResolver.getInstance().isAuthorized()) {
            comp.componentHidden();
        }
    }

    private void componentDeactivated(TopComponentListener comp) {
        if (AuthorizedResolver.getInstance().isAuthorized()) {
            comp.componentDeactivated();
        }
    }

    public enum InformType {

        COMPONENT_OPENED,
        COMPONENT_CLOSED,
        COMPONENT_ACTIVATED,
        COMPONENT_SHOWING,
        COMPONENT_HIDDEN,
        COMPONENT_DEACTIVATED
    }
}
