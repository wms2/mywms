/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractCreateWizardInfoPanel;

/**
 *
 * @author trautm
 */
public class CreateWizardInfoPanelUI extends AbstractCreateWizardInfoPanel{

    public CreateWizardInfoPanelUI(){
   
    }

    String getInfo() {
        return additionalInfoTextArea.getText();
    }

    void implReadSettings(Object settings) {
       //
    }

    void implStoreSettings(Object settings) {
        //
    }

    void setInfo(String info) {
        additionalInfoTextArea.setText(info);
    }

}
