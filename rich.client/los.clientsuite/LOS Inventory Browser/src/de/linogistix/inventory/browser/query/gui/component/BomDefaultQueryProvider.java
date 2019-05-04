/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.query.gui.component;

import de.linogistix.common.bobrowser.query.*;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DockingMode;
import de.linogistix.common.bobrowser.query.gui.component.ProviderChangeEventListener;
import de.linogistix.common.gui.component.controls.LOSTextField;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.LOSBomQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
public class BomDefaultQueryProvider implements BOQueryComponentProvider {

    private static final Logger log = Logger.getLogger(BomDefaultQueryProvider.class.getName());
    
    Method m;
    LOSBomQueryRemote queryRemote;
    LOSTextField masterField;
    LOSTextField childField;
    
    private ProviderChangeEventListener providerChangeEventListener;
    
    private BOQueryNode bOQueryNode;
    
    private JPanel queryPanel;
    
    public BomDefaultQueryProvider(LOSBomQueryRemote queryRemote) {
        this.queryRemote = queryRemote;
        try {
            
            this.m = this.queryRemote.getClass().getDeclaredMethod("queryByDefault", new Class[]{String.class, String.class,QueryDetail.class});
            
        } catch (Throwable ex) {
            log.log(Level.SEVERE,ex.getMessage(),ex);
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
        return DockingMode.QUERYPANEL;
    }

    public JComponent createComponent() {
        if( queryPanel == null ) {
            queryPanel = new JPanel();

            JLabel l = new JLabel(NbBundle.getMessage(InventoryBundleResolver.class, "parent")+":");
            queryPanel.add(l);
            masterField = new LOSTextField();
            masterField.setColumns(12);
            queryPanel.add(masterField);

            l = new JLabel(NbBundle.getMessage(InventoryBundleResolver.class, "child")+":");
            queryPanel.add(l);
            childField = new LOSTextField();
            childField.setColumns(12);
            queryPanel.add(childField);

            masterField.requestFocus();
        }
        
        return this.queryPanel;
    }

    public WizardDescriptor createWizard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        if( queryPanel == null ) {
            return new Object[]{null, null,detail};
        }
        else {
            return new Object[]{masterField.getText(), childField.getText(), detail};
        }
    }

    @Override
    public String toString() {
        String s;
        
        s= NbBundle.getMessage(InventoryBundleResolver.class, "StockUnitDefaultQueryProvider.DefaultSearch");
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
        return this.m.getParameterTypes();
    }
    
    
}
