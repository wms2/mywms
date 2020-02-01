/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractPositionWizardULPanelUI;
import de.wms2.mywms.inventory.UnitLoadType;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class PositionWizardULPanelUI extends AbstractPositionWizardULPanelUI implements PropertyChangeListener{

    PropertyChangeListener delegateTo;
    
    public PositionWizardULPanelUI(PropertyChangeListener delegateTo){
         this.delegateTo = delegateTo;
    }

    @Override
    protected void postInit() {
        super.postInit();
        
        unitLoadLabelTextField.addItemChangeListener(this);
        
        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
      
        try {
            UnitLoadTypeQueryRemote ulTypeQuery = loc.getStateless(UnitLoadTypeQueryRemote.class);
            UnitLoadType def = ulTypeQuery.getDefaultUnitLoadType();
//            getUnitLoadTypeComboBox().addItem(new BODTO<UnitLoadType>(def.getId(), def.getVersion(), def.getName()));
            getUnitLoadTypeComboBox().getAutoFilteringComboBox().addItem(def.getName());
        } catch (J2EEServiceLocatorException ex) {
            Exceptions.printStackTrace(ex);
        }
                
        unitLoadTypeComboBox.addItemChangeListener(this);
        systemULButton.setSelected(true);
        propertyChange(null);
    }

    @Override
    public void clear() {
        unitLoadTypeComboBox.clear();
        getUnitLoadLabelTextField().setText("");
    }

    @Override
    protected void identifyULButtonItemStateChangedListener(ItemEvent evt) {
       if (evt.getStateChange() == evt.SELECTED){
           unitLoadLabelTextField.setEnabled(true);
           propertyChange(null);
       } 
    }

    @Override
    protected void systemULButtonItemStateChangedListener(ItemEvent evt) {
       if (evt.getStateChange() == evt.SELECTED){
           unitLoadLabelTextField.setEnabled(false);
           unitLoadLabelTextField.setText(null);
           propertyChange(null);
       }  
    }
    
    // ------------------------------------------------------------------------
    
    public boolean isSingleUnitLoad(){
        return !systemULButton.isSelected();
    }
    
    public void setSingleUnitLoad(boolean single){
        systemULButton.setSelected(!single);
        identifyULButton.setSelected(single);
    }
    
    public void requestUlFocus(){
        unitLoadLabelTextField.requestFocus();
    }
    
    void setUnitLoadLabel(String label){
        if (label == null){
            unitLoadLabelTextField.setText(null);
        } else{
            unitLoadLabelTextField.setText(label);
        }
    }

    String  getUnitLoadLabel(){
        return unitLoadLabelTextField.getText();
    }
    
    BODTO<UnitLoadType> getUnitLoadType(){
        return unitLoadTypeComboBox.getSelectedItem();
    }
    
    void setUnitLoadType(BODTO<UnitLoadType> type){
        unitLoadTypeComboBox.addItem(type);

    }

     // ------------------------------------------------------------------------
    
    boolean validateUnitLoadLabel() {
        if (systemULButton.isSelected()){
            return true;
        } else{
            return unitLoadLabelTextField.checkSanity();
        }
    }

    boolean validateUnitLoadType() {
        
        return getUnitLoadType() != null;
    }

    public void propertyChange(PropertyChangeEvent evt) {       
        
        if (delegateTo != null) delegateTo.propertyChange(evt);
    }
    
}
