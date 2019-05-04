/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractPositionWizardLastPanelUI;
import javax.swing.JComboBox;

/**
 *
 * @author trautm
 */
public class PositionWizardLastPanelUI extends AbstractPositionWizardLastPanelUI{

    public PositionWizardLastPanelUI(){
        
       
    }

    void initFromModel(PositionWizardModel model) {
        
        if(model.isSingleUnitLoad){
            getSameTextField().setText("1");
            getSameTextField().setEnabled(false);
        }
        else{
            getSameTextField().setText("" + model.sameCount);
            getSameTextField().setEnabled(true);
        }
        
        if (model.type != null ){
            getTypeComboBox().setSelectedItem(new TypeEntry(model.type));
        }
    }

    void implStoreSettings(Object settings) {
        //
    }
    
    JComboBox getTypeComboBox(){
        return jComboBoxType;
    }
}
