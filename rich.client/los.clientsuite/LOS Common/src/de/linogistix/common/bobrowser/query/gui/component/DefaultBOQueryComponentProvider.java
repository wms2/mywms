/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.*;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class DefaultBOQueryComponentProvider implements BOQueryComponentProvider {

    DockingMode dockingMode = DockingMode.PLAIN;
    BusinessObjectQueryRemote queryRemote;
    Method m;
    Class bundleResolver = CommonBundleResolver.class;
    private BOQueryNode bOQueryNode;
    
    public DefaultBOQueryComponentProvider(BusinessObjectQueryRemote queryRemote, Method m) {

        this.queryRemote = queryRemote;
        this.m = m;
        this.dockingMode = dockingMode.PLAIN;
    }
    
    public DefaultBOQueryComponentProvider(BusinessObjectQueryRemote queryRemote, Method m, Class bundleResolver) {

        this.queryRemote = queryRemote;
        this.m = m;
        this.dockingMode = dockingMode.PLAIN;
        this.bundleResolver = bundleResolver;
    }

    public BusinessObjectQueryRemote getQueryRemote() {
        return this.queryRemote;
    }

    public DockingMode getDockingMode() {
        return this.dockingMode;
    }

    public JComponent createComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public WizardDescriptor createWizard() {
        throw new UnsupportedOperationException();
    }

    public Method getMethod() {
        return this.m;
    }

    public String getName() {
        return m.getName() != null ? m.getName() : "";
    }

    public Class[] getParameterTypes() {
        Class[] parameterTypes = m.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            return new Class[0];
        }

        return parameterTypes;
    }

    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        Class[] paramTypes = getParameterTypes();

        if (paramTypes.length == 0) {
            return new Object[0];
        }
        
        Object[] ret = new Object[]{detail};
        return ret;
        
    }

    @Override
    public String toString() {
        String s;
        
        s = NbBundle.getMessage(bundleResolver, m.getName());
        
        return s;
    }

    public BOQueryNode getBOQueryNode() {
        return bOQueryNode;
    }

    public void setBOQueryNode(BOQueryNode bOQueryNode) {
        this.bOQueryNode = bOQueryNode;
    }

    public void setProviderChangeEventListener(ProviderChangeEventListener providerChangeEventListener) {
        // not needed here
    }

    public Class[] getQueryMethodParameterTypes() {
       return this.m.getParameterTypes();
    }
    
    
    
}
