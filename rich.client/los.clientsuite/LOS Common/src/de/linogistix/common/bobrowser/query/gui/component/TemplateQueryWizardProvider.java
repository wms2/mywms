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
import de.linogistix.los.query.TemplateQuery;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class TemplateQueryWizardProvider implements BOQueryComponentProvider {

    private static final Logger log = Logger.getLogger(TemplateQueryWizardProvider.class.getName());
    DockingMode dockingMode = DockingMode.WIZARD_OWN;
    BusinessObjectQueryRemote queryRemote;
    Method m;
    QueryByTemplateWizard w;
    private BOQueryNode bOQueryNode;
    
    public TemplateQueryWizardProvider(
            BusinessObjectQueryRemote queryRemote,
            Method m) {
        this.m = m;
        this.queryRemote = queryRemote;
        this.dockingMode = dockingMode.WIZARD_OWN;
    }

    public BusinessObjectQueryRemote getQueryRemote() {
        return queryRemote;
    }

    public Method getMethod() {
        return this.m;
    }

    public DockingMode getDockingMode() {
        return this.dockingMode;
    }

    public JComponent createComponent() {
        throw new UnsupportedOperationException();
    }

    public WizardDescriptor createWizard() throws InstantiationException {        
        w = new QueryByTemplateWizard(bOQueryNode.getTemplateNode()); 
        return w;
    }

    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        TemplateQuery q = w.createTemplateQuery();
        return new Object[]{detail, q};
    }

    public BOQueryNode getBOQueryNode() {
        return bOQueryNode;
    }

    public void setBOQueryNode(BOQueryNode bOQueryNode) {
        this.bOQueryNode = bOQueryNode;
    }
    
    @Override
    public String toString() {
        String s;
        
        s = NbBundle.getMessage(CommonBundleResolver.class, "queryByTemplateHandles");
        
        return s;
    }

    public void setProviderChangeEventListener(ProviderChangeEventListener providerChangeEventListener) {
        // not needed here
    }

    public Class[] getQueryMethodParameterTypes() {
        return this.m.getParameterTypes();
    }

  
}
