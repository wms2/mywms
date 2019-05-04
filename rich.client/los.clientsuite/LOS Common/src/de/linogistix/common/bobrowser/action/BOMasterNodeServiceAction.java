/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 * Action which shows BOBrowser component.
 */
public class BOMasterNodeServiceAction extends NodeAction {

    private Class service;
    
    private String method;
    
    private String name;
    
    public BOMasterNodeServiceAction() {

    }

    public BOMasterNodeServiceAction(String name, String iconpath, Class service, String method) {        
        this.service = service;
        this.method  = method;
        this.name = name;
        if (iconpath != null){
            putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(iconpath, true)));
        }
    }

    @Override
    protected void performAction(Node[] arg0) {
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            Object instance = loc.getStateless(this.service);
//            Method m = instance.getClass().getMethod(this.method, new Object[0]);
            Method m = instance.getClass().getMethod(this.method, new Class[]{List.class});
            List args =  new ArrayList(arg0.length + 20);
            
            for (BOMasterNode n : (BOMasterNode[])arg0){
                args.add(n.getEntity());
            }
            m.invoke(instance, new Object[]{args});    
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    protected boolean enable(Node[] arg0) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, this.name);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
