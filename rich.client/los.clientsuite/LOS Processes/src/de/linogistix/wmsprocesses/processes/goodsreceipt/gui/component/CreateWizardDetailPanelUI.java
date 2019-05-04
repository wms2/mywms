/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.LOSComboBox;
import de.linogistix.common.gui.component.controls.LOSDateFormattedTextField;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractCreateWizardDetailPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.mywms.model.Client;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class CreateWizardDetailPanelUI extends AbstractCreateWizardDetailPanel implements PropertyChangeListener{

    BOAutoFilteringComboBox<Client> clientComboBox = null;
    
    public CreateWizardDetailPanelUI(boolean allowChangeOfClient){
        clientComboBox = new BOAutoFilteringComboBox<Client>(Client.class);
        clientComboBox.setEnabled(allowChangeOfClient);
        clientComboBox.setMandatory(true);
        clientComboBox.setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"client"));
        
        
        clientComboBoxPanel.add(clientComboBox, BorderLayout.CENTER);
        
        getDatePanel().setEnabled(true);
        getDatePanel().setMandatory(false);
        getDatePanel().getTextFieldLabel().setText(
                NbBundle.getMessage(WMSProcessesBundleResolver.class,"AbstractCenterPanel.dateLabel.text"));
        //clientComboBox.addItemChangeListener(this);
        gateComboBox.setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Gate"));


    }

    LOSComboBox getGateComboBox() {
        return gateComboBox;
    }

    void implReadSettings(Object settings) {
       //
    }

    void implStoreSettings(Object settings) {
        //
    }

    void initValues(BODTO<Client> client,BODTO<LOSStorageLocation> gate, Date date, String deliverer, String externNumber ) {
        LOSGoodsReceiptFacade goodsReceiptFacade;
        
        clear();
        try{
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            goodsReceiptFacade  = loc.getStateless(LOSGoodsReceiptFacade.class);
        } catch (Throwable t){
            ExceptionAnnotator.annotate(t);
            return;
        }
        
        if (client != null) clientComboBox.addItem(client);
        if (date != null) setDate(date);
        if (deliverer != null) setDeliverer(deliverer);
        if (externNumber != null) setExternNumber(externNumber);
         try{
            getGateComboBox().removeAllItems();
            List<BODTO<LOSStorageLocation>> gates = goodsReceiptFacade.getGoodsReceiptLocations();
            BODTO<LOSStorageLocation> selected = null;
                
            for (BODTO<LOSStorageLocation> dto : gates) {
                getGateComboBox().addItem(dto);
                if( dto.equals(gate) ) {
                    selected = dto;
                }
            }
            if (selected != null) {
                getGateComboBox().setSelectedItem(selected);
            }
        }catch(Exception ex){
            ExceptionAnnotator.annotate(ex);
        }
        if (date != null) setDate(date);
    }

    void setDeliverer(String deliverer) {
        delivererTextfield.setText(deliverer);
    }

    void setExternNumber(String externNumber) {
        externNumberTextfield.setText(externNumber);
    }

   
    boolean validateClient() {
        return clientComboBox.getSelectedItem() != null;
    }
    
    String getDeliverer(){
        return delivererTextfield.getText();
    }
    
    String getExternNumber(){
        return externNumberTextfield.getText();
    }
    
    Date getDate(){
        return getDatePanel().getDate();
    }
 
    void setDate(Date date) {
        getDatePanel().setText(new SimpleDateFormat("dd.MM.yyyy").format(date));
    }
 
    LOSDateFormattedTextField getDatePanel(){
        return (LOSDateFormattedTextField) datePanel;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        
    }

    private void clear() {
        clientComboBox.clear();
        gateComboBox.removeAllItems();
        delivererTextfield.setText("");
        externNumberTextfield.setText("");
    }


}
