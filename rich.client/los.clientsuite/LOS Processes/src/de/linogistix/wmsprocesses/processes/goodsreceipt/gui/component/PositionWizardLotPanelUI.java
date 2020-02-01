/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.gui.component.controls.LOSDateFormattedTextField;
import de.linogistix.common.gui.component.controls.LOSTextField;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractPositionWizardLotPanelUI;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.product.ItemData;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.logging.Logger;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class PositionWizardLotPanelUI extends AbstractPositionWizardLotPanelUI{
   
    private static final Logger log = Logger.getLogger(PositionWizardLotPanelUI.class.getName());
    private PropertyChangeListener delegateTo;
    
    LOSTextField lotNumberField = new LOSTextField();
    LOSDateFormattedTextField bestBeforeField = new LOSDateFormattedTextField();

    public PositionWizardLotPanelUI(PropertyChangeListener delegate){
        
        this.delegateTo = delegate;
        
        lotNumberField.setEnabled(true);
        lotNumberField.setMandatory(false);
        lotNumberField.setColumns(8);
        lotNumberField.getTextFieldLabel().setTitleText( NbBundle.getMessage(WMSProcessesBundleResolver.class, "LOT_NUMBER"));
        getLotPanel().add(lotNumberField);

        bestBeforeField.setEnabled(true);
        bestBeforeField.setMandatory(false);
        bestBeforeField.setColumns(8);
        bestBeforeField.getTextFieldLabel().setTitleText( NbBundle.getMessage(WMSProcessesBundleResolver.class, "bestBefore"));
        getLotPanel().add(bestBeforeField);
        
    }
    
    void initValues(PositionWizardModel wm) {
                          
        String lotStr = wm.lotStr;
        
        
        if (lotStr != null && lotStr.length() > 0){
            lotNumberField.setText(lotStr);
        }
        else if (wm.selectedAdvice != null){
            ItemData item = wm.selectedAdvice.getItemData();

            lotNumberField.setText(wm.selectedAdvice.getLotNumber());
            lotNumberField.setMandatory(false);
            if( item!=null && item.isLotMandatory()) {
                lotNumberField.setMandatory(true);
            }

            bestBeforeField.setText("");
            if(wm.validTo!=null){
                bestBeforeField.setDate(wm.validTo);
            }
            bestBeforeField.setMandatory(false);
            if( item!=null && item.isBestBeforeMandatory()) {
                bestBeforeField.setMandatory(true);
            }
        }
         
        if (delegateTo != null) {
            delegateTo.propertyChange(null);
        }

     }
    
    public void clear() {
        lotNumberField.setText("");
        bestBeforeField.setText("");
    }

    public boolean validateLot(){
        return lotNumberField.checkSanity();
    }
    
    public String getLotNumber() {
        return lotNumberField.getText();
    }
    
    public Date getBestBefore() {
        return bestBeforeField.getDate();
    }

    public boolean validateBestBefore(){
        return bestBeforeField.checkSanity();
    }

}
