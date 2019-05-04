/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Action which shows BOBrowser component.
 */
public class BOServiceAction extends AbstractAction {

    static final String ICON_PATH = "de/linogistix/bobrowser/res/icon/BOBrowser.png";

    private Class service;
    
    private String method;
    
    public BOServiceAction() {
    }

    public BOServiceAction(String name, String iconpath, Class service, String method) {
        super(NbBundle.getMessage(CommonBundleResolver.class, name));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(iconpath, true)));
        this.service = service;
        this.method  = method;
    }

    public void actionPerformed(ActionEvent evt) {
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            Object instance = loc.getStateless(this.service);
//            Method m = instance.getClass().getMethod(this.method, new Object[0]);
            Method m = instance.getClass().getMethod(this.method, new Class[0]);
            m.invoke(instance, new Object[0]);    
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }
}
