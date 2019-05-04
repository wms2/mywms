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
import de.linogistix.common.bobrowser.query.gui.gui_builder.AbstractBOQueryQuickSearchPanel;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class AutoCompletionQueryProvider implements BOQueryComponentProvider {

    private static final Logger log = Logger.getLogger(AutoCompletionQueryProvider.class.getName());
    
    public Method m;
    public BusinessObjectQueryRemote queryRemote;
    public AbstractBOQueryQuickSearchPanel quickSerachPanel;
    private ProviderChangeEventListener providerChangeEventListener;
    private BOQueryNode bOQueryNode;

    public AutoCompletionQueryProvider() {   }

    public AutoCompletionQueryProvider(BusinessObjectQueryRemote queryRemote) {
        this.queryRemote = queryRemote;
        try {
            this.m = this.queryRemote.getClass().getDeclaredMethod("autoCompletion", new Class[]{String.class, QueryDetail.class});
        } catch (Throwable ex) {
            log.severe(ex.getMessage());
            throw new RuntimeException();
        }
    }

    public BusinessObjectQueryRemote getQueryRemote() {
        return this.queryRemote;
    }

    public Method getMethod() {
        return this.m;
    }

    public DockingMode getDockingMode() {
//        return DockingMode.INLPLACE;
        // Or with own textfield in QueryPanel???
        return DockingMode.QUERYPANEL;
    }

    public JComponent createComponent() {

        if (quickSerachPanel == null) {
            quickSerachPanel = new AbstractBOQueryQuickSearchPanel();
        }

        quickSerachPanel.removeProviderChangeEventListeners();
        quickSerachPanel.addProviderChangeEventListener(providerChangeEventListener);

        return quickSerachPanel;

    }

    public WizardDescriptor createWizard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        if (quickSerachPanel != null) {
            return new Object[]{quickSerachPanel.getQuickSearchString(), detail};
        } else {
            return new Object[]{"", detail};
        }
    }

    public String getSearchString(){
        if(quickSerachPanel != null){
            return quickSerachPanel.getQuickSearchString();
        }
        else{
            return "";
        }
    }
    
    @Override
    public String toString() {
        String s;

        s = NbBundle.getMessage(CommonBundleResolver.class, "quickSearch");
        return s;
    }

    public BOQueryNode getBOQueryNode() {
        return bOQueryNode;
    }

    public void setBOQueryNode(BOQueryNode bOQueryNode) {
        this.bOQueryNode = bOQueryNode;
    }

    public ProviderChangeEventListener getProviderChangeEventListener() {
        return providerChangeEventListener;
    }

    public void setProviderChangeEventListener(ProviderChangeEventListener providerChangeEventListener) {
        this.providerChangeEventListener = providerChangeEventListener;
    }

    public Class[] getQueryMethodParameterTypes() {
       return new Class[]{String.class, QueryDetail.class};
    }
    
    
}
