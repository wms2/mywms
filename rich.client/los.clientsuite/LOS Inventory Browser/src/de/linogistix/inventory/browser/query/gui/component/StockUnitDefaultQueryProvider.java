/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.query.gui.component;

import de.linogistix.inventory.gui.component.controls.ClientItemDataLotFilteringComponent;
import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.bobrowser.query.*;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DockingMode;
import de.linogistix.common.bobrowser.query.gui.component.ProviderChangeEventListener;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class StockUnitDefaultQueryProvider implements BOQueryComponentProvider {

    private static final Logger log = Logger.getLogger(StockUnitDefaultQueryProvider.class.getName());
    
    Method m;
    StockUnitQueryRemote queryRemote;
    ClientItemDataLotFilteringComponent cilComp;
    BOAutoFilteringComboBox<StorageLocation> slCombo;
    
    private ProviderChangeEventListener providerChangeEventListener;
    
    private BOQueryNode bOQueryNode;
    
    private JPanel queryPanel;
    
    public StockUnitDefaultQueryProvider(StockUnitQueryRemote queryRemote) {
        this.queryRemote = queryRemote;
        try {
            
            this.m = this.queryRemote.getClass().getDeclaredMethod("queryByDefault", new Class[]{BODTO.class, String.class,BODTO.class,BODTO.class,QueryDetail.class});
            
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
//        return DockingMode.INLPLACE;
        // Or with own textfield in QueryPanel???
        return DockingMode.QUERYPANEL;
    }

    public JComponent createComponent() {
        if (cilComp == null){
            try {
                cilComp = new ClientItemDataLotFilteringComponent();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
            slCombo = new BOAutoFilteringComboBox<StorageLocation>();
            slCombo.setBoClass(StorageLocation.class);
            slCombo.initAutofiltering();
            slCombo.setEditorLabelTitle(NbBundle.getMessage(
                    InventoryBundleResolver.class, "StockUnitDefaultQueryProvider.StorageLocation"));
            slCombo.addItemChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    cilComp.clear();
                }
            });

            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            this.queryPanel = new JPanel();
            
            this.queryPanel.add(cilComp.getItemDataCombo());
            this.queryPanel.add(slCombo);
            
            this.queryPanel.invalidate();
            this.queryPanel.validate();
        }
        
//        quickSerachPanel.removeProviderChangeEventListeners();
//        quickSerachPanel.addProviderChangeEventListener(providerChangeEventListener);
        
        return this.queryPanel;
    }

    public WizardDescriptor createWizard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        if (cilComp == null){
            return new Object[]{
            null, null, null, null,detail};
        } else{
            return new Object[]{
            null,
            cilComp.getItemData(),
            slCombo.getSelectedItem(),
            detail};
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

    public BOAutoFilteringComboBox<ItemData> getItemDataCombo(){
        return cilComp.getItemDataCombo();
    }
    
    public BOAutoFilteringComboBox<StorageLocation> getStorageLocationCombo(){
        return slCombo;
    }

    public Class[] getQueryMethodParameterTypes() {
        return this.m.getParameterTypes();
    }
    
    
}
