/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.create_avis.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.wmsprocesses.processes.create_avis.gui.gui_builder.AbstractCenterPanel;
import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.ClientItemDataLotFilteringComponent;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.lot.gui.component.LotOptionPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.model.Client;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class CenterPanel extends AbstractCenterPanel implements TopComponentListener{

    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
    TopComponentPanel topComponentPanel;
    ClientItemDataLotFilteringComponent clientItemDataLotFilteringComponent = null;     
    
//    private BOAutoFilteringComboBox<Client> clientComboBox = null;
//
//    private BOAutoFilteringComboBox<ItemData> itemDataComboBox = null;
    
    private final static Logger log = Logger.getLogger(CenterPanel.class.getName());

    private boolean initialized  = false;
    private BOAutoFilteringComboBox<Client> clientCombo;

    public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;

        try {
            clientItemDataLotFilteringComponent = new ClientItemDataLotFilteringComponent();
            clientCombo = new BOAutoFilteringComboBox<Client>();
            clientCombo.setBoClass(Client.class);
            clientCombo.initAutofiltering();
            clientCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "Client"));

            clientComboBoxPanel.add(getClientComboBox(), BorderLayout.CENTER);
            itemComboBoxPanel.add(getItemDataComboBox(), BorderLayout.CENTER);

        }
        catch(Exception e ) {
            e.printStackTrace();
        }




//        getAmountTextField().setEnabled(false);
        getAmountTextField().setMinimumValue(new BigDecimal(0), false);
        getAmountTextField().setMandatory(true);
        getAmountTextField().setColumns(8);
        getAmountTextField().getTextFieldLabel().setTitleText( NbBundle.getMessage(WMSProcessesBundleResolver.class,"CreateAvisCenterPanel.Amount"));
        
        getDeliveryTextField().setEnabled(true);
        getDeliveryTextField().setMandatory(true);
        getDeliveryTextField().setColumns(8);
        getDeliveryTextField().getTextFieldLabel().setTitleText( NbBundle.getMessage(WMSProcessesBundleResolver.class, "DELIVERY_DATE"));
        
    }
    
    private void initAutofiltering(){
    
        getClientComboBox().setEnabled(true);
        getClientComboBox().setMandatory(true);
        getClientComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"client"));
        
        getLotOptionPanel().initAutofiltering();
        
        getItemDataComboBox().setEnabled(true);
        getItemDataComboBox().setMandatory(true);
        getItemDataComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"itemData"));
        
        getItemDataComboBox().addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
               
                ItemData selItemData = getItemDataComboBox().getSelectedAsEntity();

                if(selItemData != null){
//                    getAmountTextField().setEnabled(true);
                    getAmountTextField().setUnitName(selItemData.getItemUnit().getUnitName());
                    getAmountTextField().setScale(selItemData.getScale());
                }
                else {
//                    getAmountTextField().setEnabled(false);
                }
                
            }
        });
        
        getLotComboBox().addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                
                if(getLotComboBox().getSelectedAsEntity() == null){
                    return;
                }
                
                ItemData selItemData = getLotComboBox().getSelectedAsEntity().getItemData();

                if(selItemData != null){
//                    getAmountTextField().setEnabled(true);
                    getAmountTextField().setUnitName(selItemData.getItemUnit().getUnitName());
                    getAmountTextField().setScale(selItemData.getScale());
                }
                else {
//                    getAmountTextField().setEnabled(false);
                }
                
            }
        });
        
