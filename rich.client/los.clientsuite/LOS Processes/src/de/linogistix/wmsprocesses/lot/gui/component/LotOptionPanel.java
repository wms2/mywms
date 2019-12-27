/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.lot.gui.component;

import de.linogistix.inventory.gui.component.controls.LotComboBoxModel;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.lot.gui.gui_builder.AbstractLotOptionPanel;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.mywms.model.Client;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class LotOptionPanel extends AbstractLotOptionPanel implements ActionListener{

    private static final String CHOOSE_LOT_ACTION_COMMAND = "CHOOSE_LOT_ACTION";
    
    private static final String CREATE_LOT_ACTION_COMMAND = "CREATE_LOT_ACTION";
    
    public LotOptionPanel(){
        this(false);
    }
    
    public LotOptionPanel(boolean alignHorizontal) {
        
        super(alignHorizontal);
        
        getLotNumberTextField().setEnabled(true);
        getLotNumberTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "LOT_NUMBER"));
        
        getValidFromTextField().setEnabled(true);
        getValidFromTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "Valid from"));
        
        getValidToTextField().setEnabled(true);
        getValidToTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "Valid till"));
        
        chooseLotButton.setActionCommand(CHOOSE_LOT_ACTION_COMMAND);
        chooseLotButton.addActionListener(this);
        
        createLotButton.setActionCommand(CREATE_LOT_ACTION_COMMAND);
        createLotButton.addActionListener(this);
    }
    
    public void initAutofiltering(){
        
        getLotComboBox().setEnabled(true);
        getLotComboBox().setMandatory(false);
        getLotComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "lot"));
        
        setChooseLotOptionEnabled(true);
        setCreateLotOptionEnabled(false);
        
        validate();
    }

    public void clear(){
        
        if(getLotComboBox() != null){
            getLotComboBox().clear();
        }
        
        getLotNumberTextField().setText("");
        getValidFromTextField().setText("");
        getValidToTextField().setText("");
    }

    public boolean isLotChoosen(){
        return chooseLotButton.isSelected();
    }
    
    public BODTO<Lot> getSelectedLot(){
        return getLotComboBox().getSelectedItem();
    }
    
    public void setChooseLotOptionEnabled(boolean enabled){
        
        chooseLotButton.setSelected(enabled);
        getLotComboBox().setEnabled(enabled);
        
        if(enabled){
            // if user chooses a lot, no input for a new lot number required
            getLotNumberTextField().setMandatory(false);
            
            getLotComboBox().setMandatory(this.isMandatory());
        }
        // if choose option disabled clear it
        else {
            // remember client and itemData restriction
            LotComboBoxModel lotComboModel = (LotComboBoxModel)getLotComboBox().getComboBoxModel();
            if( lotComboModel == null ) {
                try {
                lotComboModel = new LotComboBoxModel();
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
                getLotComboBox().setComboBoxModel(lotComboModel);
            }
            BODTO<Client> clientTO = lotComboModel.getClientTO();
            BODTO<ItemData> itemTO = lotComboModel.getItemDataTO();

            getLotComboBox().clear();
            
            lotComboModel.setClientTO(clientTO);
            lotComboModel.setItemDataTO(itemTO);
        }
        
    }
    
    public void setCreateLotOptionEnabled(boolean enabled){
        
        createLotButton.setSelected(enabled);
        getValidToTextField().setEnabled(enabled);
        getValidFromTextField().setEnabled(enabled);
        getLotNumberTextField().setEnabled(enabled);
        
        if(enabled){
            // if user creates a new lot, input for a new lot number required
            getLotNumberTextField().setMandatory(true);
        }
        // if create option is disabled clear it
        else {
            getLotNumberTextField().setText("");
            getValidFromTextField().setText("");
            getValidToTextField().setText("");
        }
        
    }
    
    public void lockCreateLotOption(boolean lockIt){
        createLotButton.setEnabled(!lockIt);
    }
    
    public boolean checkSanity(){
        
        if(isLotChoosen()){
            return getLotComboBox().checkSanity();
        }
        else{
            return getLotNumberTextField().checkSanity();
        }
    }
    
    /**
     *       Implementation of interface ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().equals(CHOOSE_LOT_ACTION_COMMAND)){
            setChooseLotOptionEnabled(true);
            setCreateLotOptionEnabled(false);
        }
        else{
            setChooseLotOptionEnabled(false);
            setCreateLotOptionEnabled(true);
        }
    }
}