//        try{
//            clientItemDataLotFilteringComponent = new ClientItemDataLotFilteringComponent(getClientComboBox(), getItemDataComboBox(), getLotComboBox());
//        } catch (Exception ex) {
//            ExceptionAnnotator.annotate(ex);
//        }
        
        validate();
    }

    public void createOrder() {
        
        if(!getClientComboBox().checkSanity()
            || !getItemDataComboBox().checkSanity()
            || !getAmountTextField().checkSanity()
            || !getDeliveryTextField().checkSanity()
            || !getLotOptionPanel().checkSanity())
        {
            return;
        }
        
        ManageInventoryFacade mi;
        
        try {
            mi = (ManageInventoryFacade) loc.getStateless(ManageInventoryFacade.class);
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        }
        try {
            
            boolean expire = getLotOptionPanel().getReplaceOldLotCheckBox().isSelected();
            
            Date dExpected = getDeliveryTextField().getDate();
            Date dTill = getValidToTextField().getDate();
            Date dFrom = getValidFromTextField().getDate();
            
            String lotName = null;
            if(getLotOptionPanel().isLotChoosen() 
               && getLotOptionPanel().getSelectedLot() != null)
            {
                lotName = getLotOptionPanel().getSelectedLot().getName();
            }
            else if(!getLotOptionPanel().isLotChoosen()){
                
                lotName = getLotNumberTextField().getText();
            }
            String comment = getCommentField().getText() == null ? "" : getCommentField().getText();
            
            boolean ret = mi.createAvis(getClientComboBox().getSelectedItem().getName(), 
                                        getItemDataComboBox().getSelectedItem().getName(), 
                                        lotName, 
                                        getAmountTextField().getValue(), 
                                        dExpected, 
                                        dTill, 
                                        dFrom, 
                                        expire, "", comment);
            
            if (!ret) {
                ExceptionAnnotator.annotate(new InventoryException(InventoryExceptionKey.CREATE_AVIS_FAILED, ""));
                return;
            }
            clear();
        } catch (Exception ex) {
            log.log(Level.INFO, ex.getMessage(), ex);
            ExceptionAnnotator.annotate(new InventoryException(InventoryExceptionKey.CREATE_AVIS_FAILED, ""));
            return;
        }
    }
    
     protected BOAutoFilteringComboBox<Lot> getLotComboBox() {  
        return ((LotOptionPanel)lotOptionPanel).getLotComboBox();
    }

    protected BOAutoFilteringComboBox<ItemData> getItemDataComboBox() {
        return clientItemDataLotFilteringComponent.getItemDataCombo();
//        if(itemDataComboBox == null){
//
//            itemDataComboBox = new BOAutoFilteringComboBox<ItemData>(ItemData.class);
//            itemComboBoxPanel.add(itemDataComboBox, BorderLayout.CENTER);
//        }
//
//        return itemDataComboBox;
    }
    

    // Implementation of TopComponentListener
    
    public void clear(){
        if(getItemDataComboBox() != null){
            getItemDataComboBox().clear();
        }
                
        getAmountTextField().setValue(new BigDecimal(0));
        getDeliveryTextField().setText("");
        
        getLotOptionPanel().clear();
        
        initDefaults();
        getClientComboBox().requestFocus();

    }

    protected BOAutoFilteringComboBox<Client> getClientComboBox() {
        return clientCombo;
//        if(clientComboBox == null){
//            clientComboBox = new BOAutoFilteringComboBox<Client>(Client.class);
//            clientComboBoxPanel.add(clientComboBox, BorderLayout.CENTER);
//        }
//
//        return clientComboBox;
    }
    
    public void componentOpened() {
        if (initialized){
            return;
        }
        initAutofiltering();
        initialized = true;
        clear();
    }

    public void componentClosed() {
    }

    public void componentActivated() {
        getClientComboBox().requestFocus();
    }

    public void componentDeactivated() {
    }

    public void componentHidden() {
    }

    public void componentShowing() {
    }
    
    private void initDefaults() {
        
        if( getClientComboBox().getSelectedItem() == null ) {
            BODTO<Client> client = loc.getDefaultClient();
            if( client != null ) {
                try {
                    getClientComboBox().clear();
                    getClientComboBox().addItem(client);
                }
                catch( Exception e) {}
            }
        }

        if( clientItemDataLotFilteringComponent != null ) {
            clientItemDataLotFilteringComponent.clear();
        }

        getDeliveryTextField().setDate( new Date() );
        getCommentField().setText("");
    }
}
